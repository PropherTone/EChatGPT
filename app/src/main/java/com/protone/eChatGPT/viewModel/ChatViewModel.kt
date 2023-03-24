package com.protone.eChatGPT.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.api.model.ModelId
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.R
import com.protone.eChatGPT.activity.TAG
import com.protone.eChatGPT.bean.ChatHistory
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.repository.OpenAiHelper
import com.protone.eChatGPT.repository.dataBase.ChatDataBase
import com.protone.eChatGPT.repository.dataBase.chatHistoryDAO
import com.protone.eChatGPT.repository.userConfig
import com.protone.eChatGPT.utils.*
import kotlinx.coroutines.cancel
import java.io.File

class ChatViewModel : ViewModel() {

    companion object {
        const val CONVERSATION_TIME_OUT = 100L
        const val SAVE_SUCCESS = "SAVE_SUCCESS"
        const val SAVING = "ON_SAVING"
        const val SAVE_FAILED = "SAVE_FAILED"
    }

    private val openAi: OpenAiHelper by lazy { OpenAiHelper(userConfig.token) }

    private val _isConversation = MutableLiveData<Boolean>()
    val isConversation: LiveData<Boolean> get() = _isConversation

    private val _isSystem = MutableLiveData<Boolean>()
    val isSystem: LiveData<Boolean> get() = _isSystem

    private val _saveState = MutableLiveData<String>()
    val saveState: LiveData<String> get() = _saveState

    private val _conversationState by lazy { MutableLiveData<Boolean>() }
    val conversationState: LiveData<Boolean> get() = _conversationState

    private var chatId = -1

    private fun getChatId() = --chatId

    fun reverseIsSystem() {
        _isSystem.postValue(!(_isSystem.value ?: false))
    }

    fun reverseIsConversation() {
        _isConversation.postValue(!(_isConversation.value ?: false))
    }

    @OptIn(BetaOpenAI::class)
    fun chat(
        chatRole: ChatRole = ChatRole.User,
        chatList: Collection<ChatItem>,
        msg: String,
        callBack: (ChatItem) -> Unit
    ) {
        if (_conversationState.value == true) return
        _conversationState.postValue(true)
        viewModelScope.launchDefault chatJob@{
            val system = isSystem.value ?: false
            callBack(getUserChatItem(system, msg))
            if (system) {
                reverseIsSystem()
                return@chatJob
            }
            var item: ChatItem? = null
            try {
                doWithTimeout(timeoutMills = CONVERSATION_TIME_OUT, func = { refreshTimer ->
                    openAi.chat(
                        ChatCompletionRequest(
                            model = ModelId("gpt-3.5-turbo"),
                            generateChatMessages(chatList, chatRole, msg)
                        )
                    ).bufferCollect { completionChunk ->
                        refreshTimer()
                        Log.d(TAG, "chat: $completionChunk")
                        (item ?: ChatItem(
                            completionChunk.id,
                            ChatItem.ChatTarget.AI,
                            System.currentTimeMillis().toString()
                        ).also { item = it }).let { chatItem ->
                            chatItem.content = completionChunk.choices.joinToString { chunk ->
                                chunk.delta?.content ?: ""
                            }
                            callBack(chatItem)
                        }
                    }
                }) { cancel() }
            } catch (e: OpenAIHttpException) {
                callBack(
                    ChatItem(
                        getChatId().toString(),
                        ChatItem.ChatTarget.AI,
                        R.string.net_work_timeout.getString(),
                        System.currentTimeMillis().toString()
                    )
                )
            }
        }.invokeOnCompletion {
            _conversationState.postValue(false)
        }
    }

    fun saveConversation(data: Collection<ChatItem>) {
        _saveState.postValue(SAVING)
        viewModelScope.launchIO {
            data.listToJson(ChatItem::class.java).saveToFile(
                "${EApplication.app.filesDir}${File.separator}",
                "Chat-${System.currentTimeMillis()}"
            )?.let {
                chatHistoryDAO.insertChatHistory(ChatHistory(it, System.currentTimeMillis()))
                _saveState.postValue(SAVE_SUCCESS)
            } ?: _saveState.postValue(SAVE_FAILED)
        }.invokeOnCompletion {
            if (_saveState.value != SAVE_SUCCESS || _saveState.value != SAVE_FAILED) {
                _saveState.postValue(SAVE_FAILED)
            }
        }
    }

    private fun getUserChatItem(isSystem: Boolean, msg: String) = ChatItem(
        getChatId().toString(),
        ChatItem.ChatTarget.HUMAN.also { it.isSystem = isSystem },
        msg,
        System.currentTimeMillis().toString()
    )

    @OptIn(BetaOpenAI::class)
    private fun generateChatMessages(
        chatList: Collection<ChatItem>,
        chatRole: ChatRole = ChatRole.User,
        msg: String,
    ): List<ChatMessage> = when {
        _isConversation.value == true -> {
            chatList.map { chatItem ->
                ChatMessage(chatItem.chatRole, chatItem.content.toString())
            } as MutableList
        }
        chatList.takeIf { it.isNotEmpty() }?.last()?.target?.isSystem == true -> {
            chatList.last().let { chatItem ->
                listOf(ChatMessage(chatItem.chatRole, chatItem.content as String))
            } as MutableList
        }
        else -> mutableListOf()
    }.also { it.add(ChatMessage(chatRole, msg)) }

}
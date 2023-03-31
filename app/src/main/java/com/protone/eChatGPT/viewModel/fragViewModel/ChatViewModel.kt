package com.protone.eChatGPT.viewModel.fragViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.repository.OpenAiHelper
import com.protone.eChatGPT.repository.userConfig
import com.protone.eChatGPT.utils.*
import kotlinx.coroutines.cancel

class ChatViewModel : ViewModel() {

    companion object {
        const val CONVERSATION_TIME_OUT = 6000L
    }

    private val openAi: OpenAiHelper by lazy { OpenAiHelper(userConfig.token) }

    private val _isConversation = MutableLiveData<Boolean>()
    val isConversation: LiveData<Boolean> get() = _isConversation

    private val _isSystem = MutableLiveData<Boolean>()
    val isSystem: LiveData<Boolean> get() = _isSystem

    private val _conversationState by lazy { MutableLiveData<Boolean>() }
    val conversationState: LiveData<Boolean> get() = _conversationState

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
            val userChatItem = getUserChatItem(system, msg)
            callBack(userChatItem)
            if (system) {
                reverseIsSystem()
                return@chatJob
            }
            openAi.chat(
                userChatItem.id,
                ChatCompletionRequest(
                    model = ModelId("gpt-3.5-turbo"),
                    generateChatMessages(chatList, chatRole, msg)
                ),
                CONVERSATION_TIME_OUT,
                onTimeout = { cancel() }
            ) { callBack(it) }
        }.invokeOnCompletion {
            _conversationState.postValue(false)
        }
    }

    private fun getUserChatItem(isSystem: Boolean, msg: String) = ChatItem(
        openAi.getChatId().toString(),
        ChatItem.ChatTarget.HUMAN.also { it.isSystem = isSystem },
        msg,
        System.currentTimeMillis()
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
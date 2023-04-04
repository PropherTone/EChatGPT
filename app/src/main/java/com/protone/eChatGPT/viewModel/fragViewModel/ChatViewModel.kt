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

    private val systemChatCache by lazy { arrayOfNulls<String>(1) }
    private fun getSystemID(): String? = systemChatCache[0]?.also { systemChatCache[0] = null }
    private fun cacheSystemID(systemId: String?) {
        systemChatCache[0] = systemId
    }

    fun reverseIsSystem() {
        _isSystem.postValue(!(_isSystem.value ?: false))
    }

    fun reverseIsConversation() {
        _isConversation.postValue(!(_isConversation.value ?: false))
    }

    @OptIn(BetaOpenAI::class)
    fun chat(
        userId: String?,
        systemId: String?,
        chatRole: ChatRole = ChatRole.User,
        chatList: Collection<ChatItem>,
        msg: String,
        callBack: (ChatItem) -> Unit
    ) {
        if (_conversationState.value == true) return
        _conversationState.postValue(true)
        viewModelScope.launchDefault chatJob@{
            val userChatItem = getUserChatItem(userId, systemId, msg)
            callBack(userChatItem)
            if (isSystem.value == true) {
                cacheSystemID(userChatItem.id)
                reverseIsSystem()
                return@chatJob
            }
            openAi.chat(
                userChatItem.id,
                ChatCompletionRequest(
                    model = ModelId("gpt-3.5-turbo"),
                    generateChatMessages(userChatItem, chatList, chatRole, msg)
                ),
                CONVERSATION_TIME_OUT,
                onTimeout = { cancel() }
            ) { callBack(it) }
        }.invokeOnCompletion {
            _conversationState.postValue(false)
        }
    }

    private fun getUserChatItem(
        userId: String?,
        systemId: String?,
        msg: String
    ) = ChatItem(
        userId ?: openAi.getChatId().toString(),
        ChatItem.ChatTarget.HUMAN(systemId ?: getSystemID()),
        msg,
        System.currentTimeMillis()
    )

    @OptIn(BetaOpenAI::class)
    private fun generateChatMessages(
        item: ChatItem,
        chatList: Collection<ChatItem>,
        chatRole: ChatRole = ChatRole.User,
        msg: String,
    ): List<ChatMessage> = when {
        _isConversation.value == true -> {
            chatList.map { chatItem ->
                ChatMessage(chatItem.chatRole, chatItem.content.toString())
            } as MutableList
        }
        item.target is ChatItem.ChatTarget.HUMAN -> {
            chatList.find {
                it.id == item.target.systemId
            }?.let {
                mutableListOf(ChatMessage(it.chatRole, it.content as String))
            } ?: mutableListOf()
        }
        else -> mutableListOf()
    }.also { it.add(ChatMessage(chatRole, msg)) }

}
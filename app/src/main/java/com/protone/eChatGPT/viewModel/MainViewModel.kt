package com.protone.eChatGPT.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.api.model.ModelId
import com.protone.eChatGPT.R
import com.protone.eChatGPT.adapter.ChatListAdapter
import com.protone.eChatGPT.repository.OpenAiHelper
import com.protone.eChatGPT.repository.userConfig
import com.protone.eChatGPT.utils.bufferCollect
import com.protone.eChatGPT.utils.getString
import com.protone.eChatGPT.utils.launchDefault
import kotlinx.coroutines.delay

class MainViewModel : ViewModel() {

    private val openAi: OpenAiHelper by lazy { OpenAiHelper(userConfig.token) }

    private var chatId = -1
    private fun getChatId() = --chatId

    private val _conversationState by lazy { MutableLiveData<Boolean>() }
    val conversationState get() = _conversationState

    fun getUserChatItem(msg: String) = ChatListAdapter.ChatItem(
        getChatId().toString(),
        ChatListAdapter.ChatItem.ChatTarget.HUMAN,
        msg,
        System.currentTimeMillis().toString()
    )

    @OptIn(BetaOpenAI::class)
    fun chat(vararg msg: String, callBack: suspend (ChatListAdapter.ChatItem) -> Unit) {
        if (_conversationState.value == true) return
        conversationState.postValue(true)
        viewModelScope.launchDefault {
            var item: ChatListAdapter.ChatItem? = null
            try {
                throw OpenAIHttpException()
                openAi.chat(ChatCompletionRequest(
                    model = ModelId("gpt-3.5-turbo"),
                    msg.map {
                        ChatMessage(role = ChatRole.User, content = it)
                    }
                )).bufferCollect { completionChunk ->
                    (item ?: ChatListAdapter.ChatItem(
                        completionChunk.id,
                        ChatListAdapter.ChatItem.ChatTarget.AI,
                        System.currentTimeMillis().toString()
                    ).also { item = it }).let { chatItem ->
                        chatItem.content = completionChunk.choices.joinToString { chunk ->
                            chunk.delta?.content ?: ""
                        }
                        callBack(chatItem)
                    }
                }
                _conversationState.postValue(false)
            } catch (e: OpenAIHttpException) {
                ChatListAdapter.ChatItem(
                    getChatId().toString(),
                    ChatListAdapter.ChatItem.ChatTarget.AI,
                    System.currentTimeMillis().toString()
                ).let { chat ->
                    repeat(10) {
                        chat.content = R.string.net_work_timeout.getString()
                        callBack(chat)
                        delay(500)
                    }
                }
                _conversationState.postValue(false)
            }
        }
    }

}
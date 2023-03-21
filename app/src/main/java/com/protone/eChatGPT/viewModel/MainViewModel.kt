package com.protone.eChatGPT.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.protone.eChatGPT.activity.TAG
import com.protone.eChatGPT.repository.OpenAiHelper
import com.protone.eChatGPT.utils.bufferCollect
import com.protone.eChatGPT.utils.launchDefault
import kotlinx.coroutines.flow.Flow

class MainViewModel : ViewModel() {

    private var openAi: OpenAiHelper? = null

    fun init(token: String) {
        openAi = OpenAiHelper(token)
    }

    @OptIn(BetaOpenAI::class)
    fun chat(vararg msg: String, callBack: (ChatCompletionChunk) -> Unit) {
        viewModelScope.launchDefault {
            openAi?.chat(ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                msg.map {
                    ChatMessage(role = ChatRole.User, content = it)
                }
            ))?.bufferCollect { callBack(it) }
        }
    }

}
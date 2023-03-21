package com.protone.eChatGPT.repository

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.protone.eChatGPT.activity.TAG
import com.protone.eChatGPT.utils.bufferCollect
import kotlinx.coroutines.flow.Flow

class OpenAiHelper {

    private val api: OpenAI

    private constructor() {
        api = OpenAI("")
    }

    constructor(openAIConfig: OpenAIConfig) {
        api = OpenAI(openAIConfig)
    }

    constructor(token: String) {
        api = OpenAI(token)
    }

    @OptIn(BetaOpenAI::class)
    fun chat(chatCompletionRequest: ChatCompletionRequest) =
        api.chatCompletions(chatCompletionRequest)

}
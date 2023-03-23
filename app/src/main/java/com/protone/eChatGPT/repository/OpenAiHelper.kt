package com.protone.eChatGPT.repository

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig

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
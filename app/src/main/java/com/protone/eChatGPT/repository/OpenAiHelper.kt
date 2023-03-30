package com.protone.eChatGPT.repository

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.protone.eChatGPT.R
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.utils.bufferCollect
import com.protone.eChatGPT.utils.doWithTimeout
import com.protone.eChatGPT.utils.getString

class OpenAiHelper {

    private val api: OpenAI

    private var chatId = -1L

    fun getChatId() = --chatId

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
    suspend fun chat(
        userChatID: String,
        chatCompletionRequest: ChatCompletionRequest,
        timeout: Long,
        onTimeout: () -> Unit = {},
        callBack: (ChatItem) -> Unit
    ) {
        var item: ChatItem? = null
        try {
            doWithTimeout(
                timeoutMills = timeout,
                func = { refreshTimer ->
                    api.chatCompletions(chatCompletionRequest).bufferCollect { completionChunk ->
                        refreshTimer()
                        (item ?: ChatItem(
                            completionChunk.id,
                            ChatItem.ChatTarget.AI,
                            System.currentTimeMillis()
                        ).also { item = it }).let { chatItem ->
                            chatItem.addUsage(completionChunk.usage)
                            chatItem.content = completionChunk.choices.joinToString { chunk ->
                                chatItem.chatTag = ChatItem.ChatTag(chunk.finishReason ?: "")
                                chunk.delta?.content ?: ""
                            }
                            callBack(chatItem)
                        }
                    }
                }, onTimeout
            )
        } catch (e: OpenAIHttpException) {
            callBack(
                item ?: ChatItem(
                    getChatId().toString(),
                    ChatItem.ChatTarget.AI,
                    System.currentTimeMillis()
                ).also {
                    it.content =
                        "${if (it.content.isEmpty()) "" else "/n"}${R.string.net_work_timeout.getString()}"
                    it.chatTag = ChatItem.ChatTag.NetworkError
                }
            )
        }
    }

}
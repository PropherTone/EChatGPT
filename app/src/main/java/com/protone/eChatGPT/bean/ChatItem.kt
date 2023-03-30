package com.protone.eChatGPT.bean

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.core.Usage

data class ChatItem(val id: String, val target: ChatTarget, val time: Long) {

    @JvmInline
    value class ChatTag(val tag: String) {
        companion object {
            val Finish: ChatTag = ChatTag("stop")
            val Thinking: ChatTag = ChatTag("")
            val MaxLength: ChatTag = ChatTag("length")
            val NotSupport: ChatTag = ChatTag("content_filter")
            val NetworkError: ChatTag = ChatTag("Network_error")
        }
    }

    enum class ChatTarget(var isSystem: Boolean) {
        AI(false),
        HUMAN(false)
    }

    private val sb = StringBuilder()
    var content: CharSequence
        get() = sb.toString()
        set(value) {
            sb.append(value)
        }

    val usage = TokenUsage(0, 0, 0)

    var chatTag: ChatTag = ChatTag.Thinking

    constructor(
        id: String,
        target: ChatTarget,
        content: CharSequence,
        time: Long
    ) : this(id, target, time) {
        this.content = content
    }

    fun addUsage(usage: Usage?) {
        if (usage == null) return
        this.usage.prompt += usage.promptTokens ?: 0
        this.usage.completion += usage.completionTokens ?: 0
        this.usage.total = this.usage.prompt + this.usage.completion
    }

    @OptIn(BetaOpenAI::class)
    val chatRole
        get() = when (target) {
            ChatTarget.AI -> ChatRole.Assistant
            else -> if (target.isSystem) ChatRole.System else ChatRole.User
        }

    override fun toString(): String {
        return "ChatItem(id='$id', target=$target, time='$time', content=$content)"
    }

    data class TokenUsage(var total: Int, var prompt: Int, var completion: Int)

}
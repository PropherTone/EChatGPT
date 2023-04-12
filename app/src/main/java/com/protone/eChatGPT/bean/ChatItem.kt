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

    sealed class ChatTarget {
        data class AI(val userId: String) : ChatTarget()
        data class HUMAN(val systemId: String? = null, val isSystem: Boolean = false) : ChatTarget()
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
            is ChatTarget.AI -> ChatRole.Assistant
            is ChatTarget.HUMAN -> if (target.isSystem) ChatRole.System else ChatRole.User
        }

    override fun toString(): String {
        return "ChatItem(id='$id', target=$target, time=$time, sb=$sb, content=$content, usage=$usage, chatTag=$chatTag)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatItem

        if (id != other.id) return false
        if (target != other.target) return false
        if (time != other.time) return false
        if (content != other.content) return false
        if (usage != other.usage) return false
        if (chatTag != other.chatTag) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + usage.hashCode()
        result = 31 * result + chatTag.hashCode()

        return result
    }

    data class TokenUsage(var total: Int, var prompt: Int, var completion: Int)

}
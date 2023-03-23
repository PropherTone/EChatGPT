package com.protone.eChatGPT.bean

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatRole

data class ChatItem(val id: String, val target: ChatTarget, val time: String) {
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

    constructor(
        id: String,
        target: ChatTarget,
        content: CharSequence,
        time: String
    ) : this(id, target, time) {
        this.content = content
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

}
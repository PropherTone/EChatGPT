package com.protone.eChatGPT.messenger.event

sealed class ChatViewEvent : Event() {
    object OnSave : ChatViewEvent()
    object OnConversation : ChatViewEvent()
    object OnSystem : ChatViewEvent()
    data class OnSendMsg(
        val msg: String,
        val userId: String? = null,
        val systemId: String? = null
    ) : ChatViewEvent()
}
package com.protone.eChatGPT.messenger.event

sealed class SaveConversationEvent : Event() {
    object Finish : SaveConversationEvent()
    object Save : SaveConversationEvent()
}

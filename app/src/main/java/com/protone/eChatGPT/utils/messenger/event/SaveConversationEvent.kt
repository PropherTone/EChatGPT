package com.protone.eChatGPT.utils.messenger.event

sealed class SaveConversationEvent : Event() {
    object Finish : SaveConversationEvent()
    object Save : SaveConversationEvent()
}

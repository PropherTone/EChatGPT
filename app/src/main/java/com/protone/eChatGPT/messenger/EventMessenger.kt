package com.protone.eChatGPT.messenger

import com.protone.eChatGPT.messenger.event.Event
import kotlinx.coroutines.CoroutineScope

interface EventMessenger<E : Event> {
    fun send(event: E)
    suspend fun onEvent(callback:suspend (E) -> Unit)
}
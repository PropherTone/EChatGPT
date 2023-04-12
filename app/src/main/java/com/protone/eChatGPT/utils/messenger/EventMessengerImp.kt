package com.protone.eChatGPT.utils.messenger

import com.protone.eChatGPT.utils.messenger.event.Event
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class EventMessengerImp<E : Event> : EventMessenger<E> {

    private var viewEvent: Channel<E>? = null

    override fun send(event: E) {
        viewEvent?.trySend(event)
    }

    override suspend fun onEvent(callback: suspend (E) -> Unit) {
        Channel<E>(1).let {
            viewEvent = it
            it.receiveAsFlow().collect { event ->
                callback(event)
            }
        }
    }
}
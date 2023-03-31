package com.protone.eChatGPT.viewModel.activityViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protone.eChatGPT.adapter.ChatListAdapter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChatModViewModel : ViewModel() {

    sealed class ChatModEvent {
        object SaveConversation : ChatModEvent()
        object Back : ChatModEvent()
    }

    private val _eventFlow by lazy { MutableSharedFlow<ChatModEvent>() }
    val eventFlow get() = _eventFlow.asSharedFlow()

    val chatListAdapter by lazy { ChatListAdapter() }
    var normalSaveViewHeight = 0

    fun send(event: ChatModEvent) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

}
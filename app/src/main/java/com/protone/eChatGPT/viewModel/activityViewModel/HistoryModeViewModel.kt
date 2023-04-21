package com.protone.eChatGPT.viewModel.activityViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HistoryModeViewModel : ViewModel() {

    sealed class HistoryViewEvent {
        object Back : HistoryViewEvent()
        object Finish : HistoryViewEvent()
        data class ShowChatHistory(val group: String) : HistoryViewEvent()
        data class ContinueChat(val group: String) : HistoryViewEvent()
    }

    private val _eventFlow by lazy { MutableSharedFlow<HistoryViewEvent>() }
    val eventFlow get() = _eventFlow.asSharedFlow()

    fun send(event: HistoryViewEvent) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

}
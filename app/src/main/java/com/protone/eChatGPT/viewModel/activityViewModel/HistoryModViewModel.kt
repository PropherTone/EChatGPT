package com.protone.eChatGPT.viewModel.activityViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HistoryModViewModel : ViewModel() {

    sealed class HistoryViewEvent {
        object Finish : HistoryViewEvent()
        object ToDetail : HistoryViewEvent()
    }

    private val _eventFlow by lazy { MutableSharedFlow<HistoryViewEvent>() }
    val eventFlow get() = _eventFlow.asSharedFlow()

    fun send(event: HistoryViewEvent) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

}
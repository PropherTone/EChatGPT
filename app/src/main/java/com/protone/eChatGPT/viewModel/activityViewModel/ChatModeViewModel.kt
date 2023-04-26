package com.protone.eChatGPT.viewModel.activityViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.adapter.ChatListAdapter
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.repository.dataBase.chatHistoryDAO
import com.protone.eChatGPT.utils.launchDefault
import com.protone.eChatGPT.utils.listToJson
import com.protone.eChatGPT.utils.saveToFile
import com.protone.eChatGPT.utils.tryWithCatch
import com.protone.eChatGPT.utils.withDefaultContext
import com.protone.eChatGPT.utils.withIOContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class ChatModeViewModel : ViewModel() {

    sealed class ChatModViewEvent {
        data class SaveConversation(val startNewAfterSaved: Boolean = false) : ChatModViewEvent()
        object Back : ChatModViewEvent()
        object BackToMenu : ChatModViewEvent()
        object NewChat : ChatModViewEvent()
    }

    private val _eventFlow by lazy { MutableSharedFlow<ChatModViewEvent>() }
    val eventFlow get() = _eventFlow.asSharedFlow()

    val chatListAdapter by lazy { ChatListAdapter() }
    var normalSaveViewHeight = 0

    fun send(event: ChatModViewEvent) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

}
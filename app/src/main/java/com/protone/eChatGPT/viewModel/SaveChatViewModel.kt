package com.protone.eChatGPT.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protone.eChatGPT.bean.ChatHistory
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.repository.dataBase.chatHistoryDAO
import com.protone.eChatGPT.utils.launchIO
import com.protone.eChatGPT.utils.toJson

class SaveChatViewModel : ViewModel() {

    companion object {
        const val SAVE_SUCCESS = "SAVE_SUCCESS"
        const val SAVING = "ON_SAVING"
        const val SAVE_FAILED = "SAVE_FAILED"
        const val NAME_CONFLICT = "NAME_CONFLICT"
    }

    private val _saveState = MutableLiveData<String>()
    val saveState: LiveData<String> get() = _saveState

    fun saveConversation(name: String, data: Collection<ChatItem>) {
        _saveState.postValue(SAVING)
        viewModelScope.launchIO saveJob@{
            if (chatHistoryDAO.getChatHistoryByName(name) > 0) {
                _saveState.postValue(NAME_CONFLICT)
                return@saveJob
            }
            val millis = System.currentTimeMillis()
            data.map {
                ChatHistory(name, it.toJson(), millis)
            }.let {
                chatHistoryDAO.insertChatHistories(it)
            }
            _saveState.postValue(SAVE_SUCCESS)
        }.invokeOnCompletion {
            if (it != null) _saveState.postValue(SAVE_FAILED)
        }
    }

}
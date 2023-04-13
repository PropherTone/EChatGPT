package com.protone.eChatGPT.viewModel.fragViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.bean.ChatHistory
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.repository.dataBase.chatHistoryDAO
import com.protone.eChatGPT.utils.launchIO
import com.protone.eChatGPT.utils.listToJson
import com.protone.eChatGPT.utils.saveToFile

class SaveChatViewModel : ViewModel() {

    companion object {
        const val SAVE_SUCCESS = "SAVE_SUCCESS"
        const val SAVING = "ON_SAVING"
        const val SAVE_FAILED = "SAVE_FAILED"
        const val NAME_CONFLICT = "NAME_CONFLICT"
        const val EMPTY_CONTENT = "EMPTY_CONTENT"
    }

    private val _saveState = MutableLiveData<String>()
    val saveState: LiveData<String> get() = _saveState

    fun saveConversation(name: String, data: Collection<ChatItem>) {
        if (name.isEmpty()) {
            _saveState.postValue(EMPTY_CONTENT)
            return
        }
        _saveState.postValue(SAVING)
        viewModelScope.launchIO saveJob@{
            if (chatHistoryDAO.getChatHistoryCountByName(name) > 0) {
                _saveState.postValue(NAME_CONFLICT)
                return@saveJob
            }
            EApplication.app.filesDir?.absolutePath?.let { dir ->
                ChatHistory(
                    name,
                    data.listToJson(ChatItem::class.java).saveToFile(dir, name) ?: return@let null,
                    System.currentTimeMillis()
                )
            }?.let {
                chatHistoryDAO.insertChatHistory(it)
                _saveState.postValue(SAVE_SUCCESS)
            } ?: _saveState.postValue(SAVE_FAILED)
        }.invokeOnCompletion {
            if (it != null) _saveState.postValue(SAVE_FAILED)
        }
    }

}
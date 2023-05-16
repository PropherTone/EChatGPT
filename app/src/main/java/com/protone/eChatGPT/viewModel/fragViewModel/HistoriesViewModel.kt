package com.protone.eChatGPT.viewModel.fragViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.protone.eChatGPT.adapter.ChatHistoriesAdapter
import com.protone.eChatGPT.bean.ChatHistory
import com.protone.eChatGPT.repository.dataBase.chatHistoryDAO
import com.protone.eChatGPT.utils.launchIO

class HistoriesViewModel : ViewModel() {

    val chatHistoriesAdapter: ChatHistoriesAdapter by lazy { ChatHistoriesAdapter() }

    fun getHistoryPagingSource(pageSize: Int) = Pager(PagingConfig(pageSize)) {
        chatHistoryDAO.getPagingSource()
    }

    fun deleteChatHistory(
        chatHistories: MutableList<ChatHistory>,
        callback: (Collection<ChatHistory>) -> Unit
    ) {
        viewModelScope.launchIO {
            callback(
                chatHistories.also { it.removeAll(chatHistoryDAO.deleteChatHistories(chatHistories)) }
            )
        }
    }

}
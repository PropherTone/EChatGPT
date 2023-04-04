package com.protone.eChatGPT.viewModel.fragViewModel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.protone.eChatGPT.repository.dataBase.chatHistoryDAO

class HistoriesViewModel : ViewModel() {

    fun getHistoryPagingSource(pageSize: Int) = Pager(PagingConfig(pageSize)) {
        chatHistoryDAO.getPagingSource()
    }

}
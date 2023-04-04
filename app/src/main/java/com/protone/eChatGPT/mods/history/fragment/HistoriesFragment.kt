package com.protone.eChatGPT.mods.history.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.protone.eChatGPT.databinding.HistoriesFragmentBinding
import com.protone.eChatGPT.mods.BaseFragment
import com.protone.eChatGPT.utils.launchIO
import com.protone.eChatGPT.viewModel.activityViewModel.HistoryModViewModel
import com.protone.eChatGPT.viewModel.fragViewModel.HistoriesViewModel

class HistoriesFragment :
    BaseFragment<HistoriesFragmentBinding, HistoriesViewModel, HistoryModViewModel>() {
    override val viewModel: HistoriesViewModel by viewModels()
    override val activityViewModel: HistoryModViewModel by activityViewModels()

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): HistoriesFragmentBinding {
        return HistoriesFragmentBinding.inflate(inflater, container, false).apply {
            historyList.init()
        }
    }

    override fun HistoriesViewModel.init() {
        launchIO {
            getHistoryPagingSource(20).flow.collect {

            }
        }
    }

    private fun RecyclerView.init() {
        layoutManager = LinearLayoutManager(activity)
    }

}
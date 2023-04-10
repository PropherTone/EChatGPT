package com.protone.eChatGPT.mods.history

import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.protone.eChatGPT.R
import com.protone.eChatGPT.databinding.HistoryActivityBinding
import com.protone.eChatGPT.mods.BaseActivity
import com.protone.eChatGPT.utils.launchMain
import com.protone.eChatGPT.viewModel.activityViewModel.HistoryModViewModel

class HistoryActivity : BaseActivity<HistoryActivityBinding, HistoryModViewModel>() {
    override val viewModel: HistoryModViewModel by viewModels()

    private val navController by lazy { findNavController(R.id.history_nav_host) }

    override fun createView(): HistoryActivityBinding {
        return HistoryActivityBinding.inflate(layoutInflater)
    }

    override fun HistoryModViewModel.init() {
        launchMain {
            eventFlow.collect {
                when (it) {
                    HistoryModViewModel.HistoryViewEvent.ToDetail ->
                        navController.navigate(R.id.action_historiesFragment_to_historyFragment)
                    HistoryModViewModel.HistoryViewEvent.Finish -> finish()
                }
            }
        }
    }

}
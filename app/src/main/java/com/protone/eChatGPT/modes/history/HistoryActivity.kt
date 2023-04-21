package com.protone.eChatGPT.modes.history

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.protone.eChatGPT.R
import com.protone.eChatGPT.databinding.HistoryActivityBinding
import com.protone.eChatGPT.modes.BaseActivity
import com.protone.eChatGPT.modes.chat.ChatActivity
import com.protone.eChatGPT.modes.history.fragment.HistoryFragment
import com.protone.eChatGPT.utils.intent
import com.protone.eChatGPT.utils.launchMain
import com.protone.eChatGPT.viewModel.activityViewModel.HistoryModViewModel

class HistoryActivity : BaseActivity<HistoryActivityBinding, HistoryModViewModel>() {
    override val viewModel: HistoryModViewModel by viewModels()

    private val navController by lazy { findNavController(R.id.history_nav_host) }

    override fun createView(savedInstanceState: Bundle?): HistoryActivityBinding {
        return HistoryActivityBinding.inflate(layoutInflater)
    }

    override fun HistoryModViewModel.init(savedInstanceState: Bundle?) {
        launchMain {
            eventFlow.collect {
                when (it) {
                    HistoryModViewModel.HistoryViewEvent.Back -> navController.popBackStack()
                    HistoryModViewModel.HistoryViewEvent.Finish -> finish()
                    is HistoryModViewModel.HistoryViewEvent.ShowChatHistory -> {
                        navController.navigate(
                            R.id.action_historiesFragment_to_historyFragment,
                            Bundle().apply {
                                putString(HistoryFragment.CHAT_GROUP, it.group)
                            }
                        )
                    }
                    is HistoryModViewModel.HistoryViewEvent.ContinueChat -> {
                        navController.popBackStack()
                        startActivity(
                            ChatActivity::class.intent
                                .putExtra(ChatActivity.OPTION, "")
                                .putExtra(ChatActivity.GROUP, it.group)
                        )
                    }
                }
            }
        }
    }

}
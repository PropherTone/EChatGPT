package com.protone.eChatGPT.mods.menu

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.protone.eChatGPT.R
import com.protone.eChatGPT.bean.SwapAni
import com.protone.eChatGPT.databinding.MenuActivityBinding
import com.protone.eChatGPT.mods.BaseActivity
import com.protone.eChatGPT.mods.activities
import com.protone.eChatGPT.mods.chat.ChatActivity
import com.protone.eChatGPT.mods.history.HistoryActivity
import com.protone.eChatGPT.utils.intent
import com.protone.eChatGPT.viewModel.activityViewModel.MenuViewModel

class MenuActivity : BaseActivity<MenuActivityBinding, MenuViewModel>() {
    override val viewModel: MenuViewModel by viewModels()

    override fun createView(savedInstanceState: Bundle?): MenuActivityBinding {
        return MenuActivityBinding.inflate(layoutInflater).apply {
            back.isVisible = activities.contains(ChatActivity::class.java)
            initViewAction()
        }
    }

    override fun MenuViewModel.init(savedInstanceState: Bundle?) {

    }

    override fun getSwapAnim(): SwapAni {
        return SwapAni(R.anim.card_bot_in, R.anim.card_bot_out)
    }

    private fun MenuActivityBinding.initViewAction() {
        back.setOnClickListener {
            startActivity(ChatActivity::class.intent)
            overridePendingTransition(R.anim.card_bot_in, R.anim.card_bot_out)
        }
        completion.setOnClickListener {
            startActivity(ChatActivity::class.intent.putExtra(ChatActivity.OPTION, ""))
        }
        history.setOnClickListener {
            startActivity(HistoryActivity::class.intent)
        }
    }
}
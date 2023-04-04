package com.protone.eChatGPT.mods.menu

import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.protone.eChatGPT.R
import com.protone.eChatGPT.bean.SwapAni
import com.protone.eChatGPT.databinding.MenuActivityBinding
import com.protone.eChatGPT.mods.BaseActivity
import com.protone.eChatGPT.mods.activities
import com.protone.eChatGPT.mods.chat.ChatActivity
import com.protone.eChatGPT.utils.intent
import com.protone.eChatGPT.viewModel.activityViewModel.MenuViewModel

class MenuActivity : BaseActivity<MenuActivityBinding, MenuViewModel>() {
    override val viewModel: MenuViewModel by viewModels()

    override fun createView(): MenuActivityBinding {
        return MenuActivityBinding.inflate(layoutInflater).apply {
            back.isVisible = activities.contains(ChatActivity::class.java)
            initViewAction()
        }
    }

    override fun MenuViewModel.init() {

    }

    override fun getSwapAnim(): SwapAni {
        return SwapAni(R.anim.card_bot_in, R.anim.card_bot_out)
    }

    private fun MenuActivityBinding.initViewAction() {
        back.setOnClickListener {
            finish()
        }
        completion.setOnClickListener {
            startActivity(ChatActivity::class.intent)
        }
        history.setOnClickListener {

        }
    }
}
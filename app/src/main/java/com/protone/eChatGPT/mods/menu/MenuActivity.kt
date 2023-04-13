package com.protone.eChatGPT.mods.menu

import android.app.ActivityManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.protone.eChatGPT.R
import com.protone.eChatGPT.bean.SwapAni
import com.protone.eChatGPT.databinding.MenuActivityBinding
import com.protone.eChatGPT.mods.BaseActivity
import com.protone.eChatGPT.mods.activities
import com.protone.eChatGPT.mods.chat.ChatActivity
import com.protone.eChatGPT.mods.history.HistoryActivity
import com.protone.eChatGPT.mods.image.ImageActivity
import com.protone.eChatGPT.utils.intent
import com.protone.eChatGPT.viewModel.activityViewModel.MenuViewModel

class MenuActivity : BaseActivity<MenuActivityBinding, MenuViewModel>() {
    override val viewModel: MenuViewModel by viewModels()

    override fun createView(savedInstanceState: Bundle?): MenuActivityBinding {
        return MenuActivityBinding.inflate(layoutInflater).apply {
            backChat.isVisible = activities.contains(ChatActivity::class.java)
            initViewAction()
        }
    }

    override fun MenuViewModel.init(savedInstanceState: Bundle?) {

    }

    override fun onResume() {
        super.onResume()
        (getSystemService(ACTIVITY_SERVICE) as ActivityManager).apply {
            appTasks.mapNotNull { it.taskInfo?.topActivity?.className }.let {
                binding.backChat.isGone = !it.contains(ChatActivity::class.java.name)
                binding.backImage.isGone = !it.contains(ImageActivity::class.java.name)
            }
        }
    }

    override fun getSwapAnim(): SwapAni {
        return SwapAni(R.anim.card_bot_in, R.anim.card_bot_out)
    }

    private fun MenuActivityBinding.initViewAction() {
        backChat.setOnClickListener {
            startActivity(ChatActivity::class.intent)
            overridePendingTransition(R.anim.card_bot_in, R.anim.card_bot_out)
        }
        backImage.setOnClickListener {
            startActivity(ImageActivity::class.intent)
        }
        chat.setOnClickListener {
            startActivity(ChatActivity::class.intent.putExtra(ChatActivity.OPTION, ""))
        }
        history.setOnClickListener {
            startActivity(HistoryActivity::class.intent)
        }
    }
}
package com.protone.eChatGPT.activity

import android.animation.ObjectAnimator
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.protone.eChatGPT.R
import com.protone.eChatGPT.adapter.ChatListAdapter
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.SaveChatActivityLayoutBinding
import com.protone.eChatGPT.messenger.EventMessenger
import com.protone.eChatGPT.messenger.EventMessengerImp
import com.protone.eChatGPT.messenger.event.SaveConversationEvent
import com.protone.eChatGPT.utils.getString
import com.protone.eChatGPT.utils.isKeyboardActive
import com.protone.eChatGPT.utils.launchMain
import com.protone.eChatGPT.viewModel.SaveChatViewModel

class SaveConversationActivity : BaseActivity<SaveChatActivityLayoutBinding, SaveChatViewModel>(),
    EventMessenger<SaveConversationEvent> by EventMessengerImp() {

    companion object {
        const val ITEM_NORMAL_HEIGHT = "ITEM_NORMAL_HEIGHT"
        const val ITEM_HEIGHT = "ITEM_HEIGHT"
        const val CHAT_HISTORY = "CHAT_HISTORY"
    }

    override val viewModel: SaveChatViewModel by viewModels()

    private val chatListAdapter by lazy { ChatListAdapter() }

    override fun createView(): SaveChatActivityLayoutBinding {
        return SaveChatActivityLayoutBinding.inflate(layoutInflater).apply {
            save.updateLayoutParams {
                height = intent.getIntExtra(ITEM_HEIGHT, height)
            }
            save.post {
                if (!isKeyboardActive) {
                    save.updateLayoutParams {
                        height = intent.getIntExtra(ITEM_NORMAL_HEIGHT, height)
                    }
                }
            }
            chatList.init()
            back.setOnClickListener { send(SaveConversationEvent.Finish) }
            save.setOnClickListener { send(SaveConversationEvent.Save) }
        }
    }

    override fun SaveChatViewModel.init() {
        gainData.get<Collection<ChatItem>>(CHAT_HISTORY)?.let {
            chatListAdapter.setList(it)
        }
        saveState.observe(this@SaveConversationActivity) {
            when (it) {
                SaveChatViewModel.SAVE_SUCCESS -> finish()
                SaveChatViewModel.SAVING -> binding.saveProgress.isVisible = true
                SaveChatViewModel.SAVE_FAILED -> {
                    binding.saveProgress.isVisible = false
                    binding.state.errorWarning(getColor(R.color.main_color))
                    Toast.makeText(
                        this@SaveConversationActivity,
                        R.string.save_failed.getString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                SaveChatViewModel.NAME_CONFLICT -> {
                    binding.saveProgress.isVisible = false
                    binding.name.errorWarning(getColor(R.color.white))
                    Toast.makeText(
                        this@SaveConversationActivity,
                        R.string.name_conflict.getString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        launchMain {
            onEvent {
                when (it) {
                    SaveConversationEvent.Finish -> finish()
                    SaveConversationEvent.Save -> {
                        saveConversation(binding.name.text.toString(), chatListAdapter.getData())
                    }
                }
            }
        }
    }

    override fun getSwapAnim(): Pair<Int, Int> {
        return Pair(R.anim.card_in_rtl, R.anim.card_out_rtl)
    }

    private fun RecyclerView.init() {
        layoutManager = LinearLayoutManager(this@SaveConversationActivity)
        adapter = chatListAdapter
    }

    private fun View.errorWarning(originalColor: Int) {
        ObjectAnimator.ofArgb(
            this,
            "backgroundColor",
            originalColor,
            getColor(R.color.red),
            originalColor
        ).setDuration(500L).start()
    }

}
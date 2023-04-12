package com.protone.eChatGPT.mods.chat.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.protone.eChatGPT.R
import com.protone.eChatGPT.mods.BaseFragment
import com.protone.eChatGPT.databinding.SaveConversationFragmentBinding
import com.protone.eChatGPT.utils.messenger.EventMessenger
import com.protone.eChatGPT.utils.messenger.EventMessengerImp
import com.protone.eChatGPT.utils.messenger.event.SaveConversationEvent
import com.protone.eChatGPT.mods.BaseActivityFragment
import com.protone.eChatGPT.utils.getString
import com.protone.eChatGPT.utils.hideSoftInput
import com.protone.eChatGPT.utils.launchMain
import com.protone.eChatGPT.viewModel.activityViewModel.ChatModViewModel
import com.protone.eChatGPT.viewModel.fragViewModel.SaveChatViewModel

class SaveConversationFragment :
    BaseActivityFragment<SaveConversationFragmentBinding, SaveChatViewModel, ChatModViewModel>(),
    EventMessenger<SaveConversationEvent> by EventMessengerImp() {

    companion object {
        const val FINISH_OPTION = "Finish_option"
    }

    override val viewModel: SaveChatViewModel by viewModels()
    override val activityViewModel: ChatModViewModel by activityViewModels()

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): SaveConversationFragmentBinding {
        return SaveConversationFragmentBinding.inflate(inflater, container, false).apply {
            save.updateLayoutParams {
                height = activityViewModel.normalSaveViewHeight
            }
            chatList.init()
            back.setOnClickListener { send(SaveConversationEvent.Finish) }
            save.setOnClickListener { send(SaveConversationEvent.Save) }
        }
    }

    override fun SaveChatViewModel.init(savedInstanceState: Bundle?) {
        saveState.observe(this@SaveConversationFragment) {
            when (it) {
                SaveChatViewModel.SAVE_SUCCESS -> {
                    binding.name.hideSoftInput()
                    activityViewModel.send(ChatModViewModel.ChatModViewEvent.Back)
                }
                SaveChatViewModel.SAVING -> binding.saveProgress.isVisible = true
                SaveChatViewModel.SAVE_FAILED -> {
                    binding.saveProgress.isVisible = false
                    binding.state.errorWarning(R.color.main_color)
                    Toast.makeText(
                        context,
                        R.string.save_failed.getString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                SaveChatViewModel.NAME_CONFLICT -> {
                    binding.saveProgress.isVisible = false
                    binding.name.errorWarning(R.color.white)
                    Toast.makeText(
                        context,
                        R.string.name_conflict.getString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        launchMain {
            onEvent {
                when (it) {
                    SaveConversationEvent.Finish -> {
                        binding.name.hideSoftInput()
                        activityViewModel.send(ChatModViewModel.ChatModViewEvent.Back)
                    }
                    SaveConversationEvent.Save -> {
                        saveConversation(
                            binding.name.text.toString(),
                            activityViewModel.chatListAdapter.getData()
                        )
                    }
                }
            }
        }
    }

    private fun RecyclerView.init() {
        layoutManager = LinearLayoutManager(context)
        adapter = activityViewModel.chatListAdapter
    }

    private fun View.errorWarning(@ColorRes originalColor: Int) {
        ObjectAnimator.ofArgb(
            this,
            "backgroundColor",
            context.getColor(originalColor),
            context.getColor(R.color.red),
            context.getColor(originalColor)
        ).setDuration(500L).start()
    }

}
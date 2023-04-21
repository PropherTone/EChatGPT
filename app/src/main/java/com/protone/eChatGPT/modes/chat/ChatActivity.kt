package com.protone.eChatGPT.modes.chat

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.protone.eChatGPT.R
import com.protone.eChatGPT.databinding.ChatActivityBinding
import com.protone.eChatGPT.databinding.ContinueChatGuideBinding
import com.protone.eChatGPT.databinding.EnterTokenGuideLayoutBinding
import com.protone.eChatGPT.databinding.NewChatGuideDialogBinding
import com.protone.eChatGPT.modes.BaseActivity
import com.protone.eChatGPT.modes.chat.fragment.SaveConversationFragment
import com.protone.eChatGPT.modes.menu.MenuActivity
import com.protone.eChatGPT.repository.userConfig
import com.protone.eChatGPT.service.ChatService
import com.protone.eChatGPT.utils.intent
import com.protone.eChatGPT.utils.messenger.EventMessenger
import com.protone.eChatGPT.utils.messenger.EventMessengerImp
import com.protone.eChatGPT.utils.messenger.event.ChatViewEvent
import com.protone.eChatGPT.utils.requestBackgroundAlive
import com.protone.eChatGPT.utils.showPopupWindow
import com.protone.eChatGPT.viewModel.activityViewModel.ChatModeViewModel
import com.protone.eChatGPT.viewModel.fragViewModel.HistoryViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ChatActivity : BaseActivity<ChatActivityBinding, ChatModeViewModel>(),
    EventMessenger<ChatViewEvent> by EventMessengerImp() {

    companion object {
        const val OPTION = "Option"
        const val GROUP = "Group"
    }

    override val viewModel: ChatModeViewModel by viewModels()

    private val navController by lazy { findNavController(R.id.chat_nav_host) }

    override fun createView(savedInstanceState: Bundle?): ChatActivityBinding {
        requestBackgroundAlive()
        return ChatActivityBinding.inflate(layoutInflater).apply {
            root.post {
                var token = ""
                if (userConfig.token.isEmpty()) showPopupWindow(root) { pop ->
                    EnterTokenGuideLayoutBinding.inflate(layoutInflater).apply {
                        confirmBtn.setOnClickListener {
                            token = tokenInputBox.text.toString()
                            userConfig.token = token
                            startService(ChatService::class.intent)
                            pop.dismiss()
                        }
                        pop.setOnDismissListener { token.ifEmpty { finish() } }
                    }.root
                }
                else startService(ChatService::class.intent)
            }
        }
    }

    override fun ChatModeViewModel.init(savedInstanceState: Bundle?) {
        launch {
            eventFlow.collect { event ->
                if (lifecycle.currentState < Lifecycle.State.STARTED) return@collect
                when (event) {
                    is ChatModeViewModel.ChatModViewEvent.SaveConversation -> {
                        navController.navigate(
                            R.id.action_chatFragment_to_saveConversationActivity,
                            Bundle().apply {
                                putBoolean(
                                    SaveConversationFragment.FINISH_OPTION,
                                    event.startNewAfterSaved
                                )
                            })
                    }

                    ChatModeViewModel.ChatModViewEvent.Back -> navController.popBackStack()
                    ChatModeViewModel.ChatModViewEvent.BackToMenu -> {
                        navController
                        startActivity(MenuActivity::class.intent.also {
                            it.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        overridePendingTransition(R.anim.card_top_in, R.anim.card_top_out)
                    }

                    ChatModeViewModel.ChatModViewEvent.NewChat -> {
                        navController.popBackStack()
                        navController.navigate(R.id.chatFragment)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.apply {
            getStringExtra(OPTION)?.run {
                fun continueChat() {
                    getStringExtra(GROUP)?.let {
                        combineChat(
                            combine = { setChat(it) },
                            noNeed = {
                                viewModel.chatListAdapter.clear()
                                setChat(it)
                            }
                        )
                    } ?: viewModel.chatListAdapter.clear()
                    viewModel.send(ChatModeViewModel.ChatModViewEvent.NewChat)
                }
                if (viewModel.chatListAdapter.getData().isEmpty()) {
                    continueChat()
                    return@run
                }
                startNewChat(
                    goSave = {
                        viewModel.send(ChatModeViewModel.ChatModViewEvent.SaveConversation(true))
                    },
                    noNeed = { continueChat() }
                )

            }
        }
    }

    private inline fun startNewChat(
        crossinline goSave: () -> Unit,
        crossinline noNeed: () -> Unit
    ) {
        BottomSheetDialog(this).apply {
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            val dialogBinding = NewChatGuideDialogBinding.inflate(layoutInflater)
            dialogBinding.goSave.setOnClickListener {
                dismiss()
                goSave()
            }
            dialogBinding.noNeed.setOnClickListener {
                dismiss()
                noNeed()
            }
            setContentView(dialogBinding.root)
            show()
        }
    }

    private inline fun combineChat(
        crossinline combine: () -> Unit,
        crossinline noNeed: () -> Unit
    ) {
        BottomSheetDialog(this).apply {
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            val dialogBinding = ContinueChatGuideBinding.inflate(layoutInflater)
            dialogBinding.combine.setOnClickListener {
                dismiss()
                combine()
            }
            dialogBinding.noNeed.setOnClickListener {
                dismiss()
                noNeed()
            }
            setContentView(dialogBinding.root)
            show()
        }
    }

    private fun setChat(group: String) {
        HistoryViewModel().run {
            getList(group) {
                this@ChatActivity.launch { viewModel.chatListAdapter.setList(it) }
                viewModelScope.cancel()
            }
        }
    }

}
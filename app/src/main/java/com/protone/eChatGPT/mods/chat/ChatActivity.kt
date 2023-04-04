package com.protone.eChatGPT.mods.chat

import android.content.Intent
import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.protone.eChatGPT.R
import com.protone.eChatGPT.databinding.ChatActivityBinding
import com.protone.eChatGPT.messenger.EventMessenger
import com.protone.eChatGPT.messenger.EventMessengerImp
import com.protone.eChatGPT.messenger.event.ChatViewEvent
import com.protone.eChatGPT.mods.BaseActivity
import com.protone.eChatGPT.mods.chat.fragment.ChatFragment
import com.protone.eChatGPT.mods.menu.MenuActivity
import com.protone.eChatGPT.repository.userConfig
import com.protone.eChatGPT.service.ChatService
import com.protone.eChatGPT.utils.*
import com.protone.eChatGPT.viewModel.activityViewModel.ChatModViewModel
import kotlinx.coroutines.launch


class ChatActivity : BaseActivity<ChatActivityBinding, ChatModViewModel>(),
    EventMessenger<ChatViewEvent> by EventMessengerImp() {

    override val viewModel: ChatModViewModel by viewModels()

    private val navController by lazy { findNavController(R.id.chat_nav_host) }

    override fun createView(): ChatActivityBinding {
        requestBackgroundAlive()
        return ChatActivityBinding.inflate(layoutInflater).apply {
            root.post {
                var token = ""
                if (userConfig.token.isEmpty()) showGetTokenPop(root) {
                    token = it.toString()
                    userConfig.token = token
                    startService(ChatService::class.intent)
                }.apply { setOnDismissListener { token.ifEmpty { finish() } } }
                else startService(ChatService::class.intent)
            }
        }
    }

    override fun ChatModViewModel.init() {
        launch {
            eventFlow.bufferCollect { event ->
                when (event) {
                    ChatModViewModel.ChatModViewEvent.SaveConversation -> {
                        navController.navigate(R.id.action_chatFragment_to_saveConversationActivity)
                    }
                    ChatModViewModel.ChatModViewEvent.Back -> navController.popBackStack()
                    ChatModViewModel.ChatModViewEvent.BackToMenu -> {
                        startActivity(MenuActivity::class.intent.also {
                            it.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        overridePendingTransition(R.anim.card_top_in, R.anim.card_top_out)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.chatListAdapter.clear()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.chat_nav_host, ChatFragment())
            commit()
        }
    }

}
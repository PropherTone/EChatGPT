package com.protone.eChatGPT.mods.chat

import androidx.activity.viewModels
import androidx.navigation.findNavController
import com.protone.eChatGPT.R
import com.protone.eChatGPT.mods.BaseActivity
import com.protone.eChatGPT.databinding.ChatActivityBinding
import com.protone.eChatGPT.messenger.EventMessenger
import com.protone.eChatGPT.messenger.EventMessengerImp
import com.protone.eChatGPT.messenger.event.ChatViewEvent
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
                    ChatModViewModel.ChatModEvent.SaveConversation -> {
                        navController.navigate(R.id.action_chatFragment_to_saveConversationActivity)
                    }
                    ChatModViewModel.ChatModEvent.Back -> navController.popBackStack()
                }
            }
        }
    }

}
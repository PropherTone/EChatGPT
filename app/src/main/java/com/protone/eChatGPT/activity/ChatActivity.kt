package com.protone.eChatGPT.activity

import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.BetaOpenAI
import com.protone.eChatGPT.R
import com.protone.eChatGPT.adapter.ChatListAdapter
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.MainActivityBinding
import com.protone.eChatGPT.repository.userConfig
import com.protone.eChatGPT.service.ChatService
import com.protone.eChatGPT.utils.*
import com.protone.eChatGPT.viewModel.ChatViewModel
import kotlinx.coroutines.launch

class ChatActivity : BaseActivity<MainActivityBinding, ChatViewModel>() {
    override val viewModel: ChatViewModel by viewModels()

    private val chatListAdapter by lazy { ChatListAdapter() }

    @OptIn(BetaOpenAI::class)
    override fun createView(): MainActivityBinding {
        return MainActivityBinding.inflate(layoutInflater).apply {
            root.post {
                var token = ""
                if (userConfig.token.isEmpty()) showGetTokenPop(root) {
                    token = it.toString()
                    userConfig.token = token
                    startService(ChatService::class.intent)
                }.apply { setOnDismissListener { token.ifEmpty { finish() } } }
                else startService(ChatService::class.intent)
            }

            linkInput(chatList, chatInputBox)
            linkInput(root, chatInputBox)

            chatList.init()
            conversation.setOnClickListener {
                viewModel.reverseIsConversation()
            }
            chatSystem.setOnClickListener {
                viewModel.reverseIsSystem()
            }
            send.setOnClickListener {
                val msg = chatInputBox.text.toString()
                if (msg.isEmpty()) return@setOnClickListener
                chatInputBox.text.clear()
                viewModel.chat(msg = msg, chatList = chatListAdapter.getData()) { chatItem ->
                    if (chatItem.target == ChatItem.ChatTarget.HUMAN) {
                        launch { chatListAdapter.chatSent(chatItem) }
                        return@chat
                    }
                    launch { chatListAdapter.receive(chatItem) }
                }
            }
        }
    }

    override fun ChatViewModel.init() {
        conversationState.observe(this@ChatActivity) {
            binding.chatState.isVisible = it
        }
        isConversation.observe(this@ChatActivity) {
            binding.conversation.elevation = if (it) 0f else R.dimen.option_elevation.getDimension()
        }
        isSystem.observe(this@ChatActivity) {
            binding.chatSystem.elevation = if (it) {
                binding.chatInputBox.hint = R.string.describe_model_behavior.getString()
                0f
            } else {
                binding.chatInputBox.hint = R.string.enter_message.getString()
                R.dimen.option_elevation.getDimension()
            }
        }
    }

    private fun RecyclerView.init() {
        layoutManager = LinearLayoutManager(this@ChatActivity).apply {
            stackFromEnd = true
        }
        adapter = chatListAdapter
    }

}
package com.protone.eChatGPT.activity

import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.protone.eChatGPT.adapter.ChatListAdapter
import com.protone.eChatGPT.databinding.MainActivityBinding
import com.protone.eChatGPT.repository.userConfig
import com.protone.eChatGPT.service.ChatService
import com.protone.eChatGPT.utils.*
import com.protone.eChatGPT.viewModel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<MainActivityBinding, MainViewModel>() {
    override val viewModel: MainViewModel by viewModels()

    private val chatListAdapter by lazy { ChatListAdapter() }

    override fun createView(): MainActivityBinding {
        return MainActivityBinding.inflate(layoutInflater).apply {
            root.post {
                if (userConfig.token.isEmpty()) showGetTokenPop(root) {
                    userConfig.token = it.toString()
                    startService(ChatService::class.intent)
                }.apply { setOnDismissListener { userConfig.token.ifEmpty { finish() } } }
                else startService(ChatService::class.intent)
            }
            linkInput(chatList, chatInputBox)
            linkInput(root, chatInputBox)
            chatList.init()
            send.setOnClickListener {
                val msg = chatInputBox.text.toString()
                if (msg.isEmpty()) return@setOnClickListener
                chatInputBox.text.clear()
                chatListAdapter.chatSent(viewModel.getUserChatItem(msg))
                viewModel.chat(msg) { chatItem ->
                    launch { chatListAdapter.receive(chatItem) }
                }
            }
        }
    }

    override fun MainViewModel.init() {
        conversationState.observe(this@MainActivity) {
            binding.chatState.isVisible = it
        }
    }

    private fun RecyclerView.init() {
        layoutManager = LinearLayoutManager(this@MainActivity).apply {
            stackFromEnd = true
        }
        adapter = chatListAdapter
    }

}
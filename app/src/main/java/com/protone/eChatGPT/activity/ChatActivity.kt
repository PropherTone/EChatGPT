package com.protone.eChatGPT.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.BetaOpenAI
import com.protone.eChatGPT.R
import com.protone.eChatGPT.adapter.ChatListAdapter
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.ChatActivityBinding
import com.protone.eChatGPT.messenger.EventMessenger
import com.protone.eChatGPT.repository.userConfig
import com.protone.eChatGPT.service.ChatService
import com.protone.eChatGPT.utils.*
import com.protone.eChatGPT.messenger.EventMessengerImp
import com.protone.eChatGPT.messenger.event.ChatViewEvent
import com.protone.eChatGPT.viewModel.ChatViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ChatActivity : BaseActivity<ChatActivityBinding, ChatViewModel>(),
    EventMessenger<ChatViewEvent> by EventMessengerImp() {

    override val viewModel: ChatViewModel by viewModels()

    private val chatListAdapter by lazy { ChatListAdapter() }

    private var normalSaveViewHeight: Int = 0

    override fun createView(): ChatActivityBinding {
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
            chatInput.post {
                chatList.marginBottom(root.measuredHeight - chatInputBox.y.roundToInt())
                normalSaveViewHeight = binding.root.measuredHeight - binding.chatSave.y.roundToInt()
            }

            linkInput(chatList, chatInputBox)
            linkInput(root, chatInputBox)

            chatList.init()
            initViewAction()
        }
    }

    @OptIn(BetaOpenAI::class)
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
        launchMain {
            onEvent {
                when (it) {
                    is ChatViewEvent.OnSendMsg -> {
                        if (it.msg.isEmpty()) return@onEvent
                        binding.chatInputBox.text.clear()
                        chat(msg = it.msg, chatList = chatListAdapter.getData()) { chatItem ->
                            if (chatItem.target == ChatItem.ChatTarget.HUMAN) {
                                launch { chatListAdapter.chatSent(chatItem) }
                                return@chat
                            }
                            launch { chatListAdapter.receive(chatItem) }
                        }
                    }
                    ChatViewEvent.OnSave -> {
                        binding.chatSave.elevation = 0f
                        startSaveConversationActivityForResult(
                            chatListAdapter.getData(),
                            normalSaveViewHeight,
                            binding.root.measuredHeight - binding.chatSave.y.roundToInt()
                        ) {
                            binding.chatSave.elevation = R.dimen.option_elevation.getDimension()
                        }
                    }
                    ChatViewEvent.OnConversation -> reverseIsConversation()
                    ChatViewEvent.OnSystem -> reverseIsSystem()
                }
            }
        }
    }

    private fun RecyclerView.init() {
        layoutManager = LinearLayoutManager(this@ChatActivity).apply {
            stackFromEnd = true
        }
        adapter = chatListAdapter
    }

    private fun ChatActivityBinding.initViewAction() {
        chatSave.setOnClickListener {
            send(ChatViewEvent.OnSave)
        }
        conversation.setOnClickListener {
            send(ChatViewEvent.OnConversation)
        }
        chatSystem.setOnClickListener {
            send(ChatViewEvent.OnSystem)
        }
        send.setOnClickListener {
            send(ChatViewEvent.OnSendMsg(chatInputBox.text.toString()))
        }
    }

}
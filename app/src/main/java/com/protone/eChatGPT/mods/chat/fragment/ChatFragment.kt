package com.protone.eChatGPT.mods.chat.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.BetaOpenAI
import com.protone.eChatGPT.R
import com.protone.eChatGPT.mods.BaseFragment
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.ChatFragmentBinding
import com.protone.eChatGPT.messenger.EventMessenger
import com.protone.eChatGPT.messenger.EventMessengerImp
import com.protone.eChatGPT.messenger.event.ChatViewEvent
import com.protone.eChatGPT.utils.*
import com.protone.eChatGPT.viewModel.activityViewModel.ChatModViewModel
import com.protone.eChatGPT.viewModel.fragViewModel.ChatViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ChatFragment : BaseFragment<ChatFragmentBinding, ChatViewModel, ChatModViewModel>(),
    EventMessenger<ChatViewEvent> by EventMessengerImp() {

    override val viewModel: ChatViewModel by viewModels()
    override val activityViewModel: ChatModViewModel by activityViewModels()

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): ChatFragmentBinding {
        return ChatFragmentBinding.inflate(layoutInflater, container, false).apply {
            chatInput.post {
                chatList.marginBottom(root.measuredHeight - chatInputBox.y.roundToInt())
                activityViewModel.normalSaveViewHeight =
                    binding.root.measuredHeight - binding.chatSave.y.roundToInt()
            }

            root.context.linkInput(chatList, chatInputBox)
            root.context.linkInput(root, chatInputBox)

            chatList.init()
            initViewAction()
        }
    }

    @OptIn(BetaOpenAI::class)
    override fun ChatViewModel.init() {
        conversationState.observe(this@ChatFragment) {
            binding.chatState.isVisible = it
        }
        isConversation.observe(this@ChatFragment) {
            binding.conversation.elevation = if (it) 0f else R.dimen.option_elevation.getDimension()
        }
        isSystem.observe(this@ChatFragment) {
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
                        activityViewModel.apply {
                            chat(msg = it.msg, chatList = chatListAdapter.getData()) { chatItem ->
                                if (chatItem.target == ChatItem.ChatTarget.HUMAN) {
                                    launch { chatListAdapter.chatSent(chatItem) }
                                    return@chat
                                }
                                launch { chatListAdapter.receive(chatItem) }
                            }
                        }
                    }
                    ChatViewEvent.OnSave -> {
                        binding.chatSave.elevation = 0f
                        activityViewModel.send(ChatModViewModel.ChatModEvent.SaveConversation)
                        binding.chatSave.elevation = R.dimen.option_elevation.getDimension()
                    }
                    ChatViewEvent.OnConversation -> reverseIsConversation()
                    ChatViewEvent.OnSystem -> reverseIsSystem()
                }
            }
        }
    }

    private fun RecyclerView.init() {
        layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true
        }
        adapter = activityViewModel.chatListAdapter
    }

    private fun ChatFragmentBinding.initViewAction() {
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
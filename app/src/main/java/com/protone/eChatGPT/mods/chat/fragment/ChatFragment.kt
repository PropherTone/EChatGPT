package com.protone.eChatGPT.mods.chat.fragment

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aallam.openai.api.BetaOpenAI
import com.protone.eChatGPT.R
import com.protone.eChatGPT.adapter.ChatListAdapter
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.ChatDetailLayoutBinding
import com.protone.eChatGPT.databinding.ChatFragmentBinding
import com.protone.eChatGPT.messenger.EventMessenger
import com.protone.eChatGPT.messenger.EventMessengerImp
import com.protone.eChatGPT.messenger.event.ChatViewEvent
import com.protone.eChatGPT.mods.BaseFragment
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
                        activityViewModel.apply {
                            chat(
                                userId = it.userId,
                                systemId = it.systemId,
                                msg = it.msg,
                                chatList = chatListAdapter.getData()
                            ) { chatItem ->
                                if (chatItem.target is ChatItem.ChatTarget.HUMAN) {
                                    launch { chatListAdapter.chatSent(chatItem) }
                                    return@chat
                                }
                                launch { chatListAdapter.receive(chatItem) }
                            }
                        }
                    }
                    ChatViewEvent.OnSave -> {
                        binding.chatSave.elevation = 0f
                        activityViewModel.send(ChatModViewModel.ChatModViewEvent.SaveConversation)
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
        adapter = activityViewModel.chatListAdapter.apply {
            itemEvent = object : ChatListAdapter.ItemEvent {
                override fun onRootLongClick(item: ChatItem) {
                    (activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?)
                        ?.setPrimaryClip(ClipData.newPlainText("chat", item.content))
                }

                override fun onRetry(item: ChatItem): Boolean {
                    if (item.target !is ChatItem.ChatTarget.AI) return false
                    return activityViewModel.chatListAdapter
                        .getChatItemByID(item.target.userId)
                        ?.let {
                            send(
                                ChatViewEvent.OnSendMsg(
                                    it.content.toString(),
                                    it.id,
                                    if (it.target is ChatItem.ChatTarget.HUMAN && it.target.systemId != null)
                                        it.target.systemId
                                    else null
                                )
                            )
                            true
                        } == true
                }

                override fun onDetail(item: ChatItem) {
                    val detailLayoutBinding =
                        ChatDetailLayoutBinding.inflate(layoutInflater).apply {
                            chatContent.text = item.content
                            totalTokens.text = String.format(
                                R.string.total_token_count.getString(),
                                item.usage.total.toString()
                            )
                            promptTokens.text = String.format(
                                R.string.prompt_token_count.getString(),
                                item.usage.prompt.toString()
                            )
                            completionTokens.text = String.format(
                                R.string.completion_token_count.getString(),
                                item.usage.completion.toString()
                            )
                        }
                    AlertDialog.Builder(context)
                        .setView(detailLayoutBinding.root)
                        .create()
                        .show()
                }

            }
        }
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
        openMenu.setOnClickListener {
            activityViewModel.send(ChatModViewModel.ChatModViewEvent.BackToMenu)
        }
        send.setOnClickListener {
            val msg = chatInputBox.text.toString()
            if (msg.isEmpty()) return@setOnClickListener
            binding.chatInputBox.text.clear()
            send(ChatViewEvent.OnSendMsg(msg))
        }
    }
}
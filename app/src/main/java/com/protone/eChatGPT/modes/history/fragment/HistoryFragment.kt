package com.protone.eChatGPT.modes.history.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.protone.eChatGPT.adapter.ChatListEditAdapter
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.HistoryFragmentBinding
import com.protone.eChatGPT.modes.BaseActivityFragment
import com.protone.eChatGPT.viewModel.activityViewModel.HistoryModeViewModel
import com.protone.eChatGPT.viewModel.fragViewModel.HistoryViewModel
import kotlinx.coroutines.launch

class HistoryFragment :
    BaseActivityFragment<HistoryFragmentBinding, HistoryViewModel, HistoryModeViewModel>() {

    companion object {
        const val CHAT_GROUP = "CHAT_GROUP"
    }

    override val viewModel: HistoryViewModel by viewModels()

    override val activityViewModel: HistoryModeViewModel by activityViewModels()

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HistoryFragmentBinding {
        return HistoryFragmentBinding.inflate(inflater, container, false).apply {
            chatList.init()
            back.setOnClickListener {
                activityViewModel.send(HistoryModeViewModel.HistoryViewEvent.Back)
            }
            goChat.setOnClickListener {
                activityViewModel.send(
                    HistoryModeViewModel.HistoryViewEvent.ContinueChat(
                        arguments?.getString(CHAT_GROUP) ?: return@setOnClickListener
                    )
                )
            }
        }
    }

    override fun HistoryViewModel.init(savedInstanceState: Bundle?) {
        arguments?.let { bundle ->
            getList(bundle.getString(CHAT_GROUP) ?: return@let) {
                launch { chatListEditAdapter.setList(it) }
            }
        }
    }

    private fun RecyclerView.init() {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = viewModel.chatListEditAdapter
        viewModel.chatListEditAdapter.setEditItemEvent(object : ChatListEditAdapter.EditItemEvent {
            override fun delete(position: Int, chatItem: ChatItem) {
                viewModel.deleteChat(position, chatItem) {
                    launch {
                        it.forEach { item -> viewModel.chatListEditAdapter.remove(item) }
                    }
                }
            }
        })
    }

}
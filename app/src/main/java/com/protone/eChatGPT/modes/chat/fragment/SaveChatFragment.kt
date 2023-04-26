package com.protone.eChatGPT.modes.chat.fragment

import androidx.fragment.app.activityViewModels
import com.protone.eChatGPT.adapter.ChatListEditAdapter
import com.protone.eChatGPT.modes.SaveHistoryFragment
import com.protone.eChatGPT.viewModel.activityViewModel.ChatModeViewModel

class SaveChatFragment : SaveHistoryFragment<ChatModeViewModel>() {
    override fun getAdapter() = ChatListEditAdapter().apply {
        setList(activityViewModel.chatListAdapter.getData())
    }

    override val activityViewModel: ChatModeViewModel by activityViewModels()
}
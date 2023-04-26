package com.protone.eChatGPT.viewModel.fragViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.adapter.ChatListEditAdapter
import com.protone.eChatGPT.adapter.deleteChat
import com.protone.eChatGPT.bean.ChatHistory
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.repository.dataBase.chatHistoryDAO
import com.protone.eChatGPT.utils.*
import com.protone.eChatGPT.viewModel.activityViewModel.ChatModeViewModel
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    val chatListEditAdapter by lazy { ChatListEditAdapter() }

    private var chatHistory: ChatHistory? = null

    fun getList(group: String, callback: (Collection<ChatItem>) -> Unit) {
        viewModelScope.launchIO {
            callback(
                (chatHistoryDAO.getChatHistoryByName(group).also { chatHistory = it }
                    .content
                    .getFileContent()
                    ?.jsonToList(ChatItem::class.java) as MutableList<ChatItem>?)
                    ?: listOf()
            )
        }
    }

    fun deleteChatHistory(
        position: Int,
        chatItem: ChatItem,
        callback: (Collection<ChatItem>) -> Unit
    ) {
        chatHistory?.let { history ->
            viewModelScope.launch {
                chatListEditAdapter.deleteChat(position, chatItem) {
                    EApplication.app.filesDir?.absolutePath?.let { dir ->
                        val listTmp = mutableListOf<ChatItem>()
                        listTmp.addAll(chatListEditAdapter.getData())
                        listTmp.removeAll(it)
                        val json = listTmp.listToJson(ChatItem::class.java)
                        viewModelScope.launchIO {
                            json.saveToFile(dir, history.group)?.let { content ->
                                chatHistoryDAO.updateChatHistory(
                                    history.copy(
                                        id = history.id,
                                        group = history.group,
                                        content = content,
                                        date = history.date
                                    )
                                )
                            }
                            callback(it)
                        }
                    }
                }
            }
        }
    }

}
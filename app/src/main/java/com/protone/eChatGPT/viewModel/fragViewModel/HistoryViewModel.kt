package com.protone.eChatGPT.viewModel.fragViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.adapter.ChatListEditAdapter
import com.protone.eChatGPT.bean.ChatHistory
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.repository.dataBase.chatHistoryDAO
import com.protone.eChatGPT.utils.*

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

    fun deleteChat(position: Int, chatItem: ChatItem, callback: (Collection<ChatItem>) -> Unit) {
        chatHistory?.let { history ->
            viewModelScope.launchDefault {
                val deleteList = mutableListOf<ChatItem>()
                when (chatItem.target) {
                    is ChatItem.ChatTarget.AI -> chatListEditAdapter.getChatItemByID(chatItem.target.userId)
                        ?.let { userItem ->
                            findSystemItem(userItem)?.let { system -> deleteList.add(system) }
                            deleteList.add(userItem)
                            deleteList.add(chatItem)
                        }
                    is ChatItem.ChatTarget.HUMAN -> {
                        if (chatItem.target.isSystem) {
                            deleteList.add(chatItem)
                            getItem(position + 1) { item ->
                                item.target is ChatItem.ChatTarget.HUMAN && item.target.systemId == chatItem.id
                            }?.let { userItem ->
                                deleteList.add(userItem)
                                getItem(position + 2) { item ->
                                    item.target is ChatItem.ChatTarget.AI && item.target.userId == userItem.id
                                }?.let { aiItem ->
                                    deleteList.add(aiItem)
                                }
                            }
                        } else {
                            findSystemItem(chatItem)?.let { system -> deleteList.add(system) }
                            deleteList.add(chatItem)
                            getItem(position + 1) { item ->
                                item.target is ChatItem.ChatTarget.AI && item.target.userId == chatItem.id
                            }?.let { aiItem ->
                                deleteList.add(aiItem)
                            }
                        }
                    }
                }
                EApplication.app.filesDir?.absolutePath?.let { dir ->
                    val listTmp = mutableListOf<ChatItem>()
                    listTmp.addAll(chatListEditAdapter.getData())
                    listTmp.removeAll(deleteList)
                    val json = listTmp.listToJson(ChatItem::class.java)
                    withIOContext {
                        json.saveToFile(history.group, dir)?.let { content ->
                            chatHistoryDAO.updateChatHistory(
                                history.copy(
                                    id = history.id,
                                    group = history.group,
                                    content = content,
                                    date = history.date
                                )
                            )
                        }
                        callback(deleteList)
                    }
                }
            }
        }
    }

    private fun findSystemItem(userItem: ChatItem): ChatItem? {
        return if (userItem.target is ChatItem.ChatTarget.HUMAN && userItem.target.systemId != null) {
            chatListEditAdapter.getChatItemByID(userItem.target.systemId)
        } else null
    }

    private fun getItem(position: Int, predicate: (ChatItem) -> Boolean): ChatItem? = tryWithCatch {
        val item = chatListEditAdapter.getChatItem(position)
        if (predicate(item)) {
            item
        } else null
    }


}
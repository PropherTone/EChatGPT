package com.protone.eChatGPT.adapter

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.ChatItemLayoutBinding
import com.protone.eChatGPT.databinding.DeleteDialogLayoutBinding
import com.protone.eChatGPT.utils.layoutInflater
import com.protone.eChatGPT.utils.tryWithCatch
import com.protone.eChatGPT.utils.withDefaultContext

class ChatListEditAdapter : ChatListAdapter() {

    interface EditItemEvent {
        fun delete(position: Int, chatItem: ChatItem)
    }

    private var editItemEvent: EditItemEvent? = null
    fun setEditItemEvent(itemEvent: EditItemEvent) {
        editItemEvent = itemEvent
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ChatItemLayoutBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.binding.apply {
            content.setOnLongClickListener {
                val binding = DeleteDialogLayoutBinding.inflate(root.context.layoutInflater)
                BottomSheetDialog(root.context).apply {
                    setCanceledOnTouchOutside(false)
                    setCancelable(false)
                    binding.confirm.setOnClickListener {
                        editItemEvent?.delete(
                            holder.layoutPosition,
                            getChatItem(holder.layoutPosition)
                        )
                        dismiss()
                    }
                    binding.cancel.setOnClickListener {
                        dismiss()
                    }
                    setContentView(binding.root)
                    show()
                }
                true
            }
        }
    }

    override fun ChatItemLayoutBinding.aiAppearanceConfiguration(chatItem: ChatItem) = Unit

}

suspend fun ChatListAdapter.deleteChat(
    position: Int,
    chatItem: ChatItem,
    callback: (Collection<ChatItem>) -> Unit
) {
    withDefaultContext {
        val deleteList = mutableListOf<ChatItem>()
        when (chatItem.target) {
            is ChatItem.ChatTarget.AI -> getChatItemByID(chatItem.target.userId)
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
        callback(deleteList)
    }
}

private fun ChatListAdapter.findSystemItem(userItem: ChatItem): ChatItem? {
    return if (userItem.target is ChatItem.ChatTarget.HUMAN && userItem.target.systemId != null) {
        getChatItemByID(userItem.target.systemId)
    } else null
}

private fun ChatListAdapter.getItem(
    position: Int,
    predicate: (ChatItem) -> Boolean
): ChatItem? =
    tryWithCatch {
        val item = getChatItem(position)
        if (predicate(item)) {
            item
        } else null
    }
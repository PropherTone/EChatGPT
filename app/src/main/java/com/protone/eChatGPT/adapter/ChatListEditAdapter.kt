package com.protone.eChatGPT.adapter

import com.google.android.material.bottomsheet.BottomSheetDialog
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.ChatItemLayoutBinding
import com.protone.eChatGPT.databinding.DeleteDialogLayoutBinding
import com.protone.eChatGPT.utils.layoutInflater

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
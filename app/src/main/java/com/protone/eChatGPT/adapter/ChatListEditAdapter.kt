package com.protone.eChatGPT.adapter

import androidx.appcompat.app.AlertDialog
import com.protone.eChatGPT.databinding.ChatItemLayoutBinding
import com.protone.eChatGPT.databinding.DeleteDialogLayoutBinding
import com.protone.eChatGPT.utils.layoutInflater

class ChatListEditAdapter : ChatListAdapter() {

    override fun onBindViewHolder(holder: ViewBindingHolder<ChatItemLayoutBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.binding.apply {
            content.setOnLongClickListener {
                val binding = DeleteDialogLayoutBinding.inflate(root.context.layoutInflater)
                AlertDialog.Builder(binding.root.context).setView(binding.root).create()
                    .also { dialog ->
                        binding.confirm.setOnClickListener {
                            dialog.dismiss()
                        }
                        binding.cancel.setOnClickListener {
                            dialog.dismiss()
                        }
                    }.show()
                true
            }
        }
    }
}
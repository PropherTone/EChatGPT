package com.protone.eChatGPT.adapter

import androidx.appcompat.app.AlertDialog
import android.view.ViewGroup
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.databinding.ChatItemLayoutBinding
import com.protone.eChatGPT.databinding.DeleteDialogLayoutBinding
import com.protone.eChatGPT.utils.layoutInflater

class ChatListEditAdapter : ChatListAdapter() {

    private val dialog by lazy {
        val binding = DeleteDialogLayoutBinding.inflate(EApplication.app.layoutInflater)
        AlertDialog.Builder(binding.root.context).setView(binding.root).create().also { dialog ->
            binding.confirm.setOnClickListener {
                dialog.dismiss()
            }
            binding.cancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ChatItemLayoutBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.binding.content.apply {
            setOnLongClickListener {
                dialog.show()
                true
            }
        }
    }
}
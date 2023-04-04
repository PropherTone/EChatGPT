package com.protone.eChatGPT.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.ChatHistoriesItemBinding
import com.protone.eChatGPT.utils.layoutInflater

class ChatHistoriesAdapter :
    PagingDataAdapter<ChatItem, ViewBindingHolder<ChatHistoriesItemBinding>>(object :
        DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
           return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem == newItem
        }

    }) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewBindingHolder<ChatHistoriesItemBinding> {
        return ViewBindingHolder(
            ChatHistoriesItemBinding.inflate(
                parent.context.layoutInflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: ViewBindingHolder<ChatHistoriesItemBinding>,
        position: Int
    ) {
        val item = getItem(position)
        holder.binding.apply {
        }
    }


}
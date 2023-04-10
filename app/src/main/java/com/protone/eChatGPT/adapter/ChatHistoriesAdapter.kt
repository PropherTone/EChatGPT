package com.protone.eChatGPT.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.protone.eChatGPT.bean.ChatHistory
import com.protone.eChatGPT.databinding.ChatHistoriesItemBinding
import com.protone.eChatGPT.utils.layoutInflater
import java.text.SimpleDateFormat
import java.util.*

class ChatHistoriesAdapter :
    PagingDataAdapter<ChatHistory, ViewBindingHolder<ChatHistoriesItemBinding>>(object :
        DiffUtil.ItemCallback<ChatHistory>() {
        override fun areItemsTheSame(oldItem: ChatHistory, newItem: ChatHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatHistory, newItem: ChatHistory): Boolean {
            return oldItem == newItem
        }

    }) {

    private val dataFormat by lazy { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }

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
        val item = getItem(position) ?: return
        holder.binding.apply {
            title.text = item.group
            time.text = dataFormat.format(Date(item.date))
        }
    }


}
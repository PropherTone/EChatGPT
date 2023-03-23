package com.protone.eChatGPT.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.protone.eChatGPT.R
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.ChatItemLayoutBinding
import com.protone.eChatGPT.utils.layoutInflater

class ChatListAdapter : Adapter<ViewBindingHolder<ChatItemLayoutBinding>>(),
    ChatHelper by ChatHelper.ChatHelperImp() {

    init {
        attach()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewBindingHolder<ChatItemLayoutBinding> {
        return ViewBindingHolder(
            ChatItemLayoutBinding.inflate(
                parent.context.layoutInflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = getListSize()

    override fun onBindViewHolder(
        holder: ViewBindingHolder<ChatItemLayoutBinding>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            holder.binding.content.text = getChatItem(position).content
        } else super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ChatItemLayoutBinding>, position: Int) {
        val chatItem = getChatItem(position)
        holder.binding.apply {
            when (chatItem.target) {
                ChatItem.ChatTarget.AI -> {
                    root.setBackgroundResource(R.color.ai_content)
                    root.elevation = 0f
                }
                ChatItem.ChatTarget.HUMAN -> {
                    root.setBackgroundResource(R.color.human_content)
                    root.elevation = 2f
                }
            }
            content.text = chatItem.content
        }
    }
}

interface ChatHelper {
    class ChatHelperImp : ChatHelper {
        private lateinit var adapter: ChatListAdapter
        private val chatList = mutableListOf<ChatItem>()

        override fun getData(): MutableList<ChatItem> = chatList

        override fun getChatItem(position: Int): ChatItem = chatList[position]

        override fun getListSize() = chatList.size

        override fun ChatListAdapter.attach() {
            this@ChatHelperImp.adapter = this
        }

        override fun chatSent(chatItem: ChatItem) {
            chatList.add(chatItem)
            adapter.notifyItemInserted(chatList.size)
        }

        override fun receive(chatItem: ChatItem) {
            val index = chatList.indexOfFirst { it.id == chatItem.id }
            if (index == -1) chatSent(chatItem)
            else adapter.notifyItemChanged(index, "Content-Changed")
        }
    }

    fun getData(): MutableList<ChatItem>

    fun getChatItem(position: Int): ChatItem

    fun getListSize(): Int

    fun ChatListAdapter.attach()

    fun chatSent(chatItem: ChatItem)

    fun receive(chatItem: ChatItem)
}
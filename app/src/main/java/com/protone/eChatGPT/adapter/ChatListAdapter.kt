package com.protone.eChatGPT.adapter

import android.graphics.Typeface
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.protone.eChatGPT.R
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.ChatItemLayoutBinding
import com.protone.eChatGPT.utils.layoutInflater
import java.util.concurrent.LinkedBlockingDeque

class ChatListAdapter : Adapter<ViewBindingHolder<ChatItemLayoutBinding>>(),
    ChatHelper by ChatHelper.ChatHelperImp() {

    init {
        attach()
    }

    lateinit var rv: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        rv = recyclerView
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
                    content.typeface = Typeface.DEFAULT
                }
                ChatItem.ChatTarget.HUMAN -> {
                    root.setBackgroundResource(R.color.human_content)
                    root.elevation = 2f
                    content.typeface = Typeface.DEFAULT_BOLD
                }
            }
            content.text = chatItem.content
        }
    }
}

interface ChatHelper {
    class ChatHelperImp : ChatHelper {
        private lateinit var adapter: ChatListAdapter
        private val chatList = LinkedBlockingDeque<ChatItem>()

        override fun getData(): Collection<ChatItem> = chatList

        override fun getChatItem(position: Int): ChatItem = chatList.elementAt(position)

        override fun getListSize() = chatList.size

        override fun ChatListAdapter.attach() {
            this@ChatHelperImp.adapter = this
        }

        override fun setList(list: Collection<ChatItem>) {
            chatList.addAll(list)
            adapter.notifyItemRangeInserted(0, chatList.size)
        }

        override fun chatSent(chatItem: ChatItem) {
            chatList.add(chatItem)
            adapter.notifyItemInserted(chatList.size)
            adapter.rv.scrollToPosition(chatList.size - 1)
        }

        override fun receive(chatItem: ChatItem) {
            val index = chatList.indexOfFirst { it.id == chatItem.id }
            if (index == -1) chatSent(chatItem)
            else adapter.notifyItemChanged(index, "Content-Changed")
        }
    }

    fun getData(): Collection<ChatItem>

    fun getChatItem(position: Int): ChatItem

    fun getListSize(): Int

    fun ChatListAdapter.attach()

    fun setList(list: Collection<ChatItem>)

    fun chatSent(chatItem: ChatItem)

    fun receive(chatItem: ChatItem)
}
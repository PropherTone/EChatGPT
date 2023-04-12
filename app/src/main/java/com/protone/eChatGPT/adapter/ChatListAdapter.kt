package com.protone.eChatGPT.adapter

import android.graphics.Typeface
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.protone.eChatGPT.R
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.ChatItemLayoutBinding
import com.protone.eChatGPT.utils.layoutInflater

open class ChatListAdapter : Adapter<ViewBindingHolder<ChatItemLayoutBinding>>(),
    ChatHelper by ChatHelper.ChatHelperImp() {

    lateinit var rv: RecyclerView
    private var itemEvent: ItemEvent? = null
    fun setItemEvent(itemEvent: ItemEvent) {
        this.itemEvent = itemEvent
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attach()
        rv = recyclerView
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewBindingHolder<ChatItemLayoutBinding> {
        return ViewBindingHolder(
            ChatItemLayoutBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun getItemCount(): Int = getListSize()

    override fun onBindViewHolder(
        holder: ViewBindingHolder<ChatItemLayoutBinding>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) holder.binding.apply {
            content.isGone = false
            content.text = getChatItem(position).content
            when (payloads.first()) {
                ChatItem.ChatTag.Finish -> {
                    state.isGone = true
                }
                ChatItem.ChatTag.Thinking -> {
                    if (!state.isVisible) {
                        state.isVisible = true
                    }
                }
                ChatItem.ChatTag.MaxLength -> {
                    state.isGone = true
                }
                ChatItem.ChatTag.NotSupport -> {
                    state.isGone = true
                }
                ChatItem.ChatTag.NetworkError -> {
                    state.isGone = true
                    retry.isVisible = true
                }
            }
        } else super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: ViewBindingHolder<ChatItemLayoutBinding>, position: Int) {
        val chatItem = getChatItem(position)
        holder.binding.apply {
            when (chatItem.target) {
                is ChatItem.ChatTarget.AI -> {
                    root.setBackgroundResource(R.color.ai_content)
                    root.elevation = 0f
                    content.typeface = Typeface.DEFAULT
                    aiAppearanceConfiguration(chatItem)
                }
                is ChatItem.ChatTarget.HUMAN -> {
                    root.setBackgroundResource(R.color.human_content)
                    root.elevation = 2f
                    content.typeface = Typeface.DEFAULT_BOLD
                    humanAppearanceConfiguration(chatItem)
                }
            }
            content.text = chatItem.content
            root.setOnLongClickListener {
                itemEvent?.onRootLongClick(getChatItem(holder.layoutPosition))
                true
            }
            retry.setOnClickListener {
                val index = holder.layoutPosition
                if (itemEvent?.onRetry(getChatItem(index)) == true) {
                    remove(index)
                }
            }
            details.setOnClickListener {
                itemEvent?.onDetail(getChatItem(holder.layoutPosition))
            }
        }
    }

    internal open fun ChatItemLayoutBinding.aiAppearanceConfiguration(chatItem: ChatItem) {
        details.isVisible = true
        if (chatItem.chatTag == ChatItem.ChatTag.NetworkError) {
            details.isGone = true
            retry.isVisible = true
        } else {
            state.isVisible = true
        }
    }

    internal open fun ChatItemLayoutBinding.humanAppearanceConfiguration(chatItem: ChatItem) {
        state.isGone = true
        details.isGone = true
        retry.isGone = true
    }

    interface ItemEvent {

        fun onRootLongClick(item: ChatItem)

        fun onRetry(item: ChatItem): Boolean

        fun onDetail(item: ChatItem)

    }

}

interface ChatHelper {

    class ChatHelperImp : ChatHelper {

        private var adapter: ChatListAdapter? = null
        private val chatList = mutableListOf<ChatItem>()

        override fun ChatListAdapter.attach() {
            this@ChatHelperImp.adapter = this
        }

        override fun getData(): Collection<ChatItem> = chatList

        override fun getChatItem(position: Int): ChatItem = chatList[position]

        override fun getChatItemByID(id: String): ChatItem? = chatList.find { it.id == id }

        override fun getListSize() = chatList.size

        override fun setList(list: Collection<ChatItem>) {
            adapter?.run {
                val size = chatList.size
                chatList.addAll(list)
                notifyItemRangeInserted(size, chatList.size)
            }
        }

        override fun remove(chatItem: ChatItem) {
            val index = chatList.indexOf(chatItem)
            if (index != -1) remove(index)
        }

        override fun remove(position: Int) {
            chatList.removeAt(position)
            adapter?.notifyItemRemoved(position)
        }

        override fun clear() {
            val size = chatList.size
            chatList.clear()
            adapter?.notifyItemRangeRemoved(0, size)
        }

        override fun chatSent(chatItem: ChatItem) {
            adapter?.run {
                val userIndex = chatList.indexOfFirst { it.id == chatItem.id }
                if (userIndex != -1) return
                chatList.add(chatItem)
                notifyItemInserted(chatList.size)
                rv.scrollToPosition(chatList.size - 1)
            }
        }

        override fun receive(chatItem: ChatItem) {
            if (chatItem.target !is ChatItem.ChatTarget.AI) return
            adapter?.run {
                val aiIndex = chatList.indexOfFirst { it.id == chatItem.id }
                if (aiIndex == -1) {
                    val userIndex = chatList.indexOfFirst { it.id == chatItem.target.userId }
                    if (userIndex != -1) {
                        chatList.add(userIndex + 1, chatItem)
                        notifyItemInserted(userIndex + 1)
                    } else chatSent(chatItem)
                } else notifyItemChanged(aiIndex, chatItem.chatTag)
            }
        }

    }

    fun ChatListAdapter.attach()

    fun getData(): Collection<ChatItem>

    fun getChatItem(position: Int): ChatItem

    fun getChatItemByID(id: String): ChatItem?

    fun getListSize(): Int

    fun setList(list: Collection<ChatItem>)

    fun remove(chatItem: ChatItem)

    fun remove(position: Int)

    fun clear()

    fun chatSent(chatItem: ChatItem)

    fun receive(chatItem: ChatItem)
}
package com.protone.eChatGPT.adapter

import android.app.AlertDialog
import android.graphics.Typeface
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.protone.eChatGPT.R
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.databinding.ChatDetailLayoutBinding
import com.protone.eChatGPT.databinding.ChatItemLayoutBinding
import com.protone.eChatGPT.mods.TAG
import com.protone.eChatGPT.utils.getString
import com.protone.eChatGPT.utils.layoutInflater

open class ChatListAdapter : Adapter<ViewBindingHolder<ChatItemLayoutBinding>>(),
    ChatHelper by ChatHelper.ChatHelperImp() {

    lateinit var rv: RecyclerView

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
                ChatItem.ChatTarget.AI -> {
                    root.setBackgroundResource(R.color.ai_content)
                    root.elevation = 0f
                    content.typeface = Typeface.DEFAULT
                    aiAppearanceConfiguration(chatItem)
                }
                ChatItem.ChatTarget.HUMAN -> {
                    root.setBackgroundResource(R.color.human_content)
                    root.elevation = 2f
                    content.typeface = Typeface.DEFAULT_BOLD
                    humanAppearanceConfiguration(chatItem)
                }
            }
            content.text = chatItem.content
            root.setOnLongClickListener {
                true
            }
            retry.setOnClickListener {

            }
            details.setOnClickListener {
                val detailLayoutBinding =
                    ChatDetailLayoutBinding.inflate(root.context.layoutInflater).apply {
                        totalTokens.text = String.format(
                            R.string.total_token_count.getString(),
                            chatItem.usage.total.toString()
                        )
                        promptTokens.text = String.format(
                            R.string.prompt_token_count.getString(),
                            chatItem.usage.prompt.toString()
                        )
                        completionTokens.text = String.format(
                            R.string.completion_token_count.getString(),
                            chatItem.usage.completion.toString()
                        )
                    }
                AlertDialog.Builder(root.context)
                    .setView(detailLayoutBinding.root)
                    .create()
                    .show()
            }
        }
    }

    internal open fun ChatItemLayoutBinding.aiAppearanceConfiguration(chatItem: ChatItem) {
        details.isVisible = true
        if (chatItem.chatTag != ChatItem.ChatTag.NetworkError) {
            state.isVisible = true
        } else {
            details.isGone = true
            retry.isVisible = true
        }
    }

    internal open fun ChatItemLayoutBinding.humanAppearanceConfiguration(chatItem: ChatItem) {
        state.isGone = true
        details.isGone = true
        retry.isGone = true
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

        override fun getChatItem(position: Int): ChatItem = chatList.elementAt(position)

        override fun getListSize() = chatList.size

        override fun setList(list: Collection<ChatItem>) {
            adapter?.run {
                chatList.addAll(list)
                notifyItemRangeInserted(0, chatList.size)
            }
        }

        override fun remove(chatItem: ChatItem) {
            chatList.remove(chatItem)
        }

        override fun remove(position: Int) {
            chatList.removeAt(position)
        }

        override fun chatSent(chatItem: ChatItem) {
            adapter?.run {
                chatList.add(chatItem)
                notifyItemInserted(chatList.size)
                rv.scrollToPosition(chatList.size - 1)
            }
        }

        override fun receive(chatItem: ChatItem) {
            adapter?.run {
                val index = chatList.indexOfFirst { it.id == chatItem.id }
                if (index == -1) chatSent(chatItem)
                else notifyItemChanged(index, chatItem.chatTag)
            }
        }

    }

    fun ChatListAdapter.attach()

    fun getData(): Collection<ChatItem>

    fun getChatItem(position: Int): ChatItem

    fun getListSize(): Int

    fun setList(list: Collection<ChatItem>)

    fun remove(chatItem: ChatItem)

    fun remove(position: Int)

    fun chatSent(chatItem: ChatItem)

    fun receive(chatItem: ChatItem)
}
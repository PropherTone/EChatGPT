package com.protone.eChatGPT.adapter

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.protone.eChatGPT.bean.ChatHistory
import com.protone.eChatGPT.databinding.ChatHistoriesItemBinding
import com.protone.eChatGPT.utils.SelectProxy
import com.protone.eChatGPT.utils.SelectProxyImp
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

    }), SelectProxy<ChatHistory> by SelectProxyImp() {

    interface ItemEvent {
        fun enterSelectMode()
        fun exitSelectMode()
        fun itemClicked(item: ChatHistory)

    }

    private val dataFormat by lazy { SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()) }

    private var onSelectMode = false

    private var itemEvent: ItemEvent? = null
    fun setOnItemEvent(itemEvent: ItemEvent) {
        this.itemEvent = itemEvent
    }

    init {
        onSelect = { position, isSelect ->
            notifyItemChanged(position, isSelect)
        }
        indexOfItem = { item ->
            snapshot().indexOf(item)
        }
    }

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
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads.first() is Boolean) {
            TransitionManager.beginDelayedTransition(
                holder.binding.root as ViewGroup,
                ChangeBounds()
            )
            holder.binding.select.isGone = !(payloads.first() as Boolean)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(
        holder: ViewBindingHolder<ChatHistoriesItemBinding>,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.binding.apply {
            select.isGone = !isSelected(item)
            root.setOnClickListener {
                if (!onSelectMode) {
                    itemEvent?.itemClicked(
                        getItem(holder.layoutPosition) ?: return@setOnClickListener
                    )
                    return@setOnClickListener
                }
                select(
                    holder.layoutPosition,
                    getItem(holder.layoutPosition) ?: return@setOnClickListener
                )
            }
            root.setOnLongClickListener {
                onSelectMode = true
                itemEvent?.enterSelectMode()
                select(
                    holder.layoutPosition,
                    getItem(holder.layoutPosition) ?: return@setOnLongClickListener true
                )
                true
            }
            title.text = item.group
            time.text = dataFormat.format(Date(item.date))
        }
    }

    fun getHistory(title: String) = snapshot().filter { it?.group == title }.filterNotNull()

    fun getSelectedData(): Collection<ChatHistory> {
        val data = snapshot()
        return getSelected().filter { data.indexOf(it) != -1 }
    }

    fun exitSelectMode() {
        onSelectMode = false
        clearSelect()
        itemEvent?.exitSelectMode()
    }

}
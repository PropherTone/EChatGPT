package com.protone.eChatGPT.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.protone.eChatGPT.bean.Mode
import com.protone.eChatGPT.databinding.ModeListItemLayoutBinding
import com.protone.eChatGPT.utils.layoutInflater

class ModeListAdapter : Adapter<ViewBindingHolder<ModeListItemLayoutBinding>>() {

    private val data = mutableListOf<Mode>()

    fun setModes(list: Collection<Mode>) {
        data.addAll(list)
        notifyItemRangeInserted(0, data.size)
    }

    private var callback: ((View, String) -> Unit)? = null

    fun modesEvent(func: (View, String) -> Unit) {
        callback = func
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewBindingHolder<ModeListItemLayoutBinding> {
        return ViewBindingHolder(
            ModeListItemLayoutBinding.inflate(
                parent.context.layoutInflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewBindingHolder<ModeListItemLayoutBinding>, position: Int) {
        holder.binding.apply {
            data[position].let {
                icon.setImageResource(it.iconRes)
                icon.text = it.text
                callback?.invoke(root, it.text)
            }
        }
    }

}
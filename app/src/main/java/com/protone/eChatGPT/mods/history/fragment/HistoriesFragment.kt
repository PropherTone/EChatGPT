package com.protone.eChatGPT.mods.history.fragment

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewpager2.widget.ViewPager2
import com.protone.eChatGPT.R
import com.protone.eChatGPT.adapter.ChatHistoriesAdapter
import com.protone.eChatGPT.adapter.ViewBindingHolder
import com.protone.eChatGPT.databinding.HistoriesFragmentBinding
import com.protone.eChatGPT.databinding.HistoriesOptionsActiveSceneBinding
import com.protone.eChatGPT.databinding.HistoriesOptionsNormalSceneBinding
import com.protone.eChatGPT.mods.BaseFragment
import com.protone.eChatGPT.utils.launchIO
import com.protone.eChatGPT.utils.layoutInflater
import com.protone.eChatGPT.viewModel.activityViewModel.HistoryModViewModel
import com.protone.eChatGPT.viewModel.fragViewModel.HistoriesViewModel

class HistoriesFragment :
    BaseFragment<HistoriesFragmentBinding, HistoriesViewModel, HistoryModViewModel>() {
    override val viewModel: HistoriesViewModel by viewModels()
    override val activityViewModel: HistoryModViewModel by activityViewModels()

    private var chatHistoriesAdapter: ChatHistoriesAdapter? = null

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): HistoriesFragmentBinding {
        return HistoriesFragmentBinding.inflate(inflater, container, false).apply {
            historyList.init()
            options.init()
        }
    }

    override fun HistoriesViewModel.init() {
        chatHistoriesAdapter?.refresh() ?: launchIO {
            getHistoryPagingSource(20).flow.collect {
                chatHistoriesAdapter?.submitData(it)
            }
        }
    }

    private fun RecyclerView.init() {
        layoutManager = LinearLayoutManager(activity)
        chatHistoriesAdapter = ChatHistoriesAdapter()
        adapter = chatHistoriesAdapter
        addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildLayoutPosition(view)
                val size = requireContext().resources.getDimensionPixelSize(R.dimen.item_Decoration)
                if (position != 0) {
                    outRect.top = size
                }
                if (position == parent.size - 1) {
                    outRect.bottom = size
                }
            }
        })
    }

    private fun ViewPager2.init() {
        adapter = object : Adapter<ViewBindingHolder<ViewDataBinding>>() {

            override fun getItemViewType(position: Int): Int {
                return position
            }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ViewBindingHolder<ViewDataBinding> {
                return ViewBindingHolder(
                    if (viewType == 0) HistoriesOptionsNormalSceneBinding.inflate(
                        parent.context.layoutInflater,
                        parent,
                        false
                    ) else HistoriesOptionsActiveSceneBinding.inflate(
                        parent.context.layoutInflater,
                        parent,
                        false
                    )
                )
            }

            override fun getItemCount(): Int = 2

            override fun onBindViewHolder(
                holder: ViewBindingHolder<ViewDataBinding>,
                position: Int
            ) {
                holder.binding.apply {
                    when (this) {
                        is HistoriesOptionsNormalSceneBinding -> {
                            root.setOnClickListener {
                                activityViewModel.send(HistoryModViewModel.HistoryViewEvent.Finish)
                            }
                        }
                        is HistoriesOptionsActiveSceneBinding -> {
                            
                        }
                    }
                }
            }

        }
    }

}
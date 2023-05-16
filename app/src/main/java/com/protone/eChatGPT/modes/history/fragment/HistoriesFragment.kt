package com.protone.eChatGPT.modes.history.fragment

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.size
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.protone.eChatGPT.R
import com.protone.eChatGPT.adapter.ChatHistoriesAdapter
import com.protone.eChatGPT.adapter.ViewBindingHolder
import com.protone.eChatGPT.bean.ChatHistory
import com.protone.eChatGPT.databinding.HistoriesFragmentBinding
import com.protone.eChatGPT.databinding.HistoriesOptionsActiveSceneBinding
import com.protone.eChatGPT.databinding.HistoriesOptionsNormalSceneBinding
import com.protone.eChatGPT.modes.BaseActivityFragment
import com.protone.eChatGPT.modes.TAG
import com.protone.eChatGPT.modes.history.HistoryActivity
import com.protone.eChatGPT.utils.launchDefault
import com.protone.eChatGPT.utils.launchIO
import com.protone.eChatGPT.utils.launchMain
import com.protone.eChatGPT.utils.layoutInflater
import com.protone.eChatGPT.viewModel.activityViewModel.HistoryModeViewModel
import com.protone.eChatGPT.viewModel.fragViewModel.HistoriesViewModel
import kotlinx.coroutines.launch

class HistoriesFragment :
    BaseActivityFragment<HistoriesFragmentBinding, HistoriesViewModel, HistoryModeViewModel>() {
    override val viewModel: HistoriesViewModel by viewModels()
    override val activityViewModel: HistoryModeViewModel by activityViewModels()

    companion object {
        const val SWAP_DURATION = 300L
        const val PAGE_SIZE = 20
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): HistoriesFragmentBinding {
        return HistoriesFragmentBinding.inflate(inflater, container, false).apply {
            historyList.init()
            options.init(
                normal = {
                    it.root.setOnClickListener {
                        activityViewModel.send(HistoryModeViewModel.HistoryViewEvent.Finish)
                    }
                },
                active = {
                    it.delete.setOnClickListener {
                        viewModel.deleteChatHistories(
                            viewModel.chatHistoriesAdapter.getSelectedData().toMutableList()
                        )
                    }
                    it.exit.setOnClickListener {
                        viewModel.chatHistoriesAdapter.exitSelectMode()
                        options.setCurrentItem(0, SWAP_DURATION)
                    }
                }
            )
        }
    }

    override fun HistoriesViewModel.init(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) chatHistoriesAdapter.refresh() else {
            chatHistoriesAdapter.setOnItemEvent(object : ChatHistoriesAdapter.ItemEvent {
                override fun enterSelectMode() {
                    binding.options.setCurrentItem(1, SWAP_DURATION)
                }

                override fun exitSelectMode() {
                    binding.options.setCurrentItem(0, SWAP_DURATION)
                }

                override fun itemClicked(item: ChatHistory) {
                    activityViewModel.send(
                        HistoryModeViewModel.HistoryViewEvent.ShowChatHistory(
                            item.group
                        )
                    )
                }

            })
            launchIO {
                getHistoryPagingSource(PAGE_SIZE).flow.collect {
                    chatHistoriesAdapter.submitData(it)
                }
            }
            findNavController().currentBackStackEntry
                ?.savedStateHandle
                ?.getLiveData(HistoryActivity.FRAG_KEY, "")
                ?.observe(this@HistoriesFragment) {
                    if (it.isEmpty()) return@observe
                    deleteChatHistories(chatHistoriesAdapter.getHistory(it).toMutableList())
                }
        }
    }

    private fun RecyclerView.init() {
        layoutManager = LinearLayoutManager(activity)
        adapter = viewModel.chatHistoriesAdapter
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

    private fun ViewPager2.init(
        normal: (HistoriesOptionsNormalSceneBinding) -> Unit,
        active: (HistoriesOptionsActiveSceneBinding) -> Unit
    ) {
        isUserInputEnabled = false
        orientation = ViewPager2.ORIENTATION_VERTICAL
        adapter = object : RecyclerView.Adapter<ViewBindingHolder<ViewDataBinding>>() {

            override fun getItemViewType(position: Int): Int = position

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
                            normal(this)
                        }

                        is HistoriesOptionsActiveSceneBinding -> {
                            active(this)
                        }
                    }
                }
            }

        }
    }

    private fun ViewPager2.setCurrentItem(
        item: Int,
        duration: Long,
        interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
        pagePxWidth: Int = width
    ) {
        val pxToDrag: Int = pagePxWidth * (item - currentItem)
        var previousValue = 0
        ValueAnimator.ofInt(0, pxToDrag).apply {
            addUpdateListener { valueAnimator ->
                val currentValue = valueAnimator.animatedValue as Int
                val currentPxToDrag = (currentValue - previousValue).toFloat()
                fakeDragBy(-currentPxToDrag)
                previousValue = currentValue
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    beginFakeDrag()
                }

                override fun onAnimationEnd(animation: Animator) {
                    endFakeDrag()
                }

                override fun onAnimationCancel(animation: Animator) = Unit
                override fun onAnimationRepeat(animation: Animator) = Unit
            })
            this.interpolator = interpolator
            this.duration = duration
            start()
        }
    }

    private fun HistoriesViewModel.deleteChatHistories(data: MutableList<ChatHistory>) {
        deleteChatHistory(data) { result ->
            launch {
                chatHistoriesAdapter.submitData(PagingData.from(result.toList()))
                chatHistoriesAdapter.exitSelectMode()
            }
        }
    }

}
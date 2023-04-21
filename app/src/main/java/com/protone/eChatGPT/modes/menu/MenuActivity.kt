package com.protone.eChatGPT.modes.menu

import android.app.ActivityManager
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.protone.eChatGPT.R
import com.protone.eChatGPT.adapter.ModeListAdapter
import com.protone.eChatGPT.bean.Mode
import com.protone.eChatGPT.bean.SwapAni
import com.protone.eChatGPT.databinding.MenuActivityBinding
import com.protone.eChatGPT.modes.BaseActivity
import com.protone.eChatGPT.modes.activities
import com.protone.eChatGPT.modes.chat.ChatActivity
import com.protone.eChatGPT.modes.history.HistoryActivity
import com.protone.eChatGPT.modes.image.ImageActivity
import com.protone.eChatGPT.utils.getString
import com.protone.eChatGPT.utils.intent
import com.protone.eChatGPT.viewModel.activityViewModel.MenuViewModel

class MenuActivity : BaseActivity<MenuActivityBinding, MenuViewModel>() {
    override val viewModel: MenuViewModel by viewModels()

    override fun createView(savedInstanceState: Bundle?): MenuActivityBinding {
        return MenuActivityBinding.inflate(layoutInflater).apply {
            backChat.isVisible = activities.contains(ChatActivity::class.java)
            initViewAction()
            root.post {
                modeList.init()
            }
        }
    }

    override fun MenuViewModel.init(savedInstanceState: Bundle?) {

    }

    override fun onResume() {
        super.onResume()
        (getSystemService(ACTIVITY_SERVICE) as ActivityManager).apply {
            appTasks.mapNotNull { it.taskInfo?.topActivity?.className }.let {
                binding.backChat.isGone = !it.contains(ChatActivity::class.java.name)
                binding.backImage.isGone = !it.contains(ImageActivity::class.java.name)
            }
        }
    }

    override fun getSwapAnim(): SwapAni {
        return SwapAni(R.anim.card_bot_in, R.anim.card_bot_out)
    }

    private fun MenuActivityBinding.initViewAction() {
        backChat.setOnClickListener {
            startActivity(ChatActivity::class.intent)
            overridePendingTransition(R.anim.card_bot_in, R.anim.card_bot_out)
        }
        backImage.setOnClickListener {
            startActivity(ImageActivity::class.intent)
        }
    }

    private fun RecyclerView.init() {
        val itemCount = 3
        layoutManager = GridLayoutManager(this@MenuActivity, itemCount)
        addItemDecoration(object : ItemDecoration() {

            private val margin =
                this@MenuActivity.resources.getDimensionPixelSize(R.dimen.option_content_margin)

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val position = parent.getChildLayoutPosition(view)
                if (position % itemCount == 0) {
                    outRect.left = margin
                }
                outRect.right = margin
                outRect.top = margin
            }
        })
        ModeListAdapter().let {
            adapter = it
            val chat = R.string.chat.getString()
            val history = R.string.history.getString()
            val configs = R.string.configs.getString()
            val image = R.string.image.getString()
            val audio = R.string.audio.getString()
            val completions = R.string.completions.getString()
            val edits = R.string.edits.getString()
            val embeddings = R.string.embeddings.getString()
            val files = R.string.files.getString()
            val fineTunes = R.string.fineTunes.getString()
            val models = R.string.models.getString()
            val moderations = R.string.moderations.getString()
            it.setModes(
                listOf(
                    Mode(R.drawable.chat, chat),
                    Mode(R.drawable.history, history),
                    Mode(R.drawable.chart, configs),
                    Mode(R.drawable.image, image),
                    Mode(R.drawable.audio, audio),
                    Mode(R.drawable.completions, completions),
                    Mode(R.drawable.edit, edits),
                    Mode(R.drawable.embeddings, embeddings),
                    Mode(R.drawable.file, files),
                    Mode(R.drawable.tune, fineTunes),
                    Mode(R.drawable.model, models),
                    Mode(R.drawable.moderation, moderations)
                )
            )
            it.modesEvent { view, s ->
                view.setOnClickListener {
                    when (s) {
                        chat -> startActivity(
                            ChatActivity::class.intent.putExtra(ChatActivity.OPTION, "")
                        )

                        history -> startActivity(HistoryActivity::class.intent)
                        configs -> {}
                        image -> {}
                        audio -> {}
                        completions -> {}
                        edits -> {}
                        embeddings -> {}
                        files -> {}
                        fineTunes -> {}
                        models -> {}
                        moderations -> {}
                    }
                }
            }
        }
    }

}
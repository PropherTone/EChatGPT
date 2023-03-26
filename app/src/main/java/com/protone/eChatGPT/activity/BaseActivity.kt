package com.protone.eChatGPT.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.protone.eChatGPT.R
import com.protone.eChatGPT.bean.ChatItem
import com.protone.eChatGPT.utils.*
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

const val TAG = "EChatGPT_TAG"

abstract class BaseActivity<VB : ViewDataBinding, VM : ViewModel> : AppCompatActivity(),
    CoroutineScope by MainScope() {

    protected lateinit var binding: VB
        private set
    abstract val viewModel: VM

    abstract fun createView(): VB
    abstract fun VM.init()

    val code = AtomicInteger(0)

    override fun onCreate(savedInstanceState: Bundle?) {

        setTransparentClipStatusBar(AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        binding = createView().apply {
            setContentView(root)
            if (savedInstanceState == null) root.post { viewModel.init() }
        }
    }

    suspend inline fun startActivityForResult(
        intent: Intent?, pendingTransition: Pair<Int, Int>? = null
    ): ActivityResult? = onResult(Dispatchers.Main) { co ->
        activityResultRegistry.register(
            code.incrementAndGet().toString(),
            ActivityResultContracts.StartActivityForResult(),
        ) {
            co.resumeWith(Result.success(it))
        }.launch(intent)
        pendingTransition?.let { overridePendingTransition(it.first, it.second) }
    }

    open fun getSwapAnim(): Pair<Int, Int>? {
        return null
    }

    override fun finish() {
        super.finish()
        getSwapAnim()?.let {
            overridePendingTransition(it.first, it.second)
        }
    }

    override fun onDestroy() {
        try {
            cancel()
        } finally {
            super.onDestroy()
        }
    }
}

val gainData by lazy { GainData() }

suspend inline fun BaseActivity<*, *>.startSaveConversationActivityForResult(
    chatList: Collection<ChatItem>,
    itemNormalHeight: Int,
    itemHeight: Int,
    callback: (ActivityResult) -> Unit
) {
    gainData.put(SaveConversationActivity.CHAT_HISTORY, chatList)
    startActivityForResult(
        SaveConversationActivity::class.intent
            .putExtra(SaveConversationActivity.ITEM_NORMAL_HEIGHT, itemNormalHeight)
            .putExtra(SaveConversationActivity.ITEM_HEIGHT, itemHeight),
        Pair(R.anim.card_in_ltr, R.anim.card_out_ltr)
    )?.let {
        callback(it)
    }
}
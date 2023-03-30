package com.protone.eChatGPT.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
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
            root.post { viewModel.init() }
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

    var isKeyBroadShow = false
    var onLayoutChangeListener: View.OnLayoutChangeListener? = null

    inline fun setSoftInputStatusListener(crossinline onSoftInput: (Int, Boolean) -> Unit = { _, _ -> }) {
        isKeyBroadShow = false
        removeSoftInputStatusListener()
        val rect = Rect()
        val height = window.decorView.height
        onLayoutChangeListener = View.OnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            v.getWindowVisibleDisplayFrame(rect)
            val i = height - rect.bottom
            if (i > 0 && !isKeyBroadShow) {
                isKeyBroadShow = true
                onSoftInput.invoke(i, true)
            } else if (i <= 0 && isKeyBroadShow) {
                isKeyBroadShow = false
                onSoftInput.invoke(i, false)
            }
        }
        window.decorView.addOnLayoutChangeListener(onLayoutChangeListener)
    }

    fun removeSoftInputStatusListener() {
        if (onLayoutChangeListener == null) return
        window.decorView.removeOnLayoutChangeListener(onLayoutChangeListener)
        onLayoutChangeListener = null
    }

    open fun getSwapAnim(): Pair<Int, Int>? {
        return null
    }

    override fun finish() {
        removeSoftInputStatusListener()
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
    itemHeight: Int,
    callback: (ActivityResult) -> Unit
) {
    gainData.put(SaveConversationActivity.CHAT_HISTORY, chatList)
    startActivityForResult(
        SaveConversationActivity::class.intent.putExtra(
            SaveConversationActivity.ITEM_HEIGHT,
            itemHeight
        ),
        Pair(R.anim.card_in_ltr, R.anim.card_out_ltr)
    )?.let {
        callback(it)
    }
}
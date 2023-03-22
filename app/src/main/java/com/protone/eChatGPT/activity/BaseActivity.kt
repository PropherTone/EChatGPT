package com.protone.eChatGPT.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.protone.eChatGPT.utils.setTransparentClipStatusBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

const val TAG = "EChatGPT_TAG"

abstract class BaseActivity<VB : ViewDataBinding, VM : ViewModel> : AppCompatActivity(),
    CoroutineScope by MainScope() {

    protected lateinit var binding: VB
        private set
    abstract val viewModel: VM

    abstract fun createView(): VB
    abstract fun VM.init()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTransparentClipStatusBar(AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        binding = createView().apply {
            setContentView(root)
            if (savedInstanceState == null) root.post { viewModel.init() }
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
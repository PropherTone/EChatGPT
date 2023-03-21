package com.protone.eChatGPT.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.protone.eChatGPT.utils.setTransparentClipStatusBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

const val TAG = "EChatGPT_TAG"

abstract class BaseActivity<VB : ViewDataBinding, VM : ViewModel> : AppCompatActivity(),
    CoroutineScope by MainScope() {

    protected lateinit var binding: VB
        private set
    abstract val viewModel: VM

    abstract fun createView(): VB
    abstract fun init()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTransparentClipStatusBar()
        super.onCreate(savedInstanceState)
        binding = createView().apply {
            setContentView(root)
            init()
        }
    }
}
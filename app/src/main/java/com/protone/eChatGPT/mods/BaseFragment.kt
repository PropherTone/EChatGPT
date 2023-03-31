package com.protone.eChatGPT.mods

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseFragment<VB : ViewDataBinding, VM : ViewModel, AVM : ViewModel> : Fragment(),
    CoroutineScope by MainScope() {

    lateinit var binding: VB
    abstract val viewModel: VM
    abstract val activityViewModel: AVM

    abstract fun createView(inflater: LayoutInflater, container: ViewGroup?): VB
    abstract fun VM.init()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createView(inflater, container).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init()
    }

    override fun onDestroy() {
        try {
            cancel()
        } finally {
            super.onDestroy()
        }
    }
}
package com.protone.eChatGPT.modes

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

abstract class BaseActivityFragment<VB : ViewDataBinding, VM : ViewModel, AVM : ViewModel> :
    BaseFragment<VB, VM>(){
    abstract val activityViewModel: AVM
}

abstract class BaseFragment<VB : ViewDataBinding, VM : ViewModel> : Fragment(),
    CoroutineScope by MainScope() {

    lateinit var binding: VB
    abstract val viewModel: VM

    abstract fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): VB

    abstract fun VM.init(savedInstanceState: Bundle?)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return createView(inflater, container, savedInstanceState).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        try {
            cancel()
        } finally {
            super.onDestroy()
        }
    }
}
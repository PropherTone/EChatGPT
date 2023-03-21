package com.protone.eChatGPT.activity

import androidx.activity.viewModels
import com.protone.eChatGPT.databinding.ActivityMainBinding
import com.protone.eChatGPT.viewModel.MainViewModel

class MainActivity : BaseActivity<ActivityMainBinding,MainViewModel>() {
    override val viewModel: MainViewModel by viewModels()

    override fun createView(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun init() {
        viewModel.init("")
    }

}
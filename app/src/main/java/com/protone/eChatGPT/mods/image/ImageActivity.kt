package com.protone.eChatGPT.mods.image

import android.os.Bundle
import androidx.activity.viewModels
import com.protone.eChatGPT.databinding.ImageActivityBinding
import com.protone.eChatGPT.mods.BaseActivity
import com.protone.eChatGPT.viewModel.activityViewModel.ImageModViewModel

class ImageActivity : BaseActivity<ImageActivityBinding, ImageModViewModel>() {

    override val viewModel: ImageModViewModel by viewModels()

    override fun createView(savedInstanceState: Bundle?): ImageActivityBinding {
        return ImageActivityBinding.inflate(layoutInflater)
    }

    override fun ImageModViewModel.init(savedInstanceState: Bundle?) {
    }

}
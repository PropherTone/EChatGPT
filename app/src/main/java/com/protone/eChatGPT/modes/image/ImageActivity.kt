package com.protone.eChatGPT.modes.image

import android.os.Bundle
import androidx.activity.viewModels
import com.protone.eChatGPT.databinding.ImageActivityBinding
import com.protone.eChatGPT.modes.BaseActivity
import com.protone.eChatGPT.viewModel.activityViewModel.ImageModeViewModel

class ImageActivity : BaseActivity<ImageActivityBinding, ImageModeViewModel>() {

    override val viewModel: ImageModeViewModel by viewModels()

    override fun createView(savedInstanceState: Bundle?): ImageActivityBinding {
        return ImageActivityBinding.inflate(layoutInflater)
    }

    override fun ImageModeViewModel.init(savedInstanceState: Bundle?) {
    }

}
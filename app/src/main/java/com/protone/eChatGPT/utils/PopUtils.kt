package com.protone.eChatGPT.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import com.protone.eChatGPT.databinding.EnterTokenGuideLayoutBinding

inline fun Context.showGetTokenPop(decorView: View, crossinline callBack: (CharSequence) -> Unit) =
    PopupWindow(this).also { pop ->
        pop.contentView = EnterTokenGuideLayoutBinding.inflate(layoutInflater).apply {
            confirmBtn.setOnClickListener {
                callBack(tokenInputBox.text)
                pop.dismiss()
            }
        }.root
        pop.isFocusable = true
        pop.isOutsideTouchable = false
        pop.isClippingEnabled = false
        pop.elevation = 0f
        pop.setBackgroundDrawable(null)
        pop.width = decorView.measuredWidth
        pop.height = decorView.measuredHeight
        pop.showAtLocation(decorView, Gravity.TOP, 0, 0)
    }

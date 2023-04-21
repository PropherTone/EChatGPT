package com.protone.eChatGPT.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow

inline fun Context.showPopupWindow(decorView: View, createContent: (PopupWindow) -> View) =
    PopupWindow(this).also { pop ->
        pop.contentView = createContent(pop)
        pop.isFocusable = true
        pop.isOutsideTouchable = false
        pop.isClippingEnabled = false
        pop.setBackgroundDrawable(null)
        pop.width = decorView.measuredWidth
        pop.height = decorView.measuredHeight
        pop.showAtLocation(decorView, Gravity.TOP, 0, 0)
    }

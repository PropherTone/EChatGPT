package com.protone.eChatGPT.utils

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import com.protone.eChatGPT.EApplication
import kotlin.reflect.KClass

val KClass<*>.intent: Intent
    get() = Intent(EApplication.app, this.java)

fun Int.getString() = EApplication.app.getString(this)

fun Int.getDimension() = EApplication.app.resources.getDimension(this)

fun View.marginTop(margin: Int) {
    if (this !is ViewGroup) return
    val marginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
    marginLayoutParams.topMargin += margin
    layoutParams = marginLayoutParams
}

fun View.marginBottom(margin: Int) {
    if (this !is ViewGroup) return
    val marginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
    marginLayoutParams.bottomMargin = margin
    layoutParams = marginLayoutParams
}

fun View.marginStart(margin: Int) {
    if (this !is ViewGroup) return
    val marginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
    marginLayoutParams.leftMargin = margin
    layoutParams = marginLayoutParams
}

fun View.marginEnd(margin: Int) {
    if (this !is ViewGroup) return
    val marginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
    marginLayoutParams.rightMargin = margin
    layoutParams = marginLayoutParams
}

inline fun doWithTimeout(
    timeoutMills: Long = 3000L,
    func: (() -> Unit) -> Unit,
    crossinline onTimeout: () -> Unit
) {
    val handler = Handler(Looper.getMainLooper()) {
        onTimeout()
        false
    }
    val refreshTimer = {
        handler.removeMessages(0)
        handler.sendEmptyMessageDelayed(0, timeoutMills)
    }
    refreshTimer()
    func(refreshTimer)
    handler.removeMessages(0)
}
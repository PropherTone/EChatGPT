package com.protone.eChatGPT.utils

import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.protone.eChatGPT.EApplication
import kotlin.reflect.KClass

val KClass<*>.intent: Intent
    get() = Intent(EApplication.app, this.java)

fun Int.getString() = EApplication.app.getString(this)

fun Int.getDimension() = EApplication.app.resources.getDimension(this)

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
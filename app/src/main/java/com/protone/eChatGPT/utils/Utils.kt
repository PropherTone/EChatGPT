package com.protone.eChatGPT.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.modes.TAG
import kotlinx.coroutines.*

fun String.toast() {
    Toast.makeText(EApplication.app, this, Toast.LENGTH_SHORT).show()
}

fun String.saveContentToClipBoard() {
    (EApplication.app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?)
        ?.setPrimaryClip(ClipData.newPlainText("chat", this))
}

suspend inline fun <T> onResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    crossinline runnable: CoroutineScope.(CancellableContinuation<T>) -> Unit
) = withContext(dispatcher) {
    suspendCancellableCoroutine {
        try {
            runnable.invoke(this, it)
        } catch (e: Exception) {
            it.resumeWith(Result.failure(e))
        }
    }
}

inline fun <reified T> tryWithCatch(catch: (Exception) -> T? = { null }, func: () -> T?): T? {
    return try {
        func()
    } catch (e: Exception) {
        catch(e)
    }
}

inline fun doWithTimeout(
    timeoutMills: Long = 3000L,
    func: (() -> Unit) -> Unit,
    crossinline onTimeout: () -> Unit
) {
    val handler = Handler(Looper.getMainLooper()) {
        Log.d(TAG, "doWithTimeout: ")
        onTimeout()
        true
    }
    val refreshTimer = {
        handler.removeMessages(0)
        handler.sendEmptyMessageDelayed(0, timeoutMills)
    }
    refreshTimer()
    func(refreshTimer)
    handler.removeMessages(0)
}
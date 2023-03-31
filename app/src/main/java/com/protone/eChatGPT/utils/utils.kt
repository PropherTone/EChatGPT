package com.protone.eChatGPT.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.mods.TAG
import kotlinx.coroutines.*
import java.io.File

fun String.saveToFile(dir: String, fileName: String): String? {
    val fileDir = File(dir)
    if (!fileDir.isDirectory) return null
    return fileDir.takeIf { if (!it.exists()) it.mkdirs() else true }?.let { dirFile ->
        var path = "$dir${File.separator}$fileName"
        path += dirFile.listFiles()?.let { listFiles ->
            "_" + listFiles.count { childFile ->
                childFile.path == path
            }
        }
        val file = File(path)
        if (file.exists()) file.delete()
        file.takeIf { it.createNewFile() }?.let {
            it.writeText(this)
            path
        }
    }
}

fun String.toast() {
    Toast.makeText(EApplication.app, this, Toast.LENGTH_SHORT).show()
}

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

inline fun tryWithCatch(catch: (Exception) -> Unit = {}, func: () -> Unit) {
    try {
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
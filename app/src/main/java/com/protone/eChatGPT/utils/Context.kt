package com.protone.eChatGPT.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.protone.eChatGPT.EApplication
import kotlin.reflect.KClass

val KClass<*>.intent: Intent
    get() = Intent(EApplication.app, this.java)

val Context.layoutInflater: LayoutInflater
    get() = LayoutInflater.from(this)

val Context.isKeyboardActive
    get() = (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).isActive

fun Activity.hideKeyboard() {
    val inputManager: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputManager.isActive) {
        inputManager.hideSoftInputFromWindow(
            window.decorView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

@SuppressLint("ClickableViewAccessibility")
fun Context.linkInput(target: View, input: View) {
    target.setOnTouchListener { _, _ ->
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isActive) {
            inputManager.hideSoftInputFromWindow(
                input.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
        false
    }
}

@Suppress("DEPRECATION")
fun Activity.setTransparentClipStatusBar(isDarkText: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.isStatusBarContrastEnforced = false
    }
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    if (!isDarkText) {
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.TRANSPARENT
}

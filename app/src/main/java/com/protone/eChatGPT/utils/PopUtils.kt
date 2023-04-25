package com.protone.eChatGPT.utils

import android.app.Activity
import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import com.protone.eChatGPT.EApplication
import com.protone.eChatGPT.R
import com.protone.eChatGPT.databinding.ConfigurationLayoutBinding
import com.protone.eChatGPT.databinding.ShortcutEditDialogLayoutBinding
import com.protone.eChatGPT.repository.UserConfig
import com.protone.eChatGPT.repository.UserConfig.Shortcut.Companion.getResource
import com.protone.eChatGPT.repository.userConfig

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

fun Activity.showConfigurationPopupWindow(decorView: View, userConfig: UserConfig) {
    showPopupWindow(decorView) { pop ->
        ConfigurationLayoutBinding.inflate(layoutInflater).apply {
            root.setPadding(0, getStatusBarHeight()?.top ?: 0, 0, 0)

            keyEdit.isEnabled = false
            keyEdit.setText(userConfig.token)
            copy.setOnClickListener {
                keyEdit.text.toString().saveContentToClipBoard()
            }
            keyVisible.setOnCheckedChangedListener { _, isCheck ->
                if (edit.isChecked) return@setOnCheckedChangedListener
                keyEdit.transformationMethod =
                    if (isCheck) HideReturnsTransformationMethod.getInstance()
                    else PasswordTransformationMethod.getInstance()
            }
            edit.setOnCheckedChangedListener { _, isCheck ->
                keyEdit.isEnabled = isCheck
                keyVisible.isChecked = isCheck
                if (!isCheck) userConfig.token = keyEdit.text.toString()
            }

            userConfig.notificationShortcuts.forEach { shortcut ->
                shortcutContainer.addView(
                    ImageView(shortcutContainer.context).apply {
                        setBackgroundResource(R.drawable.main_color_background_ripple)
                        setImageResource(shortcut.getResource())
                        setPadding(resources.getDimensionPixelSize(R.dimen.content_padding))
                        setOnClickListener {
                            ShortcutEditDialogLayoutBinding.inflate(layoutInflater).apply binding@{
                                val dialog = AlertDialog.Builder(this@showConfigurationPopupWindow)
                                    .setView(this@binding.root)
                                    .create()
                                switchShortcut.setOnClickListener {
                                    dialog.dismiss()
                                }
                                removeShortcut.setOnClickListener {
                                    dialog.dismiss()
                                }
                                dialog.show()
                            }
                        }
                    },
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1f
                    )
                )
            }

            close.setOnClickListener {
                pop.dismiss()
            }
        }.root
    }
}
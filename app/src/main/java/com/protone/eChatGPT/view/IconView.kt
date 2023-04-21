package com.protone.eChatGPT.view

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import com.google.android.material.button.MaterialButton
import com.protone.eChatGPT.R
import com.protone.eChatGPT.databinding.IconViewLayoutBinding
import com.protone.eChatGPT.utils.layoutInflater

class IconView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding = IconViewLayoutBinding.inflate(context.layoutInflater, this, true)

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.IconView, 0, 0).also {
            setBackgroundColor(
                it.getColor(
                    R.styleable.IconView_iconViewBackgroundTint,
                    Color.TRANSPARENT
                )
            )
            binding.apply {
                val marginLayoutParams = text.layoutParams as MarginLayoutParams
                marginLayoutParams.setMargins(
                    it.getDimensionPixelSize(
                        R.styleable.IconView_iconTextMargin,
                        marginLayoutParams.topMargin
                    )
                )
                text.layoutParams = marginLayoutParams
                val color = it.getColor(R.styleable.IconView_iconTextColor, Color.BLACK)
                text.setTextColor(color)
                text.letterSpacing = it.getFloat(R.styleable.IconView_iconLetterSpacing, 0f)
                text.setPadding(it.getDimensionPixelSize(R.styleable.IconView_iconTextPadding, 0))
                text.textSize = it.getDimension(R.styleable.IconView_iconTextSize, 12f)
                text.text = it.getText(R.styleable.IconView_iconText)
                text.typeface = when (it.getInt(R.styleable.IconView_iconTextStyle, 0)) {
                    1 -> Typeface.DEFAULT_BOLD
                    2 -> Typeface.SERIF
                    else -> Typeface.DEFAULT
                }

                icon.setBackgroundColor(
                    it.getColor(
                        R.styleable.IconView_iconBackgroundTint,
                        Color.TRANSPARENT
                    )
                )
                icon.setImageDrawable(it.getDrawable(R.styleable.IconView_iconSrc))
                if (it.getBoolean(R.styleable.IconView_iconAutoPadding, false)) {
                    icon.post { icon.setPadding(icon.width / 4) }
                } else icon.setPadding(
                    it.getDimensionPixelSize(
                        R.styleable.IconView_iconPadding,
                        0
                    )
                )
                icon.background = it.getDrawable(R.styleable.IconView_iconBackground)
            }
        }.recycle()
    }

    var text
        get() = binding.text.text
        set(value) {
            binding.text.text = value
        }

    fun setImageResource(@DrawableRes idRes: Int) {
        binding.icon.setImageResource(idRes)
    }

}
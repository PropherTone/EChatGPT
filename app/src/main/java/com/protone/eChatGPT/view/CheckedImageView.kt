package com.protone.eChatGPT.view

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.appcompat.widget.AppCompatImageView

class CheckedImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    var isChecked = false
        set(value) {
            field = value
            refreshDrawableState()
        }

    private val checkState = intArrayOf(android.R.attr.state_checked)

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        if (isChecked) return mergeDrawableStates(
            super.onCreateDrawableState(extraSpace + 1),
            checkState
        )
        return super.onCreateDrawableState(extraSpace)
    }

    private var onCheckedChangeListener: OnCheckedChangeListener? = null
    fun setOnCheckedChangedListener(onCheckedChangeListener: OnCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener
    }

    init {
        setOnClickListener {
            isChecked = !isChecked
            onCheckedChangeListener?.onCheckedChanged(null,isChecked)
        }
    }
}
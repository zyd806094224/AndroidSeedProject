package com.demo.common.view.style

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.demo.common.view.drawable.ChipStyleDelegate
import com.demo.common.view.drawable.ChipCellDrawable.Companion.createFromAttributes

/**
 * @Description:
 * @Date: 2024/9/10 19:53
 * @author:  zhaoyudong
 * @version: 1.0
 */
open class ChipStyleLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null as AttributeSet?,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    val styleDelegate = ChipStyleDelegate()
    private fun loadFromAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val chipCellDrawable = createFromAttributes(context, attrs, defStyleAttr)
        background = chipCellDrawable
        styleDelegate.setDrawable(chipCellDrawable)
    }

    init {
        loadFromAttributes(context, attrs, defStyleAttr)
    }
}
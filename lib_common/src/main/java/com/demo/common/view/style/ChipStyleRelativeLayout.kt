package com.demo.common.view.style

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.demo.common.view.drawable.ChipCellDrawable.Companion.createFromAttributes
import com.demo.common.view.drawable.ChipStyleDelegate

/**
 * @Description:
 * @Date: 2024/9/10 19:55
 * @author:  zhaoyudong
 * @version: 1.0
 */
class ChipStyleRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null as AttributeSet?,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
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
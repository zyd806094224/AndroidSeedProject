package com.demo.common.text.span

import android.graphics.Color
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.URLSpan
import android.view.View

/**
 * @Description: 可设置颜色的 urlSpan
 * @Date: 2024/2/21 11:28 AM
 * @version: 1.0
 */
class HYURLSpan : URLSpan {
    private var color = Color.TRANSPARENT
    private var isUnderlineText: Boolean = false

    constructor(url: String) : super(url)

    constructor(url: String, color: Int, isUnderlineText: Boolean = false) : super(url) {
        this.color = color
        this.isUnderlineText = isUnderlineText
    }

    override fun updateDrawState(ds: TextPaint) {
        if (color != 0) {
            ds.color = this.color
        } else {
            ds.color = ds.linkColor
        }
        ds.isUnderlineText = this.isUnderlineText
    }

    override fun onClick(widget: View) {
        val urlStr = url
        if (!TextUtils.isEmpty(urlStr) && !urlStr.startsWith("http")) {
            val context = widget.context
//            HuangYeService.getRouterService().navigation(context, urlStr)
            return
        }
        super.onClick(widget)
    }

}
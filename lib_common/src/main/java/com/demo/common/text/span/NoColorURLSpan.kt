package com.demo.common.text.span

import android.text.TextPaint
import android.text.TextUtils
import android.view.View
import com.demo.html.html.spans.UrlSpan

/**
 *  解析富文本终  <hy_a href='xxxx'></hy_a> 的元素，支持自定义点击
 *  HtmlTagCtrlFactory.setText的时候最好增加OnSpanSetListener，因为span可能会刷新
 * @Description: 无颜色的 url span
 * @Date: 2024/2/21 11:28 AM
 * @version: 1.0
 */
class NoColorURLSpan : UrlSpan {

    private var isUnderlineText: Boolean = false


    @JvmOverloads
    constructor(url: String, isUnderlineText: Boolean = false) : super(url) {
        this.isUnderlineText = isUnderlineText
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = this.isUnderlineText
    }

    override fun onClick(widget: View) {
        if(onClickListener == null) {
            val urlStr = url
            if (!TextUtils.isEmpty(urlStr) && !urlStr.startsWith("http")) {
                val context = widget.context
//                HuangYeService.getRouterService().navigation(context, urlStr)
                return
            }
        }
        super.onClick(widget)
    }

}
package com.demo.html.html.spans

import android.text.TextPaint
import android.text.style.URLSpan
import android.view.View
import com.demo.html.html.listener.UrlClickListener

/**
 * @Description: 无颜色的 url span
 * @author:  zhangke
 * @version: 1.0
 */
open class UrlSpan : URLSpan {

    private var isUnderlineText: Boolean = false

    var onClickListener : UrlClickListener?=null

    @JvmOverloads
    constructor(url: String, isUnderlineText: Boolean = false) : super(url) {
        this.isUnderlineText = isUnderlineText
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = this.isUnderlineText
    }

    override fun onClick(widget: View) {
        val urlStr = url
        if(urlStr.isNullOrEmpty())return
        if(onClickListener != null){
            onClickListener?.onUrlClick(this, urlStr)
            return
        }
        super.onClick(widget)
    }

}
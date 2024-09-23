package com.demo.html.html.listener

import com.demo.html.html.spans.UrlSpan


/**
 * 可点击跳转的span，点击的回调
 **/
interface UrlClickListener {
    fun onUrlClick(span:UrlSpan, url:String)
}
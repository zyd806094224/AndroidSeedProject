package com.demo.html.html.listener

import android.text.Spanned

/**
 * span设置到text的回调，包括刷新
 **/
interface OnSpanSetListener {
    fun onSpanSet(span: Spanned?)
}
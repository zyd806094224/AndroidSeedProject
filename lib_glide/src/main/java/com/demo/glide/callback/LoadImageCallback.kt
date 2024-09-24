package com.demo.glide.callback

import android.graphics.drawable.Drawable

/**
 * @Description:
 * @Date: 2024/9/24 15:00
 * @author:  zhaoyudong
 * @version: 1.0
 */
interface LoadImageCallback<T> {
    fun onSuccess(url: String?, result: T)

    fun onFailure(url: String?, errorDrawable: Drawable?)

    fun onCancel(url: String?)
}
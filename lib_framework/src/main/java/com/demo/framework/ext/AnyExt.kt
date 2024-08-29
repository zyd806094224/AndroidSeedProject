package com.demo.framework.ext

/**
 * @Description:
 * @Date: 2024/8/29 15:57
 * @author:  zhaoyudong
 * @version: 1.0
 */

inline fun <reified T> Any.saveAs() : T{
    return this as T
}

@Suppress("UNCHECKED_CAST")
fun <T> Any.saveAsUnChecked() : T{
    return this as T
}

inline fun <reified T> Any.isEqualType() : Boolean{
    return this is T
}
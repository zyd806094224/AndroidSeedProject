package com.demo.common.service

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @Description: 提供主页模块对外能力，其他模块只需要按需添加，需要在Main模块实现
 * @Date: 2024/8/30 11:24
 * @author:  zhaoyudong
 * @version: 1.0
 */
interface IMainService : IProvider {

    /**
     * 跳转主页
     * @param context
     * @param index tab位置
     */
    fun toMain(context: Context, index: Int)
}
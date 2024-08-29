package com.demo.network.manager

import com.demo.network.api.ApiInterface

/**
 * @Description: api管理器
 * @Date: 2024/8/29 17:52
 * @author:  zhaoyudong
 * @version: 1.0
 */
object ApiManager {
    val api by lazy { HttpManager.create(ApiInterface::class.java) }
}
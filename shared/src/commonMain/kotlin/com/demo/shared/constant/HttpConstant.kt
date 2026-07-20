package com.demo.shared.constant

/**
 * @Description: 跨平台网络常量（从 lib_network 下沉）
 * @Date: 2024/8/29 17:47
 * @author:  zhaoyudong
 * @version: 1.0
 */

/**
 * base_url
 */
const val BASE_URL = "http://baidu.com"

/**
 * 请求超时时间（毫秒），与 BaseRepository/FlowExt 统一为 10s
 */
const val DEFAULT_TIMEOUT_MILLIS = 10_000L

/**
 * Flow 请求超时时间（毫秒）
 */
const val FLOW_TIMEOUT_MILLIS = 30_000L

/**
 * 默认重试次数（对应原 NetworkRetryInterceptor 的 retry-time header）
 */
const val DEFAULT_RETRY_COUNT = 3

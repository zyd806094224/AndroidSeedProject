package com.demo.shared.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Android actual：创建 OkHttp 引擎。
 * 原 lib_network 的 HttpManager 也用 OkHttp，行为一致。
 * 超时已在 HttpClient 层用 HttpTimeout 插件统一配置，引擎层用默认值。
 */
actual fun newHttpClientEngine(): HttpClientEngine = OkHttp.create()

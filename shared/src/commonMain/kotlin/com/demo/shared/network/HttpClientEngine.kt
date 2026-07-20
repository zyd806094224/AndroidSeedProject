package com.demo.shared.network

import io.ktor.client.engine.HttpClientEngine

/**
 * expect 声明：创建平台对应的 Ktor 引擎。
 * - Android actual：OkHttp 引擎（[io.ktor.client.engine.okhttp.OkHttp]）
 * - iOS actual：Darwin 引擎（[io.ktor.client.engine.darwin.Darwin]）
 *
 * 引擎的具体配置（超时、DNS 等）由各平台 actual 实现。
 */
expect fun newHttpClientEngine(): HttpClientEngine

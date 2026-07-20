package com.demo.shared.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

/**
 * iOS actual：创建 Darwin 引擎（基于 NSURLSession）。
 * 这是 iOS 上的原生 HTTP 实现，行为与 OkHttp 在 Android 上一致（由 Ktor common 层统一）。
 */
actual fun newHttpClientEngine(): HttpClientEngine = Darwin.create()

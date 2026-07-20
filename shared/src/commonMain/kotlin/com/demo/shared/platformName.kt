package com.demo.shared

/**
 * expect 声明：commonMain 只声明签名，具体实现由各平台 source set（androidMain / iosMain）的 actual 提供。
 * 这是 KMP 处理"平台差异代码"的核心机制。
 */
expect val platformName: String

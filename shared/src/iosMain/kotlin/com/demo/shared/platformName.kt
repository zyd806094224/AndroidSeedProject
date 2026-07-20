package com.demo.shared

import platform.UIKit.UIDevice

/**
 * iOS 平台的 actual 实现。使用 kotlin_OBJC 桥接 UIKit，三个 iOS 源集（arm64/x64/simulatorArm64）
 * 都会经由 iosMain 共享这一份实现。
 */
actual val platformName: String =
    UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

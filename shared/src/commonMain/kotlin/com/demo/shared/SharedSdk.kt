package com.demo.shared

/**
 * @Description: KMP shared module 对外入口示例。
 *               通过 expect/actual 演示平台差异机制：
 *                 - commonMain 用 expect 声明 [platformName]
 *                 - androidMain / iosMain 各自提供 actual 实现
 *               下沉的纯 Kotlin 数据模型（BaseResponse、ProjectTabItem）两端均可直接调用。
 */
object SharedSdk {

    /**
     * 返回带平台标识的问候语，用于验证 commonMain 代码两端都能跑通。
     */
    fun getGreeting(): String = "Hello from KMP shared! platform=$platformName"
}

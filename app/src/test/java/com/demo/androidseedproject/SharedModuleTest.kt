package com.demo.androidseedproject

import com.demo.shared.SharedSdk
import com.demo.shared.constant.BASE_URL
import com.demo.shared.error.ERROR
import com.demo.shared.model.BaseResponse
import com.demo.shared.model.ProjectTabItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * KMP shared module 验证测试（纯 JVM 单元测试，跑在开发机 JVM 上，无需设备）。
 *
 * 这里验证的是 [shared] 模块 commonMain 的代码，在被 Android target 编译后能被正常调用。
 * 等价于验证"commonMain 的纯 Kotlin 代码在 Android 平台的行为正确"。
 *
 * 运行方式：
 *   - 命令行：./gradlew :app:testDebugUnitTest --tests "com.demo.androidseedproject.SharedModuleTest"
 *   - Android Studio：右键本文件 -> Run 'SharedModuleTest'
 */
class SharedModuleTest {

    @Test
    fun `BaseResponse 成功时 isFailed 返回 false`() {
        // 服务端 Result.success() 返回 code=200
        val response = BaseResponse(data = "ok", code = 200, msg = "成功")
        assertEquals("ok", response.data)
        assertFalse(response.isFailed())
    }

    @Test
    fun `BaseResponse 失败时 isFailed 返回 true`() {
        val response = BaseResponse<String>(data = null, code = 1001, msg = "token 失效")
        assertTrue(response.isFailed())
        assertEquals(1001, response.code)
        assertEquals("token 失效", response.msg)
    }

    @Test
    fun `BaseResponse 泛型支持 List`() {
        val response = BaseResponse(
            data = listOf(ProjectTabItem(1, "首页"), ProjectTabItem(2, "我的")),
            code = 200
        )
        assertEquals(2, response.data?.size)
        assertEquals("首页", response.data?.first()?.name)
    }

    @Test
    fun `ProjectTabItem 数据类 equals 正常`() {
        assertEquals(ProjectTabItem(1, "tab"), ProjectTabItem(1, "tab"))
    }

    @Test
    fun `SharedSdk 问候语包含 Android 平台标识`() {
        val greeting = SharedSdk.getGreeting()
        // expect platformName 在 Android target 下 actual 返回 "Android"
        assertTrue("期望包含 platform=Android，实际：$greeting", greeting.contains("platform=Android"))
    }

    // ============ 阶段 2/3：Repository / 异常体系 / Ktor 网络栈 ============

    @Test
    fun `ERROR 枚举关键错误码正确`() {
        assertEquals(401, ERROR.UNAUTHORIZED.code)
        assertEquals(500, ERROR.INTERNAL_SERVER_ERROR.code)
        assertEquals(-1001, ERROR.UNLOGIN.code)
    }

    @Test
    fun `BASE_URL 是 http 明文地址`() {
        // 确认配置基线，便于排查 cleartextTraffic 问题
        assertTrue(BASE_URL.startsWith("http://"))
    }
}

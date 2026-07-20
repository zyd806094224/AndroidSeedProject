package com.demo.androidseedproject

import com.demo.shared.error.ApiException
import com.demo.shared.repository.LoginRepository
import com.demo.shared.repository.TokenManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

/**
 * KMP 业务逻辑测试：验证 [LoginRepository] 的入参校验逻辑。
 *
 * 这些校验逻辑写在 commonMain，两端共用，所以只在 Android JVM 跑一遍就能保证两端一致。
 * 真正的网络请求（[com.demo.shared.network.Api.login]）需要后端环境，这里只测纯逻辑部分。
 */
class LoginBusinessTest {

    private val repository = LoginRepository()

    @Before
    fun setUp() {
        TokenManager.clearToken()
    }

    @Test
    fun `用户名为空时 login 抛异常`() = runBlocking {
        try {
            repository.login(username = "", password = "123456")
            fail("应抛 ApiException")
        } catch (e: ApiException) {
            assertTrue(e.errMsg.contains("用户名"))
        }
    }

    @Test
    fun `密码为空时 login 抛异常`() = runBlocking {
        try {
            repository.login(username = "alice", password = "")
            fail("应抛 ApiException")
        } catch (e: ApiException) {
            assertTrue(e.errMsg.contains("密码"))
        }
    }

    @Test
    fun `用户名少于 3 字符时抛异常`() = runBlocking {
        try {
            repository.login(username = "ab", password = "123456")
            fail("应抛 ApiException")
        } catch (e: ApiException) {
            assertTrue(e.errMsg.contains("至少 3"))
        }
    }

    @Test
    fun `密码少于 6 字符时抛异常`() = runBlocking {
        try {
            repository.login(username = "alice", password = "12345")
            fail("应抛 ApiException")
        } catch (e: ApiException) {
            assertTrue(e.errMsg.contains("至少 6"))
        }
    }

    @Test
    fun `未登录时 isLoggedIn 返回 false`() {
        TokenManager.clearToken()
        assertFalse(repository.isLoggedIn())
    }

    @Test
    fun `保存 token 后 isLoggedIn 返回 true`() {
        TokenManager.saveToken("test_token_xyz")
        assertTrue(repository.isLoggedIn())
    }

    @Test
    fun `logout 后 token 被清除`() {
        TokenManager.saveToken("test_token_xyz")
        assertTrue(repository.isLoggedIn())
        repository.logout()
        assertFalse(repository.isLoggedIn())
    }

    @Test
    fun `getToken 返回保存的值`() {
        TokenManager.saveToken("abc123")
        assertEquals("abc123", TokenManager.getToken())
    }
}

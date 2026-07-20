# KMP 业务逻辑编写指南

本文档说明如何在 `shared` 模块的 `commonMain` 编写跨平台业务逻辑，并以**登录业务**为完整示例。

## 一、业务逻辑写在哪里

```
shared/src/commonMain/kotlin/com/demo/shared/
├── model/              ← ① 数据模型（@Serializable）
├── network/Api.kt      ← ② 网络接口（单个请求）
├── repository/         ← ③ 业务逻辑（核心！写这里）
│   ├── BaseRepository.kt
│   ├── XxxRepository.kt
│   └── TokenManager.kt  ← 平台能力抽象（expect/actual）
└── usecase/            ← ④ 业务编排（可选，复杂场景用）
    └── XxxUseCase.kt
```

## 二、分层原则

| 层 | 职责 | 何时写 |
|---|---|---|
| `model` | 纯数据类（请求/响应/实体） | 每个业务都要 |
| `Api` | 单个网络请求（原子操作） | 每个接口一个 suspend 函数 |
| `Repository` | **单业务的完整逻辑**（校验+调接口+处理+存储） | 业务逻辑的核心载体 |
| `UseCase` | 跨 Repository 的编排 | 简单业务可跳过 |

**判断要不要用 UseCase**：
- 一个 UI 动作只调一个 Repository → ViewModel 直接调 Repository，不用 UseCase
- 一个 UI 动作要组合多个 Repository（如登录后还要拉配置、初始化 IM）→ 用 UseCase 聚合

## 三、完整示例：登录业务

### 1. 数据模型（model/Login.kt）

```kotlin
@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val deviceId: String = ""
)

@Serializable
data class LoginInfo(
    val token: String,
    val userId: String,
    val nickname: String = "",
    val expireTime: Long = 0L
)
```

**要点**：
- `@Serializable` 注解是 KMP 的序列化方式（替代 Gson）
- 字段加默认值，便于服务端字段缺失时反序列化不崩

### 2. 网络接口（network/Api.kt）

```kotlin
suspend fun login(request: LoginRequest): BaseResponse<LoginInfo> {
    ensureNetworkAvailable()
    return httpClient.post("https://106.15.7.132:8443/user/login") {
        setBody(request)
    }.body()
}
```

**要点**：
- `Api.kt` 只管"发请求 + 拿响应"，**不做任何业务判断**
- `ensureNetworkAvailable()` 统一无网络检测
- 返回 `BaseResponse<T>`，errorCode 校验在 `BaseRepository` 做

### 3. 业务逻辑（repository/LoginRepository.kt）—— 核心

```kotlin
class LoginRepository : BaseRepository() {

    suspend fun login(username: String, password: String, deviceId: String = ""): LoginInfo {
        // ① 入参校验（跨平台纯逻辑，两端共用）
        validateCredentials(username, password)

        // ② 调接口（requestResponse 自动处理超时 + errorCode 校验）
        val result = requestResponse {
            Api.login(LoginRequest(username, password, deviceId))
        }

        // ③ 响应处理
        val loginInfo = result ?: throw ApiException(ERROR.PARSE_ERROR)

        // ④ 副作用——持久化 token（调 expect/actual 的 TokenManager）
        TokenManager.saveToken(loginInfo.token)

        return loginInfo
    }

    fun isLoggedIn(): Boolean = TokenManager.getToken().isNotEmpty()

    fun logout() = TokenManager.clearToken()

    private fun validateCredentials(username: String, password: String) {
        if (username.isBlank()) throw ApiException(ERROR.UNKNOWN.code, "用户名不能为空")
        if (password.length < 6) throw ApiException(ERROR.UNKNOWN.code, "密码至少 6 个字符")
        // ... 两端共用的校验规则
    }
}
```

**要点**：
- 继承 `BaseRepository` 复用超时/状态码校验
- **入参校验写在 commonMain**：改一处，两端同步生效，且有单测覆盖
- 业务逻辑里的"存 token"用 `expect/actual` 抽象（见下）

### 4. 平台能力抽象（repository/TokenManager.kt）

commonMain 声明接口：
```kotlin
expect object TokenManager {
    fun saveToken(token: String)
    fun getToken(): String
    fun clearToken()
}
```

androidMain 实现（接 MMKV/DataStore/内存）：
```kotlin
actual object TokenManager {
    actual fun saveToken(token: String) { MMKV.defaultMMKV().encode("token", token) }
    // ...
}
```

iosMain 实现（接 NSUserDefaults/Keychain）：
```kotlin
actual object TokenManager {
    actual fun saveToken(token: String) = NSUserDefaults.standardUserDefaults().setObject(token, "token")
    // ...
}
```

**这就是 KMP 处理"需要平台能力"的标准套路**：业务逻辑调 expect，编译时分派到各平台 actual。

### 5. 业务编排（usecase/LoginUseCase.kt，可选）

```kotlin
class LoginUseCase(private val repo: LoginRepository = LoginRepository()) {

    suspend fun execute(username: String, password: String): LoginResult {
        return try {
            val info = repo.login(username, password)
            LoginResult.Success(info, needGuide = info.nickname.isEmpty())
        } catch (e: ApiException) {
            LoginResult.Fail(e.errCode, e.errMsg)
        }
    }

    sealed class LoginResult {
        data class Success(val loginInfo: LoginInfo, val needGuide: Boolean) : LoginResult()
        data class Fail(val errCode: Int, val errMsg: String) : LoginResult()
    }
}
```

**UseCase 比 Repository 多做的事**：
- 把异常包成 Result（对 UI 更友好，尤其 iOS Swift 调用）
- 加场景化标志（如 needGuide）
- 组合多个 Repository（本例未演示，但模式一样）

## 四、Android 端怎么调用

```kotlin
// ViewModel 或 Activity
lifecycleScope.launch {
    val useCase = LoginUseCase()
    when (val result = useCase.execute("alice", "password123")) {
        is LoginUseCase.LoginResult.Success -> {
            // 更新 UI，跳转主页
            showMainPage(result.loginInfo, result.needGuide)
        }
        is LoginUseCase.LoginResult.Fail -> {
            // 显示错误
            showError(result.errMsg)
        }
    }
}
```

**注意**：Android 端 `when` 分支是**穷举**的（sealed class 特性），新增结果类型时编译器会提示你补全分支。

## 五、iOS 端怎么调用（接入后）

```swift
let useCase = LoginUseCase.shared
useCase.execute(username: "alice", password: "password123") { result in
    switch result {
    case .success(let info):
        print("登录成功: \(info.userId)")
    case .fail(let code, let msg):
        print("登录失败: \(code) \(msg)")
    }
}
```

（suspend 函数通过 KMP 的 iOS 桥接暴露成回调）

## 六、单元测试怎么写

业务逻辑写在 commonMain 的最大价值——**一套测试覆盖两端**：

```kotlin
@Test
fun `密码少于 6 字符时抛异常`() = runBlocking {
    try {
        repository.login(username = "alice", password = "12345")
        fail("应抛 ApiException")
    } catch (e: ApiException) {
        assertTrue(e.errMsg.contains("至少 6"))
    }
}
```

在 Android JVM 跑一遍，就能保证 iOS Native 的行为一致（因为跑的是同一份 commonMain 代码）。

运行：
```bash
./gradlew :app:testHuaweiDebugUnitTest --tests "*LoginBusinessTest"
```

## 七、新增业务的 checklist

写一个新业务（比如"订单列表"）的步骤：

1. **model**：新建 `model/Order.kt`，定义 `OrderInfo` 等 `@Serializable` 数据类
2. **Api**：在 `network/Api.kt` 加 `suspend fun getOrderList(...): BaseResponse<List<OrderInfo>>`
3. **Repository**：新建 `repository/OrderRepository.kt`，继承 `BaseRepository`，写业务逻辑
4. **UseCase**（可选）：如果逻辑复杂，新建 `usecase/OrderUseCase.kt`
5. **测试**：在 `app/src/test` 加 `OrderBusinessTest.kt`，覆盖入参校验等纯逻辑
6. **UI 调用**：在 ViewModel 调 UseCase/Repository

整个过程**不需要改 androidMain / iosMain 的任何 actual 实现**——除非业务用到了新的平台能力（如新的本地存储），那时才加新的 expect/actual。

## 八、关键原则总结

| 原则 | 说明 |
|---|---|
| **业务逻辑写 commonMain** | 跨端复用，一套测试覆盖两端 |
| **平台能力用 expect/actual** | 网络、存储、日志、系统 API 都这么抽象 |
| **Api 层只管请求** | 不做业务判断，业务判断交给 Repository |
| **Repository 是业务核心** | 校验、调用、处理、副作用都在这 |
| **UseCase 可选** | 简单业务跳过，复杂编排才用 |
| **sealed class 表达结果** | 对 UI 友好，编译期穷举检查 |

## 九、相关文档
- [KMP 网络层使用指南](./KMP_网络层使用指南.md)：Api 调用、Ktor 配置、异常处理
- [KMP 跨平台接入指南](./KMP_跨平台接入指南.md)：Android/iOS 接入、expect/actual 机制

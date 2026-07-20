# KMP 网络层使用指南

本项目的 KMP `shared` 模块提供了一套**跨平台的网络栈**（基于 Ktor），与原 `lib_network`（基于 Retrofit）并行共存。本文档说明如何使用、配置，以及两套实现的差异。

## 一、两套网络栈对照

| 维度 | lib_network（原有） | shared（KMP 新增） |
|---|---|---|
| 实现技术 | Retrofit + OkHttp + Gson | Ktor Client + kotlinx.serialization |
| 平台支持 | 仅 Android | Android + iOS（同一份 commonMain 代码） |
| 包名 | `com.demo.network.*` | `com.demo.shared.*` |
| 入口 | `ApiManager.api` | `Api`（`com.demo.shared.network.Api`） |
| 序列化 | Gson | kotlinx.serialization（`@Serializable`） |
| 线程调度 | `Dispatchers.IO` | `Dispatchers.Default`（iOS Native 无 IO 默认实现） |
| 状态 | 保留，现有代码继续使用 | 新代码推荐使用 |

**并存策略**：两套互不影响。老业务（HomeViewModel、TestViewModel 现有调用）继续用 `ApiManager.api`；新业务或需要跨平台的逻辑用 `Api`。

## 二、快速使用（Android 端）

### 1. 初始化

在 `Application.onCreate()` 中注入 Android Context（Ktor 网络状态检测需要）：

```kotlin
// MyApplication.kt
override fun onCreate() {
    super.onCreate()
    // ... 其他初始化
    com.demo.shared.network.SharedAndroidContext.init(this)
}
```

### 2. 发起请求

```kotlin
import com.demo.shared.network.Api
import kotlinx.coroutines.launch

// 在 ViewModel / Activity 的协程作用域内
lifecycleScope.launch {
    try {
        val response = Api.testRequest()          // suspend 函数
        // response: BaseResponse<String>
        println("code=${response.errorCode}, data=${response.data}")
    } catch (e: Exception) {
        // 异常已由 ExceptionHandler 统一映射为 ApiException
        println("请求失败: ${e.message}")
    }
}
```

### 3. 使用 Repository 模式（推荐）

```kotlin
import com.demo.shared.repository.BaseRepository
import com.demo.shared.network.Api

class HomeRepository : BaseRepository() {
    suspend fun loadDataList() = requestResponse {
        Api.getDataList()
    }
}

// ViewModel 调用
val data = homeRepository.loadDataList()   // 返回 List<String>?，失败抛 ApiException
```

### 4. 使用 Flow 模式

```kotlin
import com.demo.shared.flow.requestFlow

val data = requestFlow(
    errorBlock = { code, msg -> /* 错误处理 */ },
    requestCall = { Api.getDataList() }
)
```

## 三、添加新接口

### 步骤

1. 在 `shared/src/commonMain/kotlin/com/demo/shared/network/Api.kt` 中添加方法：

```kotlin
suspend fun getUserInfo(userId: String): BaseResponse<UserInfo> {
    ensureNetworkAvailable()
    return httpClient.get("user/info") {
        url { parameters.append("id", userId) }
    }.body()
}
```

2. 如果返回类型是自定义类，加 `@Serializable` 注解：

```kotlin
@Serializable
data class UserInfo(val id: Int, val name: String)
```

3. **不需要**修改 Android / iOS 任何 actual 代码（除非用到平台特有 API）。

## 四、Ktor 配置说明

HttpClient 的配置集中在 `HttpClientFactory.kt` 的 `createSharedHttpClient()`：

| 插件 | 作用 | 对应原 lib_network |
|---|---|---|
| `ContentNegotiation(json)` | JSON 序列化/反序列化 | GsonConverterFactory |
| `DefaultRequest` | 默认 baseUrl + Content-type header | HeaderInterceptor |
| `HttpTimeout` | 连接/请求/Socket 超时 10s | OkHttp connect/write/read timeout |
| `HttpRequestRetry` | 重试 3 次 + 指数退避 | NetworkRetryInterceptor |
| `Logging` | 请求/响应日志 | HttpLoggingInterceptor |

### JSON 配置

`sharedJson` 采用宽松策略：
- `ignoreUnknownKeys = true`：忽略服务端多余字段
- `isLenient = true`：宽松语法
- `encodeDefaults = true`：序列化输出默认值
- `explicitNulls = false`：不强制输出 null

### 网络状态检测

- **Android**：`ConnectivityManager` + `NetworkCapabilities`（需 `ACCESS_NETWORK_STATE` 权限，已在 shared Manifest 声明）
- **iOS**：当前返回 `true`（TODO，完整实现需 `NWPathMonitor`）

## 五、异常处理

所有异常统一通过 `ExceptionHandler.handleException()` 转换为 `ApiException`：

| 原始异常 | 映射结果 |
|---|---|
| Ktor `ClientRequestException` (4xx) | 按状态码映射（401→UNAUTHORIZED 等） |
| Ktor `ServerResponseException` (5xx) | 按状态码映射（500→INTERNAL_SERVER_ERROR 等） |
| Ktor `JsonConvertException` | PARSE_ERROR |
| Ktor `HttpRequestTimeoutException` | TIMEOUT_ERROR |
| `NoNetWorkException` | NETWORK_ERROR |
| 协程 `TimeoutCancellationException` | TIMEOUT_COROUTINE_ERROR |
| 其他 | UNKNOWN（含 message） |

## 六、常见问题

### Q1: 请求报错 "CLEARTEXT communication not permitted"

项目 `usesCleartextTraffic="false"`，而 `BASE_URL` 是 `http://`。解决方案：
- 生产环境用 HTTPS
- 调试时在 `app/src/main/AndroidManifest.xml` 的 `<application>` 加 `android:networkSecurityConfig` 白名单

### Q2: iOS 编译报找不到 NWPathMonitor

iOS 的 NetworkChecker 当前是简化实现（返回 true）。完整实现需要正确配置 cinterop，参考 [Kotlin/Native interop 文档](https://kotlinlang.org/docs/native_objc_interop.html)。

### Q3: kotlinx-serialization 和 Gson 的 BaseResponse 有什么区别？

- `lib_network` 的 `BaseResponse` 用 Gson（无注解）
- `shared` 的 `BaseResponse` 用 kotlinx.serialization（需 `@Serializable`）
- 两者字段一致，但**不互通**。跨模块传递数据时用 shared 版本。

### Q4: 为什么用 `Dispatchers.Default` 而不是 `Dispatchers.IO`？

`Dispatchers.IO` 在 Kotlin/Native（iOS）上没有默认实现，commonMain 中使用会导致 metadata 编译失败。`Dispatchers.Default` 在所有平台都可用，Android 上它同样是后台线程池。

## 七、目录结构

```
shared/src/commonMain/kotlin/com/demo/shared/
├── network/
│   ├── Api.kt                    # Ktor 版接口入口
│   ├── HttpClientFactory.kt      # HttpClient 配置 + Ktor 插件
│   ├── HttpClientEngine.kt       # expect: 平台引擎声明
│   ├── NetworkChecker.kt         # expect: isNetworkAvailable()
│   ├── NetworkCheck.kt           # ensureNetworkAvailable()
│   └── SharedHttpLogger.kt       # Ktor 日志适配
├── repository/BaseRepository.kt
├── flow/FlowExt.kt
├── error/
│   ├── ERROR.kt
│   ├── ApiException.kt
│   └── ExceptionHandler.kt
├── constant/HttpConstant.kt
├── callback/IApiErrorCallback.kt
└── logger/AppLog.kt              # expect: 跨平台日志

shared/src/androidMain/kotlin/com/demo/shared/
├── network/
│   ├── HttpClientEngine.kt       # actual: OkHttp 引擎
│   └── NetworkChecker.kt         # actual: ConnectivityManager + SharedAndroidContext
└── logger/AppLog.kt              # actual: android.util.Log

shared/src/iosMain/kotlin/com/demo/shared/
├── network/
│   ├── HttpClientEngine.kt       # actual: Darwin 引擎
│   └── NetworkChecker.kt         # actual: TODO (NWPathMonitor)
└── logger/AppLog.kt              # actual: NSLog
```

# KMP 跨平台接入指南

本文档说明如何把 `shared` KMP 模块接入到不同平台。当前项目已配置 Android + iOS 两个目标平台。

## 一、整体架构

```
┌─────────────────────────────────────────┐
│            commonMain（共享）              │
│  数据模型 / Repository / Ktor 网络栈 /    │
│  异常体系 / 业务逻辑                       │
└────────────┬────────────────────────────┘
             │ expect 声明
   ┌─────────┴─────────┐
   ▼                   ▼
┌─────────┐      ┌──────────┐
│androidMain│     │  iosMain  │
│ actual   │      │  actual   │
├─────────┤      ├──────────┤
│ OkHttp   │      │  Darwin  │
│ ConnectMgr│     │ NWPath*  │
│ Log      │      │  NSLog   │
└─────────┘      └──────────┘
```

**核心思想**：平台无关的业务逻辑写在 `commonMain`（编译成各平台产物），平台差异通过 `expect/actual` 机制桥接。

## 二、Android 接入（已完成）

Android 端是最简单的——KMP module 的 Android target 编译成普通 AAR，和其他 Android library 用法一致。

### 1. 添加依赖

```groovy
// app/build.gradle
dependencies {
    implementation project(path: ':shared')
}
```

### 2. 初始化 Context + Debug 标志（必须）

```kotlin
// MyApplication.kt
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 必须调用：传入 BuildConfig.DEBUG 控制 SSL 策略
        //   - Debug：信任所有证书（便于 Charles/Fiddler 抓包）
        //   - Release：仅信任内置的自签名证书（shared/res/raw/server_cert.pem）
        // 不调用会导致首次网络请求抛 IllegalStateException
        SharedAndroidContext.init(this, BuildConfig.DEBUG)
    }
}
```

> 注意：`BuildConfig` 是接入方 app 模块自动生成的，不是 shared 的。

### 3. SSL 证书适配（自签名证书场景）

**shared 模块已自包含证书适配**，证书文件 `server_cert.pem` 打包在 `shared/src/androidMain/res/raw/`，随 AAR 分发。

- **Ktor 请求**（`Api.xxx()`）：SSL pinning 在 shared 内部自动生效，接入方无需任何配置 ✅
- **WebView / 系统网络 / 图片库等非 Ktor 链路**：shared 的 pinning 管不到。如果接入方也需要访问自签名 HTTPS 服务，需在 app 的 `network_security_config.xml` 自行信任证书：

```xml
<!-- 接入方的 app/src/main/res/xml/network_security_config.xml -->
<trust-anchors>
    <certificates src="system" />
    <certificates src="@raw/server_cert"/>  <!-- 接入方需自己放一份证书 -->
</trust-anchors>
```

> 注意：证书资源是 shared 自己的，app 的 NetworkSecurityConfig 无法引用 shared 的 `@raw/server_cert`，接入方需在自己的 `app/res/raw/` 再放一份。本工程（AndroidSeedProject）已经这么做了。

### 4. 调用

直接 import `com.demo.shared.*` 即可。详见 [KMP_网络层使用指南.md](./KMP_网络层使用指南.md)。

## 三、iOS 接入（待实跑验证）

> 当前阶段只做到**编译验证 + framework 生成**，未实际接入 Xcode 工程跑通。以下是接入步骤说明。

### 1. 生成 iOS Framework

执行构建任务生成 framework：

```bash
# Debug 版（开发调试用）
./gradlew :shared:assembleSharedFrameworkDebugIOSArm64

# Release 版（发布用）
./gradlew :shared:assembleSharedFrameworkReleaseIOSArm64
```

产物位置：
```
shared/build/bin/iosArm64/debugFramework/Shared.framework
shared/build/bin/iosArm64/releaseFramework/Shared.framework
```

三个架构的 framework：
- `iosArm64/`：真机（arm64）
- `iosX64/`：模拟器（Intel Mac）
- `iosSimulatorArm64/`：模拟器（Apple Silicon Mac）

### 2. Xcode 工程配置

**步骤 1**：把 `Shared.framework` 拖入 Xcode 工程（或通过 Linked Frameworks 添加）

**步骤 2**：在 Target → Build Phases → Link Binary With Libraries 添加 `Shared.framework`

**步骤 3**：在 Target → General → Frameworks, Libraries, and Embedded Content，把 `Shared.framework` 设为 `Embed & Sign`

**步骤 4**：添加 Framework Search Paths（如果 framework 不在默认目录）

### 3. Swift 调用示例

```swift
import Shared
import KotlinCoroutinesCombine

// 调用跨平台代码
let greeting = SharedSdk.shared.getGreeting()
print("KMP: \(greeting)")  // 输出: Hello from KMP shared! platform=iOS 17.x

// 发起网络请求（Ktor，suspend 函数需要协程桥接）
Api.shared.testRequest { response, error in
    if let response = response {
        print("请求成功: code=\(response.errorCode), data=\(response.data ?? "")")
    } else if let error = error {
        print("请求失败: \(error)")
    }
}
```

> **注意**：Kotlin 的 `suspend` 函数在 Swift 中需要通过回调或 Combine 桥接。推荐用 [Kotlin Coroutines 的 iOS 集成方案](https://kotlinlang.org/docs/mobile/integrate-in-existing-app.html)。

### 4. iOS 端限制与 TODO

- **网络状态检测**：当前 `isNetworkAvailable()` 返回 `true`（未实现 NWPathMonitor）
- **协程桥接**：需要额外引入 `kotlinx-coroutines-core` 的 iOS bridging
- **Framework 体积**：静态 framework（`isStatic = true`），会直接链接进 app

## 四、添加新的平台目标（JVM / JS / 桌面）

### 以 JVM（桌面/后端）为例

1. 在 `shared/build.gradle.kts` 的 `kotlin {}` 块加：

```kotlin
jvm {
    compilations.all {
        kotlinOptions.jvmTarget = "1.8"
    }
}
```

2. 加 JVM 专属 Ktor 引擎依赖（可选，JVM 默认用 CIO 引擎）：

```kotlin
val jvmMain by getting {
    dependencies {
        implementation("io.ktor:ktor-client-cio:2.3.0")
    }
}
```

3. 在 `shared/src/jvmMain/` 下新建 actual 实现（`HttpClientEngine.kt`、`NetworkChecker.kt`、`AppLog.kt`）

4. 验证：`./gradlew :shared:jvmJar`

## 五、expect/actual 机制详解

KMP 处理平台差异的核心机制：

### 声明（commonMain）

```kotlin
// commonMain：只声明，不实现
expect val platformName: String
expect fun isNetworkAvailable(): Boolean
expect fun newHttpClientEngine(): HttpClientEngine
```

### 实现（各平台 source set）

```kotlin
// androidMain
actual val platformName: String = "Android"
actual fun isNetworkAvailable(): Boolean { /* ConnectivityManager */ }
actual fun newHttpClientEngine(): HttpClientEngine = OkHttp.create()

// iosMain
actual val platformName: String = UIDevice.currentDevice.systemName + ...
actual fun isNetworkAvailable(): Boolean = true  // TODO
actual fun newHttpClientEngine(): HttpClientEngine = Darwin.create()
```

**规则**：
- 每个 `expect` 必须在每个 target 都有对应的 `actual`
- `actual` 的签名必须和 `expect` 完全一致
- commonMain 可以调用 `expect`，编译期会分派到各平台的 `actual`

## 六、调试技巧

### Android 端验证

```bash
# 单元测试（纯 JVM）
./gradlew :app:testHuaweiDebugUnitTest --tests "com.demo.androidseedproject.SharedModuleTest"

# 编译 APK
./gradlew :app:assembleHuaweiDebug

# Logcat 过滤 KMP 日志
adb logcat -s SharedHttp:D zzz:E TestActivity:E
```

### iOS 端验证（编译层）

```bash
# 完整构建（含 iOS 三架构 + framework）
./gradlew :shared:build

# 单独编译 iOS arm64
./gradlew :shared:compileKotlinIosArm64

# 检查 framework 产物
ls -la shared/build/bin/iosArm64/debugFramework/Shared.framework/
```

### 验证 expect/actual 分派

在 TestActivity 点 "KMP共享模块测试" 按钮，看到 `platform=Android` 说明 `platformName` 的 actual 在 Android 正确分派。iOS 端接入后调 `SharedSdk.shared.getGreeting()` 应看到 `platform=iOS x.x`。

## 七、版本与兼容性说明

当前工具链组合（已验证可用）：

| 组件 | 版本 | 说明 |
|---|---|---|
| Kotlin | 1.8.0 | KMP 最低推荐版本 |
| AGP | 7.4.2 | Android Gradle Plugin |
| Gradle | 7.5.1 | 构建工具 |
| Ktor | 2.3.0 | 最后支持 Kotlin 1.8.x 的稳定版 |
| kotlinx-coroutines | 1.6.4 | 与 Ktor 2.3.0 匹配 |
| kotlinx-serialization | 1.5.0 | 与 Ktor 2.3.0 匹配 |

**升级建议**：未来如需 Compose Multiplatform 或更稳定的 KMP 支持，建议升级到 Kotlin 2.0+ / AGP 8.x / Gradle 8.x。

## 八、相关文档

- [KMP 网络层使用指南](./KMP_网络层使用指南.md)：Ktor 配置、Api 调用、异常处理
- [Kotlin Multiplatform 官方文档](https://kotlinlang.org/docs/multiplatform.html)
- [Ktor 客户端文档](https://ktor.io/docs/client-create-new-application.html)
- [Kotlin/Native 与 Swift 互操作](https://kotlinlang.org/docs/native_objc_interop.html)

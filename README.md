# AndroidSeedProject - 现代化 Android 应用开发框架

## 📖 项目简介

**AndroidSeedProject** 是一个基于 Kotlin 语言开发的现代化 Android 应用种子工程，采用完整的模块化架构设计，旨在为 Android 开发者提供一个快速搭建高质量应用的生产级框架。项目严格遵循 Google 官方推荐的 Android 开发最佳实践，集成了现代 Android 开发生态系统中成熟的组件和库。

### 🎯 项目定位

- **快速启动**：新项目开发的标准起点
- **生产就绪**：包含完整的生产环境配置和最佳实践
- **学习参考**：现代 Android 架构模式的最佳实践示例
- **团队规范**：为团队开发提供统一的代码规范和架构模式

## 🏗️ 项目架构

### 模块化设计架构

```
AndroidSeedProject
├── app/                      # 📱 主应用模块 (APK)
├── mod_main/                 # 🏠 主要业务模块
├── lib_framework/            # ⚙️ 框架核心库
├── lib_network/              # 🌐 网络请求库
├── lib_common/               # 🛠️ 公共工具库
├── lib_room/                 # 💾 本地数据库库
├── lib_html/                 # 🌐 HTML 展示库
├── lib_glide/                # 🖼️ 图片加载库
└── lib_universaldialog/      # 💬 通用对话框库
```

### 模块职责说明

| 模块 | 职责 | 主要功能 |
|------|------|----------|
| **app** | 应用主入口 | 应用程序配置、启动逻辑、多渠道打包 |
| **mod_main** | 主业务模块 | 首页、我的页面、主要业务逻辑 |
| **lib_framework** | 框架核心 | BaseActivity/Fragment、MVVM 基础类、生命周期管理 |
| **lib_network** | 网络模块 | Retrofit 封装、OkHttp 配置、网络异常处理 |
| **lib_common** | 公共模块 | 工具类、常量、路由服务、权限管理 |
| **lib_room** | 数据库模块 | Room 数据库配置、DAO 封装、数据迁移 |
| **lib_html** | HTML 展示 | WebView 封装、HTML 内容处理 |
| **lib_glide** | 图片加载 | Glide 配置、图片处理工具 |
| **lib_universaldialog** | 对话框 | 通用对话框、弹窗组件 |

## ✨ 核心功能特性

### 🎨 UI 框架与架构

- **🏛️ MVVM 架构模式**：基于 DataBinding 的完整 MVVM 实现
- **📱 Jetpack 组件**：LiveData、ViewModel、Lifecycle 完整集成
- **🎭 DataBinding/ViewBinding**：数据绑定和视图绑定双重支持
- **🌊 响应式编程**：基于 Kotlin Coroutines 的异步编程
- **🔄 生命周期感知**：自动的生命周期管理和资源释放

### 🌐 网络请求

- **📡 Retrofit + OkHttp**：现代化的网络请求框架
- **📝 请求/响应日志**：完整的网络请求日志记录
- **🔧 统一异常处理**：网络异常的统一处理和用户友好提示
- **🌍 国际化支持**：多语言环境下的网络请求适配
- **🍪 Cookie 管理**：自动的 Cookie 保存和管理
- **⚡ 请求拦截器**：统一的请求头添加和参数处理

### 💾 数据存储

- **🏢 Room 数据库**：现代化本地数据库解决方案
- **📊 数据库迁移**：平滑的数据库版本升级支持
- **⚡ 异步数据库操作**：基于 Coroutines 的异步数据库访问
- **🔍 数据查询封装**：简洁的 DAO 层封装
- **📈 数据缓存策略**：智能的数据缓存机制

### 🛠️ 通用组件

- **🔐 权限管理**：基于 XXPermissions 的简化权限申请
- **🖼️ 图片加载**：Glide 的完整配置和优化
- **🔄 下拉刷新**：SmartRefreshLayout 集成
- **🎯 路由导航**：ARouter 框架的完整集成
- **💬 通用对话框**：可复用的对话框组件
- **📏 多媒体支持**：ExoPlayer 音视频播放
- **🌐 WebView 增强**：WebView 的完整封装和优化

## 📱 系统兼容性与适配

### Android 版本支持

| 配置项 | 版本 | 说明 |
|--------|------|------|
| **编译版本** | `compileSdk 34` | Android 14 |
| **目标版本** | `targetSdk 34` | Android 14 |
| **最低版本** | `minSdk 23` | Android 6.0 (Marshmallow) |
| **构建工具** | 自动管理 | AGP 自动选择最佳版本 |

### 🔧 Android 14 (API 34) 适配详情

#### ✅ 已完成的适配

- **权限系统优化**：精确的运行时权限声明
- **前台服务规范**：前台服务类型声明（如使用）
- **网络安全配置**：完整的网络安全策略
- **组件导出控制**：所有组件明确设置 `android:exported`
- **隐式 Intent 限制**：避免使用隐式 Intent
- **广播接收器规范**：正确的导出属性设置
- **后台活动限制**：遵循后台启动活动的新规则

#### 📋 权限声明清单

```xml
<!-- 核心权限 -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />

<!-- 媒体权限 (Android 14 推荐) -->
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />

<!-- 兼容性权限 -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

### 🔒 安全配置

- **网络安全策略**：完整的网络安全配置
- **明文传输控制**：`android:usesCleartextTraffic="false"`
- **证书锁定**：支持 HTTPS 证书验证
- **代码混淆**：ProGuard/R8 混淆配置
- **签名配置**：Debug/Release 签名分离

## 🛠️ 技术栈详解

### 核心技术

| 技术 | 版本 | 用途 |
|------|------|------|
| **Kotlin** | 1.8.0 | 主要开发语言 |
| **Android Gradle Plugin** | 7.4.2 | 构建工具 |
| **Coroutines** | 内置 | 异步编程 |
| **DataBinding** | 内置 | 数据绑定 |
| **ViewBinding** | 内置 | 视图绑定 |

### 架构组件

| 组件 | 版本 | 说明 |
|------|------|------|
| **Lifecycle** | 2.4.0 | 生命周期管理 |
| **LiveData** | 2.4.0 | 响应式数据 |
| **ViewModel** | 2.4.0 | MVVM 视图模型 |
| **Room** | 2.5.0 | 本地数据库 |
| **Navigation** | 2.3.5 | 导航组件 |

### 第三方库

| 库名 | 版本 | 功能 |
|------|------|------|
| **Retrofit** | 2.9.0 | 网络请求 |
| **OkHttp** | 3.11.0 | HTTP 客户端 |
| **Gson** | 2.10.1 | JSON 解析 |
| **Glide** | 4.15.0 | 图片加载 |
| **ARouter** | 1.5.2 | 路由导航 |
| **XXPermissions** | 26.8 | 权限管理 |
| **MMKV** | 1.2.15 | 键值存储 |
| **SmartRefreshLayout** | 2.0.5 | 下拉刷新 |
| **ExoPlayer** | 2.18.5 | 音视频播放 |
| **TBS SDK** | 44132 | 腾讯X5内核 |

## 🚀 快速开始

### 📋 环境要求

- **Android Studio**：Arctic Fox 或更高版本
- **JDK**：8 或更高版本
- **Android SDK**：API 34 (Android 14)
- **Gradle**：7.0 或更高版本
- **Kotlin**：1.8.0 (项目已配置)

### ⚙️ 快速启动步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/zyd806094224/AndroidSeedProject.git
   cd AndroidSeedProject
   ```

2. **导入 Android Studio**
   - 打开 Android Studio
   - 选择 "Open an existing Android Studio project"
   - 选择项目根目录

3. **同步项目**
   - 等待 Gradle 同步完成
   - 如果遇到依赖下载问题，可尝试使用阿里云镜像

4. **配置签名**
   - 项目已内置 Demo 签名文件
   - 生产环境请替换为正式签名

5. **运行项目**
   ```bash
   # 命令行构建
   ./gradlew assembleDebug

   # 运行到设备
   ./gradlew installDebug
   ```

### 🎯 多渠道构建

项目支持多渠道打包：

```bash
# 华为渠道
./gradlew assembleHuaweiDebug

# 小米渠道
./gradlew assembleXiaomiDebug

# 生产构建
./gradlew assembleHuaweiRelease
./gradlew assembleXiaomiRelease
```

## 📱 应用功能展示

### 🏠 主要功能模块

1. **首页模块**
   - 数据列表展示
   - 下拉刷新/上拉加载
   - 网络状态监控
   - 错误页面处理

2. **个人中心**
   - 用户信息展示
   - 权限测试功能
   - 路由跳转测试
   - 应用设置选项

3. **WebView 页面**
   - 完整的 WebView 封装
   - 进度条显示
   - 错误页面处理
   - 分享功能集成

4. **编辑页面**
   - 多行文本输入
   - 字数统计
   - 内容保存和读取

### 🔗 路由系统

项目实现了完整的外部路由系统，支持：

- **自定义协议**：`seedapp://`
- **HTTP 协议**：`https://seedapp.com`
- **参数传递**：支持多种数据类型
- **路由拦截**：参数验证和权限检查
- **错误处理**：不支持的链接有回退机制

详细的路由使用说明请参考 [路由系统使用说明.md](./路由系统使用说明.md)

## 🛠️ 开发指南

### 📝 代码规范

项目遵循以下代码规范：

- **Kotlin 编码规范**：遵循官方 Kotlin 编码约定
- **Android 架构指南**：遵循 Google 官方架构建议
- **命名规范**：统一的包名、类名、方法名命名
- **注释规范**：完整的类和方法注释

### 🔧 添加新功能

#### 添加新页面

1. **创建 Activity/Fragment**
   ```kotlin
   @Route(path = ARouterPath.NEW_ACTIVITY)
   class NewActivity : BaseMvvmActivity<ActivityNewBinding, NewViewModel>() {
       override fun initView(savedInstanceState: Bundle?) {
           super.initView(savedInstanceState)
       }
   }
   ```

2. **添加路由配置**
   ```kotlin
   // ARouterPath.kt
   const val NEW_ACTIVITY = "/new/activity"

   // RouteConfig.kt
   val ROUTE_MAP = mapOf(
       "new" to NEW_ACTIVITY
   )
   ```

3. **更新导航**
   ```kotlin
   // 导航到新页面
   ARouter.getInstance()
       .build(NEW_ACTIVITY)
       .navigation(context)
   ```

#### 添加网络请求

1. **定义 API 接口**
   ```kotlin
   interface ApiService {
       @GET("api/data")
       suspend fun getData(): Response<DataModel>
   }
   ```

2. **在 Repository 中调用**
   ```kotlin
   class DataRepository {
       suspend fun fetchData(): Result<DataModel> {
           return try {
               val response = apiService.getData()
               Result.success(response.data)
           } catch (e: Exception) {
               Result.failure(e)
           }
       }
   }
   ```

### 🧪 测试指南

#### 运行测试

```bash
# 单元测试
./gradlew test

# UI 测试
./gradlew connectedAndroidTest

# 生成测试报告
./gradlew jacocoTestReport
```

#### 路由测试

使用 ADB 命令测试路由功能：

```bash
# 测试 WebView 页面
adb shell am start -W -a android.intent.action.VIEW \
  -d "seedapp://web/activity?url=https://www.baidu.com&title=百度" \
  com.demo.androidseedproject

# 测试编辑页面
adb shell am start -W -a android.intent.action.VIEW \
  -d "seedapp://edit/activity?content=测试内容" \
  com.demo.androidseedproject
```

## 🌿 分支说明

本项目包含多个功能分支，用于演示不同的 Android 技术和开发模式：

### 分支列表

| 分支名称 | 功能描述 | 状态 | 学习价值 |
|---------|---------|------|---------|
| **master** | 主分支，基于传统 View 系统的完整 MVVM 架构应用 | ✅ 稳定 | 生产级项目架构参考 |
| **flutter-hybrid-development** | Flutter 混合开发示例 | ✅ 可用 | Flutter 与 Android 原生混合开发技术 |
| **android-compose-dev** | Jetpack Compose UI 相关开发示例 | ✅ 可用 | 现代声明式 UI 开发模式 |
| **360-replugin-dev** | 基于 360 RePlugin 的插件化使用示例 | ⚠️ 已停止维护 | 了解插件化机制和实现原理 |

### 分支详细说明

#### 1️⃣ flutter-hybrid-development - Flutter 混合开发

**技术栈：**
- Flutter SDK
- Flutter Engine 嵌入
- Flutter Boost 混合栈框架
- Platform Channel 通信

**核心特性：**
- Flutter 页面嵌入 Android 原生项目
- 原生与 Flutter 页面无缝跳转
- 数据双向通信机制
- 生命周期管理
- 混合栈路由管理

**适用场景：**
- 需要快速开发复杂 UI 页面
- 跨平台复用 Flutter 代码
- 渐进式 Flutter 接入

**切换分支：**
```bash
git checkout flutter-hybrid-development
```

#### 2️⃣ android-compose-dev - Jetpack Compose 开发

**技术栈：**
- Jetpack Compose (现代化 UI 框架)
- Material Design 3
- Compose Navigation
- Compose ViewModel

**核心特性：**
- 声明式 UI 编程
- 状态管理最佳实践
- 自定义 Compose 组件
- 动画和手势处理
- Compose 与传统 View 混用

**适用场景：**
- 新项目使用现代化 UI 框架
- 学习声明式编程范式
- 提升 UI 开发效率

**切换分支：**
```bash
git checkout android-compose-dev
```

#### 3️⃣ 360-replugin-dev - 插件化开发（学习用途）

**⚠️ 重要提示：** 360 RePlugin 已停止维护，不建议在生产环境使用。此分支仅用于学习和了解插件化机制。

**技术栈：**
- 360 RePlugin 框架
- 插件加载机制
- 宿主-插件通信
- 资源隔离

**核心特性：**
- 插件独立编译和部署
- 插件热更新能力
- 宿主与插件隔离
- 插件生命周期管理
-四大组件插件化支持

**学习价值：**
- 理解 Android 插件化原理
- 了解 ClassLoader 机制
- 学习资源冲突解决方案
- 掌握插件化架构设计思路

**技术替代方案：**
- Dynamic Feature Modules (官方推荐)
- App Bundles (动态分发)
- Modular Architecture (模块化架构)

**切换分支：**
```bash
git checkout 360-replugin-dev
```

### 如何选择分支

**根据项目需求选择：**
- 🏢 **生产项目** → 使用 `master` 分支（稳定、成熟）
- 🚀 **新 UI 开发** → 使用 `android-compose-dev`（现代化）
- 🔗 **混合开发** → 使用 `flutter-hybrid-development`（跨平台）
- 📚 **学习研究** → 使用 `360-replugin-dev`（了解插件化）

## 📦 构建与发布

### 🔨 构建配置

项目使用统一的构建配置：

```gradle
android {
    compileSdk 34
    defaultConfig {
        minSdk 23
        targetSdk 34
        versionCode 1
        versionName "1.0.1"
    }
}
```

### 📱 多渠道打包

```bash
# Debug 版本
./gradlew assembleDebug

# Release 版本
./gradlew assembleRelease

# 多渠道构建
./gradlew assembleHuaweiRelease assembleXiaomiRelease
```

### 🔄 CI/CD 支持

项目支持自动化构建：

- **GitHub Actions**：`.github/workflows/android.yml`
- **Jenkins**：`jenkins/Jenkinsfile`
- **GitLab CI**：`.gitlab-ci.yml`

### 📊 性能优化

项目包含多项性能优化：

- **启动优化**：应用启动时间优化
- **内存优化**：内存泄漏检测和优化
- **网络优化**：网络请求缓存和重试机制
- **UI 优化**：布局优化和渲染优化
- **包体积优化**：资源压缩和代码混淆

# AndroidSeedProject 安卓种子工程

## 项目简介

AndroidSeedProject 是一个基于 Kotlin 语言开发的 Android 应用程序种子项目，采用模块化架构设计，旨在为 Android 开发者提供一个快速搭建高质量应用的框架。该项目集成了 Android 开发中常用的组件和库，遵循现代 Android 开发最佳实践。

## 项目架构

项目采用模块化设计，主要包括以下模块：

- `app`: 主应用模块
- `lib_framework`: 框架核心库，提供基础组件和工具类
- `lib_network`: 网络库，基于 Retrofit 和 OkHttp 实现网络请求
- `lib_common`: 公共库，包含通用工具类和组件
- `lib_room`: 数据库库，基于 Room 实现本地数据存储
- `mod_main`: 主业务模块
- `lib_html`: HTML 相关功能库
- `lib_glide`: 图片加载库
- `lib_universaldialog`: 通用对话框库

## 核心功能

### 1. 基础框架
- BaseActivity/BaseFragment: 提供基础的 Activity 和 Fragment 封装
- DataBinding 支持: 实现 MVVM 架构模式
- 生命周期管理: 统一的生命周期回调处理

### 2. 网络请求
- 基于 Retrofit + OkHttp 的网络请求框架
- 支持请求/响应日志打印
- 统一的异常处理机制
- 网络状态检测和拦截
- 请求头和 Cookie 拦截器

### 3. 本地数据存储
- 基于 Room 的数据库操作
- 数据库版本升级支持
- DAO 层封装

### 4. 其他功能组件
- 权限管理
- 图片加载 (Glide)
- 下拉刷新
- 多媒体支持
- 路由框架 (ARouter)
- 通用对话框
- HTML 内容展示

## 技术栈

- 编程语言: Kotlin
- 网络框架: Retrofit + OkHttp
- 图片加载: Glide
- 数据库: Room
- 架构模式: MVVM
- UI 框架: Jetpack Components
- 路由框架: ARouter

## 项目特点

1. **模块化设计**: 采用模块化架构，便于维护和扩展
2. **组件化开发**: 各功能模块独立，可复用性强
3. **最佳实践**: 遵循 Android 官方推荐的开发规范和最佳实践
4. **易于集成**: 预集成了常用的第三方库和组件
5. **代码规范**: 统一的代码风格和命名规范

## 快速开始

1. 克隆项目到本地
2. 使用 Android Studio 打开项目
3. 同步 Gradle 依赖
4. 运行项目

## 适用场景

本项目适用于需要快速搭建的 Android 应用开发，特别适合:
- 新项目启动
- Android 技术栈统一
- 团队开发规范制定
- 学习现代 Android 开发架构
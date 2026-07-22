plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
}

// KMP shared module：承载跨平台（Android / iOS）的纯 Kotlin 数据模型、业务逻辑与 Ktor 网络栈。
// 与 lib_network（Retrofit）并行共存：老代码继续用 lib_network，新跨平台代码用 shared。
kotlin {
    // ---- Android target ----
    android()

    // ---- iOS targets ----
    // 旧式 DSL（Kotlin 1.9.3+ 才有新式 iosArm64() 写法），1.8.0 需用带 targetName 的形式。
    // framework 产物直接在每个 target 内联配置（Kotlin 1.8.0 的 targets.configureEach 会触发 afterEvaluate 限制）。
    fun org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.configureFramework() {
        binaries {
            framework {
                baseName = "Shared"
                isStatic = true
            }
        }
    }
    iosArm64("iosArm64") { configureFramework() }
    iosX64("iosX64") { configureFramework() }
    iosSimulatorArm64("iosSimulatorArm64") { configureFramework() }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // 跨平台协程
                implementation(libs.kotlinx.coroutines.core)
                // JSON 序列化（替代 Gson，KMP 原生支持）
                implementation(libs.kotlinx.serialization.json)
                // Ktor 跨平台网络栈
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                // Ktor WebSocket（IM 实时通信）
                implementation(libs.ktor.client.websockets)
            }
        }
        val androidMain by getting {
            dependencies {
                // Android 端 Ktor 引擎（基于 OkHttp）
                implementation(libs.ktor.client.okhttp)
            }
        }
        // 把三个 iOS 源集归并到 iosMain，避免重复实现
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                // iOS 端 Ktor 引擎（基于 NSURLSession）
                implementation(libs.ktor.client.darwin)
            }
        }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosX64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
    }
}

android {
    namespace = "com.demo.shared"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

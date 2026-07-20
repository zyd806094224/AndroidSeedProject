plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

// KMP shared module：承载跨平台（Android / iOS）的纯 Kotlin 数据模型与业务逻辑。
// 阶段 1 只下沉数据模型，网络层/Room 暂不迁移。
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
                // 跨平台协程（commonMain 只能用带 -core 后缀的多平台库）
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val androidMain by getting
        // 把三个 iOS 源集归并到 iosMain，避免重复实现
        val iosMain by creating {
            dependsOn(commonMain)
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

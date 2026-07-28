plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
    id("org.jetbrains.kotlin.native.cocoapods")
    `maven-publish`
}

group = "com.github.zyd806094224"
version = "1.0.3"

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

    cocoapods {
        version = project.version.toString()
        summary = "KMP IM + 业务逻辑共享模块（iOS）"
        homepage = "https://github.com/zyd806094224/AndroidSeedProject"
        ios.deploymentTarget = "12.4"

        framework {
            baseName = "Shared"
            isStatic = true
        }
    }

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

// ---- Maven 发布配置（GitHub Packages）----
// 发布命令：./gradlew :shared:publish
// 凭证在 gradle.properties: gpr.user / gpr.key
// 消费方通过 implementation("com.github.zyd806094224:shared:<version>") 引用，
// POM 自动声明所有传递依赖（ktor/coroutines/serialization），无需手动补依赖。
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/zyd806094224/AndroidMavenPublish")
            credentials {
                username = (findProperty("gpr.user") as String?) ?: System.getenv("GITHUB_ACTOR") ?: ""
                password = (findProperty("gpr.key") as String?) ?: System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }

    publications {
        // KMP 插件自动注册 iOS targets 的 publication，但 Kotlin 1.8.0 下 Android target 需手动创建。
        // Android publication（AAR + POM，含传递依赖声明）
        register<MavenPublication>("android") {
            groupId = group as String
            artifactId = "shared"
            version = version
            artifact("$buildDir/outputs/aar/shared-release.aar")

            pom {
                name.set("KMP Shared Module")
                description.set("跨平台（Android/iOS）IM + 业务逻辑共享模块")
                packaging = "aar"

                // 显式声明传递依赖（AAR 本身不带 POM 依赖信息）
                withXml {
                    val deps = asNode().appendNode("dependencies")
                    val allDeps = listOf(
                        Triple("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.4"),
                        Triple("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.5.0"),
                        Triple("io.ktor", "ktor-client-core", "2.3.0"),
                        Triple("io.ktor", "ktor-client-content-negotiation", "2.3.0"),
                        Triple("io.ktor", "ktor-serialization-kotlinx-json", "2.3.0"),
                        Triple("io.ktor", "ktor-client-logging", "2.3.0"),
                        Triple("io.ktor", "ktor-client-websockets", "2.3.0"),
                        Triple("io.ktor", "ktor-client-okhttp", "2.3.0")
                    )
                    allDeps.forEach { (g, a, v) ->
                        deps.appendNode("dependency").apply {
                            appendNode("groupId", g)
                            appendNode("artifactId", a)
                            appendNode("version", v)
                            appendNode("scope", "runtime")
                        }
                    }
                }
            }
        }

        // 统一设置所有 publication 的坐标
        publications.withType<MavenPublication>().configureEach {
            groupId = group as String
            version = version
            pom {
                name.set("KMP Shared Module")
                description.set("跨平台（Android/iOS）IM + 业务逻辑共享模块")
            }
        }
    }
}

// 手动注册的 Android publication 使用 AAR 文件路径，显式保证发布前先生成最新产物。
tasks.matching { it.name == "publishAndroidPublicationToGitHubPackagesRepository" }.configureEach {
    dependsOn("bundleReleaseAar")
}

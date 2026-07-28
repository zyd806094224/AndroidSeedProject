# KMP Android / iOS 发布指南

本文档说明 `shared` KMP 模块如何同时发布给 Android 和 iOS 使用，以及如何使用仓库中的自动发布脚本。

## 一、发布结构

| 平台 | 产物 | 发布位置 | 消费方式 |
| --- | --- | --- | --- |
| Android | `shared-release.aar` + Maven POM | GitHub Packages：`zyd806094224/AndroidMavenPublish` | `com.github.zyd806094224:shared:<version>` |
| iOS | `Shared.xcframework.zip` + `shared.podspec` | `AndroidSeedProject` 的 GitHub Release | Podfile 引用远程 podspec |

Android 和 iOS 必须使用同一个 `shared` 版本号。比如发布 `1.0.3`：

- Android 坐标：`com.github.zyd806094224:shared:1.0.3`
- iOS Tag：`shared-ios-1.0.3`
- iOS Release 下载目录：`releases/download/shared-ios-1.0.3/`

> 已经发布的版本不可覆盖。同一份代码发生变化时，应升级为新的补丁版本，例如从 `1.0.3` 升到 `1.0.4`。

## 二、首次环境准备

### 1. 基础工具

发布需要在 macOS 上执行，并准备好：

- JDK 与 Android SDK
- Xcode 与 Xcode Command Line Tools
- CocoaPods
- Git
- `curl` 与 `jq`（自动脚本通过 GitHub API 创建 Release）
- GitHub CLI（只在手动发布时可选使用）

自动脚本不要求安装 `gh`。如果希望手动管理 Release，可以选择安装和登录 GitHub CLI：

```bash
brew install gh
gh auth login
gh auth status
```

### 2. Android GitHub Packages 凭证

推荐把凭证放到本机的 `~/.gradle/gradle.properties`，不要提交到仓库：

```properties
gpr.user=你的GitHub用户名
gpr.key=你的GitHubPersonalAccessToken
```

Token 需要 Android GitHub Packages 的包写入权限，以及 `AndroidSeedProject` 的 Release/Contents 写入权限。脚本会复用 `gpr.key` 调用 GitHub API，也支持 CI 环境变量：

```bash
export GITHUB_ACTOR="你的GitHub用户名"
export GITHUB_TOKEN="你的GitHubToken"
```

不要把真实 Token 写入发布文档、提交记录或命令输出。

### 3. 发布前状态

发布脚本不会因为存在未提交文件而停止，只会打印警告。完整发布时，脚本只自动提交以下三个版本元数据文件：

```text
shared/build.gradle.kts
shared/shared.podspec
shared/binary/shared.podspec
```

`README`、文档、脚本、`gradle.properties` 和其他业务源码都不会被自动提交。为了保证 Git Tag 与发布产物对应，仍建议先提交 `shared/src` 下的业务代码；否则构建产物可能包含未提交代码，而 Tag 中没有这些代码。

```bash
git status
git branch --show-current
```

## 三、推荐：使用自动发布脚本

脚本位置：

```text
scripts/publish-kmp.sh
```

先确保脚本可执行：

```bash
chmod +x scripts/publish-kmp.sh
```

### 1. 只检查环境

不会修改文件，也不会构建或发布：

```bash
./scripts/publish-kmp.sh 1.0.3 --check
```

### 2. 本地准备和验证

第一次使用时，建议先执行本地准备：

```bash
./scripts/publish-kmp.sh 1.0.3
```

该命令会：

1. 更新 `shared/build.gradle.kts` 的版本号。
2. 重新生成 `shared/shared.podspec`。
3. 构建 Android Release AAR。
4. 构建包含真机和模拟器架构的 Release XCFramework。
5. 压缩 `Shared.xcframework.zip`。
6. 计算 SHA256 并更新 `shared/binary/shared.podspec`。
7. 校验 binary podspec 的语法。

不会执行 Git 提交、Tag、推送或任何远程发布。

脚本会拒绝已有本地 Tag 对应的旧版本，即使当前只准备本地产物；这是为了防止旧版本重新压缩后 SHA256 改变。

本地产物位置：

```text
shared/build/outputs/aar/shared-release.aar
shared/build/cocoapods/publish/release/Shared.xcframework.zip
shared/binary/shared.podspec
```

### 3. 一键完整发布

确认本地准备没有问题后执行：

```bash
./scripts/publish-kmp.sh 1.0.3 --publish
```

输入 `publish` 确认后，脚本会重新准备产物并继续：

1. 提交三个版本元数据文件。
2. 推送当前分支。
3. 创建并推送 `shared-ios-1.0.3` Tag。
4. 发布 Android Maven 包到 GitHub Packages。
5. 创建 GitHub Release。
6. 上传 `Shared.xcframework.zip` 和 `shared.podspec`。
7. 通过 `pod spec lint` 校验远程 iOS 包。

CI 或明确不需要交互确认时可以使用：

```bash
./scripts/publish-kmp.sh 1.0.3 --publish --yes
```

### 4. 有未提交文件时执行

脚本现在默认允许存在未提交文件，因此可以直接运行：

```bash
./scripts/publish-kmp.sh 1.0.3
./scripts/publish-kmp.sh 1.0.3 --publish
```

`--allow-dirty` 参数为兼容旧用法而保留，但已经不再改变脚本行为。

## 四、手动发布流程

自动脚本不可用时，可以按以下步骤手动操作。以下仍以 `1.0.3` 为例。

### 1. 修改版本并生成 podspec

修改 `shared/build.gradle.kts`：

```kotlin
version = "1.0.3"
```

生成 CocoaPods podspec：

```bash
./gradlew :shared:podspec --no-daemon
```

修改 `shared/binary/shared.podspec` 中的版本和下载地址：

```ruby
spec.version = '1.0.3'

spec.source = {
    :http => 'https://github.com/zyd806094224/AndroidSeedProject/releases/download/shared-ios-1.0.3/Shared.xcframework.zip',
    :sha256 => '稍后填写'
}
```

### 2. 构建 Android 和 iOS 产物

```bash
./gradlew \
  :shared:bundleReleaseAar \
  :shared:podPublishReleaseXCFramework \
  --no-daemon
```

### 3. 压缩 iOS 产物并更新 SHA256

```bash
cd shared/build/cocoapods/publish/release
rm -f Shared.xcframework.zip
ditto -c -k --sequesterRsrc --keepParent \
  Shared.xcframework \
  Shared.xcframework.zip

LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8 \
  shasum -a 256 Shared.xcframework.zip
```

把输出的 SHA256 填入 `shared/binary/shared.podspec`。计算 SHA256 后不能重新压缩 zip，否则校验值会变化。

### 4. 提交并创建 Tag

```bash
git add \
  shared/build.gradle.kts \
  shared/shared.podspec \
  shared/binary/shared.podspec

git commit -m "发布 KMP Android/iOS 1.0.3"
git push origin feature/kmp-shared

git tag -a shared-ios-1.0.3 -m "KMP Shared Android/iOS 1.0.3"
git push origin shared-ios-1.0.3
```

不要提交以下内容：

- `gradle.properties` 中的真实 Token
- `shared/build/` 目录
- `Shared.xcframework.zip`

### 5. 发布 Android Maven

```bash
./gradlew \
  :shared:publishAndroidPublicationToGitHubPackagesRepository \
  --no-daemon
```

发布后的依赖坐标：

```groovy
implementation "com.github.zyd806094224:shared:1.0.3"
```

### 6. 发布 iOS GitHub Release

使用 GitHub CLI：

```bash
gh release create shared-ios-1.0.3 \
  shared/build/cocoapods/publish/release/Shared.xcframework.zip \
  shared/binary/shared.podspec \
  --repo zyd806094224/AndroidSeedProject \
  --verify-tag \
  --title "Shared iOS 1.0.3" \
  --notes "KMP Shared Android/iOS 1.0.3"
```

也可以在 GitHub 仓库的 `Releases` 页面选择对应 Tag，手动上传两个文件。

发布后验证：

```bash
pod spec lint shared/binary/shared.podspec --allow-warnings
```

## 五、消费工程升级

### Android

把 Android 消费方依赖改成新版本：

```groovy
implementation "com.github.zyd806094224:shared:1.0.3"
```

当前 RNHybrid 工程需要检查：

```text
android/app/build.gradle
android/lib_im/build.gradle
```

然后刷新依赖并编译：

```bash
cd /Users/zhaoyudong/IdeaProjects/RNHybrid/android
./gradlew assembleDebug
```

### iOS

把 Podfile 中的 URL 改成新版本：

```ruby
pod 'shared',
    :podspec => 'https://github.com/zyd806094224/AndroidSeedProject/releases/download/shared-ios-1.0.3/shared.podspec'
```

然后执行：

```bash
cd /Users/zhaoyudong/IdeaProjects/RNHybrid/ios
pod install
```

使用 `.xcworkspace` 打开 Xcode 并分别验证模拟器 Debug 和真机 Release 构建。

## 六、失败处理

### 构建阶段失败

此时尚未远程发布。修复问题后可以使用同一个版本重新执行脚本。

### Tag 已推送，但 Android 或 iOS 发布失败

不要删除并覆盖已经被使用的版本，也不要直接重新运行完整脚本：

- Android 尚未发布：单独执行 Android Maven 发布 Gradle 任务。
- iOS Release 尚未创建：使用上面的 `gh release create` 命令上传本地两个产物。
- iOS Release 已创建但产物本身错误：发布新的补丁版本，不覆盖旧 zip。

### CocoaPods 报 SHA256 不匹配

说明 Release 中的 zip 和 podspec 记录的 SHA256 不是同一个文件。正式处理方式是重新构建并发布新版本，不修改已经分发的版本。

## 七、证书更新与 KMP 版本的关系

- 如果只替换 iOS 宿主工程中的 `server_cert.der`，没有修改 KMP 源码，不需要发布新的 KMP 包。
- 如果修改了 `shared` 内部的证书信任、pinning 或网络实现，应发布新的 Android/iOS KMP 版本。
- 使用正规 CA 签发且系统信任的完整证书链后，通常不再需要宿主中额外的自签名证书资源；仍应根据实际网络安全策略验证。

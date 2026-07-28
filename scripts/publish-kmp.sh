#!/usr/bin/env bash

set -euo pipefail

# macOS 不提供 C.UTF-8；部分终端继承该值时 Perl/shasum 会异常退出。
export LC_ALL="en_US.UTF-8"
export LANG="en_US.UTF-8"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

BUILD_FILE="$ROOT_DIR/shared/build.gradle.kts"
GENERATED_PODSPEC="$ROOT_DIR/shared/shared.podspec"
BINARY_PODSPEC="$ROOT_DIR/shared/binary/shared.podspec"
IOS_OUTPUT_DIR="$ROOT_DIR/shared/build/cocoapods/publish/release"
XCFRAMEWORK_PATH="$IOS_OUTPUT_DIR/Shared.xcframework"
ZIP_PATH="$IOS_OUTPUT_DIR/Shared.xcframework.zip"

GITHUB_REPO="zyd806094224/AndroidSeedProject"
GIT_REMOTE="origin"
GITHUB_API_TOKEN=""
PUBLISH=0
ASSUME_YES=0
CHECK_ONLY=0
ALLOW_DIRTY=0
VERSION=""

usage() {
    cat <<'EOF'
用法：
  ./scripts/publish-kmp.sh <版本号>
  ./scripts/publish-kmp.sh <版本号> --publish

示例：
  # 只更新版本、构建 Android AAR 和 iOS XCFramework，并生成带 SHA256 的 podspec
  ./scripts/publish-kmp.sh 1.0.3

  # 完整发布：准备产物、提交并推送版本、发布 Android Maven 和 iOS GitHub Release
  ./scripts/publish-kmp.sh 1.0.3 --publish

选项：
  --publish       执行完整远程发布；不指定时只在本地准备产物
  --yes           跳过完整发布前的交互确认，适用于 CI
  --check         只检查环境、版本号和仓库状态，不修改文件或构建
  --allow-dirty   兼容保留；当前未提交文件只警告，不再阻止构建或发布
  -h, --help      显示帮助
EOF
}

log() {
    printf '\n[%s] %s\n' "KMP Release" "$1"
}

warn() {
    printf '\n[KMP Release][警告] %s\n' "$1" >&2
}

die() {
    printf '\n[KMP Release][错误] %s\n' "$1" >&2
    exit 1
}

require_command() {
    command -v "$1" >/dev/null 2>&1 || die "缺少命令：$1"
}

read_gradle_property() {
    local property_name="$1"
    local properties_file="$2"

    [[ -f "$properties_file" ]] || return 1
    awk -v target="$property_name" '
        index($0, target "=") == 1 {
            sub("^[^=]*=", "")
            sub("\\r$", "")
            print
            exit
        }
    ' "$properties_file"
}

get_github_token() {
    local token=""
    local user_gradle_properties="${GRADLE_USER_HOME:-$HOME/.gradle}/gradle.properties"

    if [[ -n "${GITHUB_TOKEN:-}" ]]; then
        printf '%s' "$GITHUB_TOKEN"
        return 0
    fi

    token="$(read_gradle_property "gpr.key" "$ROOT_DIR/gradle.properties" || true)"
    if [[ -n "$token" ]]; then
        printf '%s' "$token"
        return 0
    fi

    token="$(read_gradle_property "gpr.key" "$user_gradle_properties" || true)"
    if [[ -n "$token" ]]; then
        printf '%s' "$token"
        return 0
    fi

    return 1
}

has_gradle_publish_credentials() {
    if [[ -n "${GITHUB_ACTOR:-}" && -n "${GITHUB_TOKEN:-}" ]]; then
        return 0
    fi

    if [[ -f "$ROOT_DIR/gradle.properties" ]] \
        && grep -Eq '^gpr\.user=.+$' "$ROOT_DIR/gradle.properties" \
        && grep -Eq '^gpr\.key=.+$' "$ROOT_DIR/gradle.properties"; then
        return 0
    fi

    local user_gradle_properties="${GRADLE_USER_HOME:-$HOME/.gradle}/gradle.properties"
    if [[ -f "$user_gradle_properties" ]] \
        && grep -Eq '^gpr\.user=.+$' "$user_gradle_properties" \
        && grep -Eq '^gpr\.key=.+$' "$user_gradle_properties"; then
        return 0
    fi

    return 1
}

check_worktree() {
    local unexpected=""
    local line=""
    local path=""

    while IFS= read -r line; do
        [[ -z "$line" ]] && continue
        path="${line:3}"
        case "$path" in
            gradle.properties|shared/build.gradle.kts|shared/shared.podspec|shared/binary/shared.podspec)
                ;;
            *)
                unexpected="${unexpected}${line}"$'\n'
                ;;
        esac
    done < <(git -C "$ROOT_DIR" status --porcelain --untracked-files=all)

    if [[ -n "$unexpected" ]]; then
        printf '%s' "$unexpected" >&2
        warn "检测到未提交修改，脚本将继续执行；发布提交只包含版本元数据，不会自动提交上面的文件。"
    fi
}

check_local_version_available() {
    local tag="shared-ios-$VERSION"

    if git -C "$ROOT_DIR" show-ref --verify --quiet "refs/tags/$tag"; then
        die "本地 Tag 已存在：${tag}。已发布版本不能重新构建或覆盖，请升级版本号。"
    fi
}

check_remote_version_available() {
    local tag="shared-ios-$VERSION"
    local remote_tags=""
    local response_file=""
    local http_code=""
    local api_error=""

    if ! remote_tags="$(git -C "$ROOT_DIR" ls-remote --tags "$GIT_REMOTE" "refs/tags/$tag")"; then
        die "无法检查远程 Tag，请确认 Git 网络和仓库权限。"
    fi

    if [[ -n "$remote_tags" ]]; then
        die "远程 Tag 已存在：${tag}。已发布版本不能覆盖，请升级版本号。"
    fi

    response_file="$(mktemp "${TMPDIR:-/tmp}/kmp-release-check.XXXXXX")"
    http_code="$(curl -sS -o "$response_file" -w '%{http_code}' \
        -H "Accept: application/vnd.github+json" \
        -H "Authorization: Bearer $GITHUB_API_TOKEN" \
        -H "X-GitHub-Api-Version: 2022-11-28" \
        "https://api.github.com/repos/$GITHUB_REPO/releases/tags/$tag")"

    case "$http_code" in
        200)
            rm -f "$response_file"
            die "GitHub Release 已存在：${tag}。已发布版本不能覆盖，请升级版本号。"
            ;;
        404)
            rm -f "$response_file"
            ;;
        *)
            api_error="$(jq -r '.message // "未知错误"' "$response_file" 2>/dev/null || printf '未知错误')"
            rm -f "$response_file"
            die "检查 GitHub Release 失败：HTTP ${http_code}，${api_error}"
            ;;
    esac
}

update_versions() {
    log "更新 shared 版本为 $VERSION"

    RELEASE_VERSION="$VERSION" perl -0pi -e \
        's/^version = "[^"]+"/version = "$ENV{RELEASE_VERSION}"/m' \
        "$BUILD_FILE"

    grep -Fq "version = \"$VERSION\"" "$BUILD_FILE" \
        || die "更新 shared/build.gradle.kts 版本失败。"

    "$ROOT_DIR/gradlew" -p "$ROOT_DIR" :shared:podspec --no-daemon

    grep -Eq "spec\.version[[:space:]]*=[[:space:]]*'$VERSION'" "$GENERATED_PODSPEC" \
        || die "生成的 shared/shared.podspec 版本不正确。"

    RELEASE_VERSION="$VERSION" perl -0pi -e \
        's/(spec\.version\s*=\s*)\x27[^\x27]+\x27/${1}\x27$ENV{RELEASE_VERSION}\x27/; s{releases/download/shared-ios-[^/]+/Shared\.xcframework\.zip}{releases/download/shared-ios-$ENV{RELEASE_VERSION}/Shared.xcframework.zip}' \
        "$BINARY_PODSPEC"

    grep -Fq "shared-ios-$VERSION/Shared.xcframework.zip" "$BINARY_PODSPEC" \
        || die "更新 iOS Release 下载地址失败。"
}

build_artifacts() {
    log "构建 Android Release AAR 与 iOS Release XCFramework"

    "$ROOT_DIR/gradlew" -p "$ROOT_DIR" \
        :shared:bundleReleaseAar \
        :shared:podPublishReleaseXCFramework \
        --no-daemon

    [[ -f "$ROOT_DIR/shared/build/outputs/aar/shared-release.aar" ]] \
        || die "Android AAR 未生成。"
    [[ -d "$XCFRAMEWORK_PATH" ]] \
        || die "iOS Shared.xcframework 未生成。"
}

package_ios_artifact() {
    local sha256=""
    local archive_entries=""

    log "压缩 iOS XCFramework 并写入 SHA256"

    rm -f "$ZIP_PATH"
    ditto -c -k --sequesterRsrc --keepParent "$XCFRAMEWORK_PATH" "$ZIP_PATH"

    [[ -f "$ZIP_PATH" ]] || die "Shared.xcframework.zip 未生成。"
    unzip -tqq "$ZIP_PATH"
    archive_entries="$(unzip -Z1 "$ZIP_PATH")"
    grep -q '^Shared\.xcframework/' <<< "$archive_entries" \
        || die "iOS zip 的目录结构不正确，必须包含 Shared.xcframework/ 根目录。"

    sha256="$(LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8 shasum -a 256 "$ZIP_PATH" | awk '{print $1}')"
    [[ "$sha256" =~ ^[0-9a-f]{64}$ ]] || die "计算 iOS zip SHA256 失败。"

    RELEASE_SHA256="$sha256" perl -0pi -e \
        's/(:sha256\s*=>\s*)\x27[^\x27]+\x27/${1}\x27$ENV{RELEASE_SHA256}\x27/' \
        "$BINARY_PODSPEC"

    grep -Fq "$sha256" "$BINARY_PODSPEC" \
        || die "写入 binary podspec SHA256 失败。"

    pod ipc spec "$BINARY_PODSPEC" >/dev/null

    log "本地产物准备完成"
    printf 'Android AAR: %s\n' "$ROOT_DIR/shared/build/outputs/aar/shared-release.aar"
    printf 'iOS ZIP:     %s\n' "$ZIP_PATH"
    printf 'iOS SHA256:  %s\n' "$sha256"
    printf 'Podspec:     %s\n' "$BINARY_PODSPEC"
}

confirm_publish() {
    local answer=""

    [[ "$ASSUME_YES" -eq 1 ]] && return 0
    [[ -t 0 ]] || die "非交互环境执行完整发布时必须传入 --yes。"

    printf '\n即将发布不可覆盖的版本 %s：\n' "$VERSION"
    printf '  Android: com.github.zyd806094224:shared:%s\n' "$VERSION"
    printf '  iOS:     GitHub Release shared-ios-%s\n' "$VERSION"
    printf '输入 publish 继续：'
    read -r answer
    [[ "$answer" == "publish" ]] || die "已取消发布。"
}

upload_github_release_asset() {
    local release_id="$1"
    local file_path="$2"
    local asset_name="$3"
    local content_type="$4"
    local response_file=""
    local http_code=""
    local api_error=""

    response_file="$(mktemp "${TMPDIR:-/tmp}/kmp-release-upload.XXXXXX")"
    http_code="$(curl -sS -o "$response_file" -w '%{http_code}' \
        -X POST \
        -H "Accept: application/vnd.github+json" \
        -H "Authorization: Bearer $GITHUB_API_TOKEN" \
        -H "X-GitHub-Api-Version: 2022-11-28" \
        -H "Content-Type: $content_type" \
        --data-binary "@$file_path" \
        "https://uploads.github.com/repos/$GITHUB_REPO/releases/$release_id/assets?name=$asset_name")"

    if [[ "$http_code" != "201" ]]; then
        api_error="$(jq -r '.message // "未知错误"' "$response_file" 2>/dev/null || printf '未知错误')"
        rm -f "$response_file"
        die "上传 GitHub Release 文件 ${asset_name} 失败：HTTP ${http_code}，${api_error}"
    fi

    rm -f "$response_file"
}

create_github_release() {
    local tag="$1"
    local response_file=""
    local release_payload=""
    local release_id=""
    local http_code=""
    local api_error=""

    response_file="$(mktemp "${TMPDIR:-/tmp}/kmp-release-create.XXXXXX")"
    release_payload="$(jq -n \
        --arg tag "$tag" \
        --arg name "Shared iOS $VERSION" \
        --arg body "KMP Shared Android/iOS $VERSION" \
        '{tag_name: $tag, name: $name, body: $body, draft: false, prerelease: false}')"

    http_code="$(curl -sS -o "$response_file" -w '%{http_code}' \
        -X POST \
        -H "Accept: application/vnd.github+json" \
        -H "Authorization: Bearer $GITHUB_API_TOKEN" \
        -H "X-GitHub-Api-Version: 2022-11-28" \
        -H "Content-Type: application/json" \
        --data "$release_payload" \
        "https://api.github.com/repos/$GITHUB_REPO/releases")"

    if [[ "$http_code" != "201" ]]; then
        api_error="$(jq -r '.message // "未知错误"' "$response_file" 2>/dev/null || printf '未知错误')"
        rm -f "$response_file"
        die "创建 GitHub Release 失败：HTTP ${http_code}，${api_error}"
    fi

    release_id="$(jq -r '.id // empty' "$response_file")"
    rm -f "$response_file"
    [[ -n "$release_id" ]] || die "GitHub Release 已创建，但没有获得 release id。"

    upload_github_release_asset \
        "$release_id" "$ZIP_PATH" "Shared.xcframework.zip" "application/zip"
    upload_github_release_asset \
        "$release_id" "$BINARY_PODSPEC" "shared.podspec" "application/octet-stream"
}

publish_release() {
    local branch=""
    local tag="shared-ios-$VERSION"

    branch="$(git -C "$ROOT_DIR" branch --show-current)"
    [[ -n "$branch" ]] || die "当前处于 detached HEAD，不能自动发布。"

    confirm_publish

    log "提交并推送发布元数据"
    git -C "$ROOT_DIR" diff --check
    git -C "$ROOT_DIR" add -- \
        shared/build.gradle.kts \
        shared/shared.podspec \
        shared/binary/shared.podspec

    git -C "$ROOT_DIR" diff --cached --quiet \
        && die "没有可提交的版本变更，请确认版本号已升级。"

    git -C "$ROOT_DIR" commit -m "发布 KMP Android/iOS $VERSION"
    git -C "$ROOT_DIR" push "$GIT_REMOTE" "$branch"
    git -C "$ROOT_DIR" tag -a "$tag" -m "KMP Shared Android/iOS $VERSION"
    git -C "$ROOT_DIR" push "$GIT_REMOTE" "$tag"

    log "发布 Android Maven 包到 GitHub Packages"
    "$ROOT_DIR/gradlew" -p "$ROOT_DIR" \
        :shared:publishAndroidPublicationToGitHubPackagesRepository \
        --no-daemon

    log "通过 GitHub API 创建 iOS Release 并上传产物"
    create_github_release "$tag"

    log "校验已发布的远程 iOS podspec"
    pod spec lint "$BINARY_PODSPEC" --allow-warnings

    log "版本 $VERSION 发布完成"
    printf 'Android 坐标: com.github.zyd806094224:shared:%s\n' "$VERSION"
    printf 'iOS podspec: https://github.com/%s/releases/download/%s/shared.podspec\n' "$GITHUB_REPO" "$tag"
    printf 'Release 页面: https://github.com/%s/releases/tag/%s\n' "$GITHUB_REPO" "$tag"
}

while [[ $# -gt 0 ]]; do
    case "$1" in
        --publish)
            PUBLISH=1
            ;;
        --yes)
            ASSUME_YES=1
            ;;
        --check)
            CHECK_ONLY=1
            ;;
        --allow-dirty)
            ALLOW_DIRTY=1
            ;;
        -h|--help)
            usage
            exit 0
            ;;
        -* )
            die "未知选项：$1"
            ;;
        *)
            [[ -z "$VERSION" ]] || die "只能指定一个版本号。"
            VERSION="$1"
            ;;
    esac
    shift
done

[[ -n "$VERSION" ]] || {
    usage
    exit 1
}

[[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+([.-][0-9A-Za-z.]+)?$ ]] \
    || die "版本号格式不正确：${VERSION}，例如 1.0.3。"

if [[ "$CHECK_ONLY" -eq 1 && "$PUBLISH" -eq 1 ]]; then
    die "--check 不能与 --publish 一起使用。"
fi

require_command git
require_command perl
require_command pod
require_command ditto
require_command unzip
require_command shasum
[[ -x "$ROOT_DIR/gradlew" ]] || die "找不到可执行的 Gradle Wrapper：$ROOT_DIR/gradlew"
[[ -f "$BUILD_FILE" ]] || die "找不到 $BUILD_FILE"
[[ -f "$BINARY_PODSPEC" ]] || die "找不到 $BINARY_PODSPEC"

check_worktree
check_local_version_available

if [[ "$PUBLISH" -eq 1 ]]; then
    require_command curl
    require_command jq
    GITHUB_API_TOKEN="$(get_github_token || true)"
    [[ -n "$GITHUB_API_TOKEN" ]] \
        || die "缺少 GitHub Token，请配置 gpr.key 或 GITHUB_TOKEN。"
    has_gradle_publish_credentials \
        || die "缺少 Android Maven 发布凭证，请配置 gpr.user/gpr.key 或 GITHUB_ACTOR/GITHUB_TOKEN。"
    check_remote_version_available
fi

if [[ "$CHECK_ONLY" -eq 1 ]]; then
    log "检查通过：可以准备 KMP Android/iOS $VERSION"
    exit 0
fi

update_versions
build_artifacts
package_ios_artifact

if [[ "$PUBLISH" -eq 1 ]]; then
    publish_release
else
    log "当前仅完成本地准备，尚未上传任何远程仓库"
    printf '检查变更后，执行以下命令完成发布：\n'
    printf '  ./scripts/publish-kmp.sh %s --publish\n' "$VERSION"
fi

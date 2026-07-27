package com.demo.shared.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.reinterpret
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.CFDataGetBytePtr
import platform.CoreFoundation.CFDataGetLength
import platform.CoreFoundation.CFRelease
import platform.Foundation.*
import platform.Security.SecCertificateCopyData
import platform.Security.SecTrustRef
import platform.Security.SecTrustGetCertificateAtIndex
import platform.posix.memcmp
import kotlin.native.Platform

private const val PINNED_SELF_SIGNED_HOST = "106.15.7.132"

/**
 * iOS actual：创建 Darwin 引擎（基于 NSURLSession）。
 *
 * Debug：直接接受 serverTrust，方便本地抓包。
 * Release：106.15.7.132 使用宿主 App 内置的 server_cert.der 做完整证书固定，
 * 其他 HTTPS/WSS 保持 NSURLSession 的系统默认校验。
 */
actual fun newHttpClientEngine(): HttpClientEngine = Darwin.create {
    handleChallenge { _, _, challenge, completionHandler ->
        if (challenge.protectionSpace.authenticationMethod != NSURLAuthenticationMethodServerTrust) {
            completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null)
            return@handleChallenge
        }

        val serverTrust = challenge.protectionSpace.serverTrust()
        if (serverTrust == null) {
            completionHandler(NSURLSessionAuthChallengeCancelAuthenticationChallenge, null)
            return@handleChallenge
        }

        if (Platform.isDebugBinary) {
            completionHandler(
                NSURLSessionAuthChallengeUseCredential,
                NSURLCredential.credentialForTrust(serverTrust)
            )
            return@handleChallenge
        }

        if (challenge.protectionSpace.host != PINNED_SELF_SIGNED_HOST) {
            completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, null)
            return@handleChallenge
        }

        if (!matchesPinnedServerCertificate(serverTrust)) {
            println("[SharedHttp][SSL] 服务器证书与内置证书不一致，拒绝连接: ${challenge.protectionSpace.host}")
            completionHandler(NSURLSessionAuthChallengeCancelAuthenticationChallenge, null)
            return@handleChallenge
        }

        completionHandler(
            NSURLSessionAuthChallengeUseCredential,
            NSURLCredential.credentialForTrust(serverTrust)
        )
    }
}

private fun matchesPinnedServerCertificate(serverTrust: SecTrustRef): Boolean {
    val certPath = NSBundle.mainBundle.pathForResource("server_cert", "der")
    if (certPath == null) {
        println("[SharedHttp][SSL] 未找到内置证书 server_cert.der")
        return false
    }

    val certData = NSData.dataWithContentsOfFile(certPath)
    val certBytes = certData?.bytes
    if (certData == null || certBytes == null) {
        println("[SharedHttp][SSL] 无法读取内置证书 server_cert.der")
        return false
    }

    val pinnedCertData = CFDataCreate(null, certBytes.reinterpret(), certData.length.toLong())
    if (pinnedCertData == null) {
        println("[SharedHttp][SSL] 无法创建内置证书数据")
        return false
    }

    val serverCert = SecTrustGetCertificateAtIndex(serverTrust, 0)
    val serverCertData = serverCert?.let { SecCertificateCopyData(it) }
    if (serverCertData == null) {
        CFRelease(pinnedCertData)
        println("[SharedHttp][SSL] 无法读取服务器证书")
        return false
    }

    val pinnedLength = CFDataGetLength(pinnedCertData)
    val serverLength = CFDataGetLength(serverCertData)
    val matches = pinnedLength > 0 &&
        pinnedLength == serverLength &&
        memcmp(
            CFDataGetBytePtr(pinnedCertData),
            CFDataGetBytePtr(serverCertData),
            pinnedLength.toULong()
        ) == 0
    CFRelease(serverCertData)
    CFRelease(pinnedCertData)
    return matches
}

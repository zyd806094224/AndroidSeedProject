package com.demo.shared.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Android actual：创建 OkHttp 引擎。
 * 原 lib_network 的 HttpManager 也用 OkHttp，行为一致。
 *
 * SSL 证书配置：服务端用自签名证书（106.15.7.132:8443），需手动信任。
 * - Debug：信任所有证书（支持抓包）
 * - Release：仅信任 shared/res/raw/server_cert.pem
 */
actual fun newHttpClientEngine(): HttpClientEngine = OkHttp.create {
    config {
        // SSL 证书配置（与 lib_network HttpManager 保持一致）
        if (SharedAndroidContext.isDebug) {
            // Debug 模式：信任所有证书
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())
            sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            hostnameVerifier { _, _ -> true }
        } else {
            // Release 模式：仅信任内置自签名证书
            val trustManager = createSharedTrustManager()
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(trustManager), null)
            sslSocketFactory(sslContext.socketFactory, trustManager)
        }
    }
}

/**
 * 从 shared 模块的 raw 资源加载自签名证书（server_cert.pem），创建 TrustManager。
 * 证书文件位于 shared/src/androidMain/res/raw/server_cert.pem。
 */
private fun createSharedTrustManager(): X509TrustManager {
    val context = SharedAndroidContext.get()
    val certFactory = CertificateFactory.getInstance("X.509")
    val certInput = context.resources.openRawResource(
        context.resources.getIdentifier("server_cert", "raw", context.packageName)
    )
    val caCert = certFactory.generateCertificate(certInput) as X509Certificate
    certInput.close()

    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null, null)
    keyStore.setCertificateEntry("server_cert", caCert)

    val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init(keyStore)

    return trustManagerFactory.trustManagers.first { it is X509TrustManager } as X509TrustManager
}

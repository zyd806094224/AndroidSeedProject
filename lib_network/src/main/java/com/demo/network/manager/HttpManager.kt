package com.demo.network.manager

import android.util.Log
import com.demo.framework.helper.AppHelper
import com.demo.framework.utils.NetworkUtil
import com.demo.network.constant.BASE_URL
import com.demo.network.dns.HttpDns
import com.demo.network.error.ERROR
import com.demo.network.error.NoNetWorkException
import com.demo.network.interceptor.CookiesInterceptor
import com.demo.network.interceptor.HeaderInterceptor
import com.demo.network.interceptor.NetworkMonitorInterceptor
import com.demo.network.interceptor.NetworkRetryInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * @Description: 网络请求管理类
 * @Date: 2024/8/29 17:44
 * @author:  zhaoyudong
 * @version: 1.0
 */
object HttpManager {

    private val mRetrofit: Retrofit

    init {
        mRetrofit = Retrofit.Builder()
            .client(initOkHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 获取 apiService
     */
    fun <T> create(apiService: Class<T>): T {
        return mRetrofit.create(apiService)
    }

    /**
     * 初始化OkHttp
     */
    private fun initOkHttpClient(): OkHttpClient {
        val build = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
        // 添加参数拦截器
        val interceptors = mutableListOf<Interceptor>()
        build.addInterceptor(CookiesInterceptor())
        build.addInterceptor(HeaderInterceptor())

        //日志拦截器
        val logInterceptor = HttpLoggingInterceptor { message: String ->
            Log.i("okhttp", "data:$message")
        }
        if (AppHelper.isDebug()) {
            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        }
        build.addInterceptor(logInterceptor)
        //网络状态拦截
        build.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                if (NetworkUtil.isConnected(AppHelper.getApplication())) {
                    val request = chain.request()
                    return chain.proceed(request)
                } else {
                    throw NoNetWorkException(ERROR.NETWORK_ERROR)
                }
            }
        })
        build.addInterceptor(NetworkRetryInterceptor())
        build.addNetworkInterceptor(NetworkMonitorInterceptor())
        build.dns(HttpDns())

        // SSL 证书配置：服务端用的是自签名证书（106.15.7.132:8443），需手动信任
        if (AppHelper.isDebug()) {
            // Debug 模式：信任所有证书（支持自签名证书和 Charles/Fiddler 抓包）
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())
            build.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            build.hostnameVerifier { _, _ -> true }
        } else {
            // Release 模式：仅信任内置的自签名证书
            val trustManager = createTrustManager()
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(trustManager), null)
            build.sslSocketFactory(sslContext.socketFactory, trustManager)
        }

        return build.build()
    }

    /**
     * 从 app 模块的 raw 资源加载自签名证书（server_cert.pem），创建 TrustManager。
     * 注意：证书文件位于 app/src/main/res/raw，lib_network 通过宿主 Application 资源访问。
     */
    private fun createTrustManager(): X509TrustManager {
        val context = AppHelper.getApplication()
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
}
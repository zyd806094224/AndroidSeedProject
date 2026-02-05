package com.demo.network.dns

import android.text.TextUtils
import okhttp3.Dns
import java.net.InetAddress

class HttpDns : Dns {
    override fun lookup(hostname: String): List<InetAddress?> {
        val ip = HttpDnsHelper.getIpByHost(hostname)
        if (!TextUtils.isEmpty(ip)) {
            return InetAddress.getAllByName(ip).toList()
        }
        return Dns.SYSTEM.lookup(hostname)
    }
}
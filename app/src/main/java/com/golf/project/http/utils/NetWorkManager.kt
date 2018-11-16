package com.golf.project.http.utils

import android.content.Context
import android.net.ConnectivityManager
import com.golf.project.base.BaseApplication

/**
 * 作者： Zun.
 * 创建： 2018/11/6.
 * 说明： 网络管理工具
 */
object NetWorkManager {

    /**
     * 检查网络是否可用
     */
    fun isNetConnected(): Boolean {
        val localNetworkInfo = (BaseApplication.getApplictionContext
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
        return localNetworkInfo != null && localNetworkInfo.isAvailable
    }

    /**
     * 检测wifi是否连接
     */
    fun isWifiConnected(): Boolean {
        val cm = BaseApplication.getApplictionContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI
    }
}

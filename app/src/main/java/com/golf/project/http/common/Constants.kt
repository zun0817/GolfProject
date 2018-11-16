package com.golf.project.http.common

/**
 * 作者： Zun.
 * 创建： 2018/11/7.
 * 说明： 全局变量
 */
object Constants {

    const val BASE_URL = "http://www.wanandroid.com/"

    const val PATH_CACHE = "/storage/emulated/0/Android/data/com.lingzhitech.lib_kotlin_http./cache/GolfCache"

    /**
     * 未知错误
     */
    const val ERROR_UNKNOWN: Int = 1000

    /**
     * 解析错误
     */
    const val ERROR_PARSE: Int = 1001

    /**
     * 网络错误
     */
    const val ERROR_NETWORK: Int = 1002

    /**
     * HTTP错误
     */
    const val ERROR_HTTP: Int = 1003

    /**
     * 网络连接超时
     */
    const val REQUEST_CONNECT_TIMEOUT = 20L

    /**
     * 读取数据超时
     */
    const val REQUEST_READ_TIMEOUT = 20L

    /**
     * 写入数据超时
     */
    const val REQUEST_WRITE_TIMEOUT = 20L


}
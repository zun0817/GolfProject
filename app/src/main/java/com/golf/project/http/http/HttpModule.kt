package com.golf.project.http.http

import android.text.TextUtils
import com.golf.project.BuildConfig
import com.golf.project.http.common.Constants
import com.golf.project.http.utils.NetWorkManager
import com.golf.project.http.utils.Preference
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * 作者： Zun.
 * 创建： 2018/11/6.
 * 说明： Retrofit配置管理类
 */
object HttpModule {

    private val F_BREAK = " %n"
    private val F_URL = " %s"
    private val F_TIME = " in %.1fms"
    private val F_HEADERS = "%s"
    private val F_RESPONSE = F_BREAK + "Response: %d"
    private val F_BODY = "body: %s"

    private val F_BREAKER = "$F_BREAK-------------------------------------------$F_BREAK"
    private val F_REQUEST_WITHOUT_BODY = F_URL + F_TIME + F_BREAK + F_HEADERS
    private val F_RESPONSE_WITHOUT_BODY = F_RESPONSE + F_BREAK + F_HEADERS + F_BREAKER
    private val F_REQUEST_WITH_BODY = F_URL + F_TIME + F_BREAK + F_HEADERS + F_BODY + F_BREAK
    private val F_RESPONSE_WITH_BODY = F_RESPONSE + F_BREAK + F_HEADERS + F_BODY + F_BREAK + F_BREAKER


    private var token: String by Preference("token", "")
    private var ipAddress: String by Preference("server_ip_address", "")

    fun initGolfAPI(): GolfAPI {
        return initRetrofit().create(GolfAPI::class.java)
    }

    private fun initRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .client(initOkhttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun initOkhttpClient(): OkHttpClient {

        val builder = OkHttpClient.Builder()
        addHttpLoggingInterceptor(builder)
        addCacheInterceptor(builder)
        addQueryParameterInterceptor(builder)
        addHeaderInterceptor(builder)
        addBasicConfig(builder)

        return builder.build()
    }

    /**
     * 配置基础信息
     */
    private fun addBasicConfig(builder: OkHttpClient.Builder) {
        builder.connectTimeout(Constants.REQUEST_CONNECT_TIMEOUT, TimeUnit.SECONDS)
        builder.readTimeout(Constants.REQUEST_READ_TIMEOUT, TimeUnit.SECONDS)
        builder.writeTimeout(Constants.REQUEST_WRITE_TIMEOUT, TimeUnit.SECONDS)
        builder.retryOnConnectionFailure(true)
    }

    /**
     * 设置Log信息拦截器
     */
    private fun addHttpLoggingInterceptor(builder: OkHttpClient.Builder) {
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }
    }

    /**
     * 设置公共参数
     */
    private fun addQueryParameterInterceptor(builder: OkHttpClient.Builder) {
        val queryParameterInterceptor = Interceptor { chain ->
            val queryParameterRequest = chain.request()
            val modifiedUrl = queryParameterRequest.url().newBuilder()
                .addQueryParameter("uid", "864678036850608")
                .addQueryParameter("devid", "864678036850608")
                .addQueryParameter("proid", "ifengnews")
                .addQueryParameter("vt", "5")
                .addQueryParameter("publishid", "6103")
                .addQueryParameter("screen", "1080x1920")
                .addQueryParameter("df", "androidphone")
                .addQueryParameter("os", "android_22")
                .addQueryParameter("nw", "wifi")
                .build()
            val request = queryParameterRequest.newBuilder().url(modifiedUrl).build()
            chain.proceed(request)
        }
        builder.addInterceptor(queryParameterInterceptor)
    }

    /**
     * 设置请求Header
     */
    private fun addHeaderInterceptor(builder: OkHttpClient.Builder) {
        val headerInterceptor = Interceptor { chain ->
            val hearderRequest = chain.request()
            val requestBuilder = hearderRequest.newBuilder()
                .header("token", token)
                .method(hearderRequest.method(), hearderRequest.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        builder.addInterceptor(headerInterceptor)
    }

    /**
     * 设置缓存机制 无网络时，也能显示数据
     */
    private fun addCacheInterceptor(builder: OkHttpClient.Builder) {
        val cacheFile = File(Constants.PATH_CACHE)
        val cache = Cache(cacheFile, 1024 * 1024 * 50)
        val cacheInterceptor = Interceptor { chain ->
            var request = chain.request()
            if (!NetWorkManager.isNetConnected()) {
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
            }
            val response = chain.proceed(request)
            if (NetWorkManager.isNetConnected()) {
                val maxAge = 0
                // 有网络时 设置缓存超时时间0个小时 ,意思就是不读取缓存数据,只对get有用,post没有缓冲
                response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build()
            } else {
                // 无网络时，设置超时为4周  只对get有用,post没有缓冲
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build()
            }
            response
        }
        builder.cache(cache).addInterceptor(cacheInterceptor)
    }

    /**
     * 设置自定义Logger输出日志
     */
    private fun addLoggerInterceptor(builder: OkHttpClient.Builder) {
        val loggerInterceptor = Interceptor { chain ->
            val loggerRequest = chain.request()

            val time1 = System.nanoTime()
            val response = chain.proceed(loggerRequest)
            val time2 = System.nanoTime()

            var contentType: MediaType? = null
            var body: String? = null
            response?.let {
                contentType = response.body()?.contentType()
                body = response.body()?.toString()
            }

            val time = (time2 - time1) / 1e6

            when (loggerRequest.method()) {
                "GET" -> println(
                    String.format(
                        "GET $F_REQUEST_WITHOUT_BODY$F_RESPONSE_WITH_BODY",
                        loggerRequest.url(),
                        time,
                        loggerRequest.headers(),
                        response.code(),
                        response.headers(),
                        stringifyResponseBody(body!!)
                    )
                )
                "POST" -> println(
                    String.format(
                        "POST $F_REQUEST_WITH_BODY$F_RESPONSE_WITH_BODY",
                        loggerRequest.url(),
                        time,
                        loggerRequest.headers(),
                        stringifyRequestBody(loggerRequest),
                        response.code(),
                        response.headers(),
                        stringifyResponseBody(body!!)
                    )
                )
                "DELETE" -> println(
                    String.format(
                        "DELETE $F_REQUEST_WITHOUT_BODY$F_RESPONSE_WITHOUT_BODY",
                        loggerRequest.url(),
                        time,
                        loggerRequest.headers(),
                        response.code(),
                        response.headers()
                    )
                )
            }

            if (response.body() != null) {
                val responseBody = ResponseBody.create(contentType, body)
                return@Interceptor response.newBuilder().body(responseBody).build()
            } else {
                return@Interceptor response
            }
        }
        builder.addInterceptor(loggerInterceptor)
    }

    /**
     * 动态切换BaseUrl
     */
    private fun addUrlInterceptor(builder: OkHttpClient.Builder) {
        val urlInterceptor = Interceptor { chain ->
            val request = chain.request()
            // 设置baseUrl
            var baseUrl = Constants.BASE_URL
            if (!TextUtils.isEmpty(ipAddress)) {
                //baseUrl = "http://" + ipAddress + ":8080/api/";
                val scheme = baseUrl.substring(7, 18)
                baseUrl = baseUrl.replace(scheme, ipAddress)
            }

            // 新的baseUrl
            val newBaseUrl = okhttp3.HttpUrl.parse(baseUrl)

            // 从request中获取原有的HttpUrl实例oldHttpUrl
            val oldHttpUrl = request.url()
            val newFullUrl = oldHttpUrl
                .newBuilder()
                .scheme(newBaseUrl!!.scheme())
                .host(newBaseUrl.host())
                .port(newBaseUrl.port())
                .build()

            // 获取request的创建者builder
            val builder = request.newBuilder()
            // 重建这个request，通过builder.url(newFullUrl).build()；
            // 然后返回一个response至此结束修改
            return@Interceptor chain.proceed(builder.url(newFullUrl).build())
        }
        builder.addInterceptor(urlInterceptor)
    }

    private fun stringifyRequestBody(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body()?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "did not work"
        }
    }

    private fun stringifyResponseBody(responseBody: String): String {
        return responseBody
    }

}
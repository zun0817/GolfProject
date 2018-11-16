package com.golf.project.http.exception

import com.golf.project.http.common.Constants
import com.google.gson.JsonParseException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 作者： Zun.
 * 创建： 2018/11/6.
 * 说明： 自定义异常，比如网络异常，解析异常等
 */
class LocalException {

    companion object {
        /**
         * 将本地异常解析成ApiException
         */
        fun handleException(cause: Throwable?): ApiException? {
            var exception: ApiException? = null
            exception = if (cause is JsonParseException) {
                ApiException(Constants.ERROR_PARSE, cause.message)
            } else if (cause is UnknownHostException ||
                cause is SocketTimeoutException ||
                cause is ConnectException
            ) {
                ApiException(Constants.ERROR_NETWORK, cause.message)
            } else {
                ApiException(Constants.ERROR_UNKNOWN, cause?.message)
            }
            return exception
        }
    }


}
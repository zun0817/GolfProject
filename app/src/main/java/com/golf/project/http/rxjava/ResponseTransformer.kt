package com.golf.project.http.rxjava

import com.golf.project.http.exception.ApiException
import com.golf.project.http.exception.LocalException
import com.golf.project.http.response.BaseResponse
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.functions.Function

/**
 * 作者： Zun.
 * 创建： 2018/11/6.
 * 说明： rxjava2变换封装，分离数据和异常
 */
class ResponseTransformer {

    companion object {
        fun <T> handleResult(): ObservableTransformer<BaseResponse<T>, T> {
            return MyObservableTransformer()
        }
    }

    class MyObservableTransformer<T> : ObservableTransformer<BaseResponse<T>, T> {
        override fun apply(upstream: Observable<BaseResponse<T>>): ObservableSource<T> {
            return upstream.
                    onErrorResumeNext(ErrorResumeFunction())
                    .flatMap(ResponseFunction())
        }
    }

    /**
     * 非服务器产生的异常，比如解析错误，网络连接错误
     */
    class ErrorResumeFunction<T> : Function<Throwable, ObservableSource<out BaseResponse<T>>> {
        override fun apply(t: Throwable): ObservableSource<out BaseResponse<T>> {
            return Observable.error(LocalException.handleException(t))
        }
    }

    /**
     * 服务器产生的错误：HTTP错误代码
     */
    class ResponseFunction<T> : Function<BaseResponse<T>, ObservableSource<T>> {
        override fun apply(t: BaseResponse<T>): ObservableSource<T> {
            val code: Int = t.errorCode
            return if (code >= 0) {
                Observable.just(t.data)
            } else {
                Observable.error(Throwable(t.errorMsg, ApiException(code, t.errorMsg)))
            }
        }

    }

}
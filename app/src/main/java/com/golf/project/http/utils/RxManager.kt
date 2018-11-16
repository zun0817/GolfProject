package com.golf.project.http.utils

import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 作者： Zun.
 * 创建： 2018/11/6.
 * 说明： Rxjava线程统一处理管理器
 */
object RxManager {

    fun schedulersHelper(): ObservableTransformer<Any, Any> {

        return observableTransformer
    }

    fun flowableHelper(): FlowableTransformer<Any, Any> {

        return flowableTransformer
    }

    private val observableTransformer: ObservableTransformer<Any, Any> = ObservableTransformer {
        it.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private val flowableTransformer: FlowableTransformer<Any, Any> = FlowableTransformer {
        it.subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}
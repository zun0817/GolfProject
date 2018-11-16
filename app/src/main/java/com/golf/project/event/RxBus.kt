package com.golf.project.event

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * 作者： Zun.
 * 创建： 2018/11/5.
 * 说明： Rvbus事件传递
 */
class RxBus private constructor() {

    private val subject = PublishSubject.create<Any>().toSerialized()

    private var dispoable: Disposable? = null

    /**
     * 发送一个新的事件
     */
    fun post(o: Any) {
        subject.onNext(o)
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return subject.ofType(eventType)
    }

    /**
     * 订阅
     */
    fun subscribe(bean: Class<*>, consumer: Consumer<Any>) {
        dispoable = toObservable(bean).subscribe(consumer)
    }

    /**
     * 取消订阅
     */
    fun unSubcribe() {
        if (dispoable != null && dispoable!!.isDisposed) {
            dispoable!!.dispose()
        }
    }

    companion object {

        @Volatile
        private var mInstance: RxBus? = null

        val instance: RxBus?
            get() {
                if (mInstance == null) {
                    synchronized(RxBus::class.java) {
                        if (mInstance == null) {
                            mInstance = RxBus()
                        }
                    }
                }
                return mInstance
            }
    }
}

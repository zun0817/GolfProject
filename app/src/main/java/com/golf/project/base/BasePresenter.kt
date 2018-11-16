package com.golf.project.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * 作者： Zun.
 * 创建： 2018/11/5.
 * 说明： Presenter 基类
 */
open class BasePresenter : IPresenter {

    var mView: BaseView? = null
    private var compositeDisposable: CompositeDisposable? = null


    override fun attachView(mRootView: BaseView) {
        this.mView = mRootView
    }

    override fun detachView() {
        this.mView = null
        if (compositeDisposable != null) {
            compositeDisposable?.clear()
        }
    }

    fun addSubscribe(disposable: Disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable?.add(disposable)
    }
}
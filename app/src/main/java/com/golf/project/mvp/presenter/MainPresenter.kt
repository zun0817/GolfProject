package com.golf.project.mvp.presenter

import android.content.Context
import com.golf.project.base.BaseApplication
import com.golf.project.base.BasePresenter
import com.golf.project.http.rxjava.ResponseTransformer
import com.golf.project.http.utils.RxManager
import com.golf.project.mvp.contract.MainContract
import com.golf.project.mvp.model.RegisterModel
import com.golf.project.utils.ToastManager

/**
 * 作者： Zun.
 * 创建： 2018/11/13.
 * 说明：
 */
class MainPresenter constructor(private var context: Context) : BasePresenter(),
    MainContract.Presenter {

    override fun getMainData() {
        addSubscribe(
            BaseApplication.golfAPI.userRegister("诸君小城市所发生的的", "123123123", "123123123")
                .compose(ResponseTransformer.handleResult())
                .compose(RxManager.schedulersHelper())
                .subscribe({ response ->
                    val data = response as RegisterModel
                }, { throwable ->
                    ToastManager.toast(throwable.message)
                })
        )

    }
}


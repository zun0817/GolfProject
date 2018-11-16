package com.golf.project.base

/**
 * 作者： Zun.
 * 创建： 2018/11/13.
 * 说明：
 */
interface IPresenter {

    fun attachView(mRootView: BaseView)

    fun detachView()

}
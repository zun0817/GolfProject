package com.golf.project.mvp.contract

import com.golf.project.base.BaseView
import com.golf.project.base.IPresenter

/**
 * 作者： Zun.
 * 创建： 2018/11/13.
 * 说明：
 */
interface MainContract {

    interface MianView : BaseView {

        fun showMainData()
    }

    interface Presenter : IPresenter {

        fun getMainData()
    }

}
package com.golf.project.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.golf.project.http.http.GolfAPI
import com.golf.project.utils.ToastManager
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * 作者： Zun.
 * 创建： 2018/11/7.
 * 说明： Fragment基类
 */
abstract class BaseFragment : Fragment(), BaseView {

    /**
     * 数据是否加载完毕
     */
    private var isLoadData: Boolean = false

    /**
     * 试图是否加载完毕
     */
    private var isViewPrepare: Boolean = false

    protected var mActivity: BaseActivity? = null


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as BaseActivity
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            lazyLoadDataOfPrepared()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(exInitLayout(), container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewPrepare = true
        exInitView()
        exInitData()
        lazyLoadDataOfPrepared()
    }

    private fun lazyLoadDataOfPrepared() {
        if (userVisibleHint && isViewPrepare && !isLoadData) {
            exLazyLoad()
            isLoadData = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        mActivity = null
    }

    abstract fun exInitLayout(): Int

    abstract fun exInitView()

    abstract fun exInitData()

    abstract fun exLazyLoad()

    override fun showProgress() {

    }

    override fun showToast(msg: String) {
        ToastManager.toast(msg)
    }

    override fun hideProgress() {

    }

}
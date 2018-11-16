package com.golf.project.base

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.golf.project.common.AppManager
import com.golf.project.utils.ToastManager
import dagger.android.AndroidInjection

/**
 * 作者： Zun.
 * 创建： 2018/11/5.
 * 说明： Activity基类
 */
abstract class BaseActivity : AppCompatActivity(), BaseView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(exInitLayout())
        AppManager.getAppManager.addActivity(this)
        exInitView()
        exInitData()
    }

    abstract fun exInitLayout(): Int

    abstract fun exInitView()

    abstract fun exInitData()

    override fun onDestroy() {
        super.onDestroy()
        AppManager.getAppManager.finishActivity(this)
    }

    fun openActivity(activity: BaseActivity) {
        openActivity(activity, null)
    }

    fun openActivity(activity: BaseActivity, bundle: Bundle?) {
        val intent = Intent()
        intent.setClass(this, activity::class.java)
        bundle?.let {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    override fun showToast(msg: String) {
        ToastManager.toast(msg)
    }

    override fun showProgress() {

    }

    override fun hideProgress() {

    }

}
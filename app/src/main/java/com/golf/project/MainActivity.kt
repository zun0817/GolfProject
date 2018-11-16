package com.golf.project

import com.golf.project.base.BaseActivity
import com.golf.project.mvp.contract.MainContract
import com.golf.project.mvp.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainContract.MianView {

    private val mPresenter: MainPresenter by lazy { MainPresenter(this) }

    init {
        mPresenter.attachView(this)
    }

    override fun exInitLayout(): Int {
        return R.layout.activity_main
    }

    override fun exInitView() {
        textview.setOnClickListener {
            mPresenter.getMainData()
        }
    }

    override fun exInitData() {
//        mPresenter.getMainData()
    }

    override fun showMainData() {

    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

}

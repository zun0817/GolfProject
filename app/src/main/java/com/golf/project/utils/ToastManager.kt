package com.golf.project.utils

import android.view.Gravity
import android.widget.Toast
import com.golf.project.base.BaseApplication

/**
 * 作者： Zun.
 * 创建： 2018/11/5.
 * 说明： Toast管理类
 */
object ToastManager {

    private var mToast: Toast? = null

    fun toast(resId: Int?) {
        if (resId != null && resId != 0) {
            toast(ResManager.getString(resId))
        }
    }

    fun toast(content: String?) {
        content?.let {
            Toast.makeText(BaseApplication.getApplictionContext, content, Toast.LENGTH_SHORT).show()
        }
    }

}
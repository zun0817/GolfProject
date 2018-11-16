package com.golf.project.utils

import android.graphics.drawable.Drawable
import com.golf.project.base.BaseApplication

/**
 * 作者： Zun.
 * 创建： 2018/11/5.
 * 说明： res获取管理类
 */
object ResManager {

    fun getString(resId: Int?): String? {

        return if (resId != null && resId != 0) {
            BaseApplication.getApplictionContext.resources.getString(resId)
        } else {
            null
        }
    }

    fun getDimen(resId: Int?): Float? {

        return if (resId != null && resId != 0) {
            BaseApplication.getApplictionContext.resources.getDimension(resId)
        } else {
            null
        }
    }

    fun getColor(resId: Int?): Int? {

        return if (resId != null && resId != 0) {
            BaseApplication.getApplictionContext.resources.getColor(resId)
        } else {
            null
        }
    }

    fun getDrawable(resId: Int?): Drawable? {

        return if (resId != null && resId != 0) {
            BaseApplication.getApplictionContext.resources.getDrawable(resId)
        } else {
            null
        }
    }

}
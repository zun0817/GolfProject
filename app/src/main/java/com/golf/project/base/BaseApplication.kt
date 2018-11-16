package com.golf.project.base

import android.app.Application
import com.golf.project.http.http.GolfAPI
import com.golf.project.http.http.HttpModule
import kotlin.properties.Delegates

/**
 * 作者： Zun.
 * 创建： 2018/11/5.
 * 说明： 工程基类
 */
class BaseApplication : Application() {

    companion object {
        var getInstance: BaseApplication by Delegates.notNull()
        var getApplictionContext: Application by Delegates.notNull()
        var golfAPI: GolfAPI by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        getInstance = this
        getApplictionContext = this
        golfAPI = HttpModule.initGolfAPI()
    }
}
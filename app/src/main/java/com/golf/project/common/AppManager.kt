package com.golf.project.common

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import com.golf.project.common.AppManager.Companion.activityStack

import java.util.Stack

/**
 * 作者： Zun.
 * 创建： 2018/11/5.
 * 说明： Activity管理类
 */

class AppManager private constructor() {

    /**
     * 添加Activity到堆栈
     */
    public fun addActivity(activity: Activity) {

        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack!!.add(activity)
    }


    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public fun currentActivity(): Activity {

        return activityStack!!.lastElement()
    }


    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public fun finishActivity() {

        val activity = activityStack!!.lastElement()

        finishActivity(activity)
    }


    /**
     * 结束指定的Activity
     */
    public fun finishActivity(activity: Activity?) {
        var activity = activity

        if (activity != null) {

            activityStack!!.remove(activity)

            activity.finish()

            activity = null
        }
    }


    /**
     * 结束指定类名的Activity
     */
    public fun finishActivity(cls: Class<*>) {

        for (activity in activityStack!!) {

            if (activity.javaClass == cls) {

                finishActivity(activity)
            }
        }
    }


    /**
     * 结束所有Activity
     */
    public fun finishAllActivity() {

        val size = activityStack!!.size
        for (item in 0 until size) {
            activityStack!![item]?.let {
                activityStack!![item].finish()
            }
        }
        activityStack!!.clear()
    }


    /**
     * 退出应用程序
     */
    public fun AppExit(context: Context) {

        try {
            finishAllActivity()

            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            activityManager.restartPackage(context.packageName)

            System.exit(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {

        private var activityStack: Stack<Activity>? = null

        private var instance: AppManager? = null

        /**
         * 单例模式
         */
        val getAppManager: AppManager
            get() {
                if (instance == null) {
                    instance = AppManager()
                }
                return instance as AppManager
            }
    }
}


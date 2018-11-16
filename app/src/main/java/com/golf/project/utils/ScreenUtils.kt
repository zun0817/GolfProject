package com.golf.project.utils

import android.Manifest.permission.WRITE_SETTINGS
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresPermission
import android.util.DisplayMetrics
import android.view.Surface
import android.view.WindowManager
import com.golf.project.base.BaseApplication
import com.golf.project.common.AppManager

/**
 * 作者： Zun.
 * 创建： 2018/11/5.
 * 说明： 设备Size管理类
 */
class ScreenUtils private constructor() {

    var sizeInPx: Int = 0
    var isVerticalSlide: Boolean = false

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {

        var mSizeInPx: Int = 0
        var mIsVerticalSlide: Boolean = false

        val screenWidth: Int
            get() {
                val wm = BaseApplication.getApplictionContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val point = Point()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    wm.defaultDisplay.getRealSize(point)
                } else {
                    wm.defaultDisplay.getSize(point)
                }
                return point.x
            }

        val screenHeight: Int
            get() {
                val wm = BaseApplication.getApplictionContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val point = Point()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    wm.defaultDisplay.getRealSize(point)
                } else {
                    wm.defaultDisplay.getSize(point)
                }
                return point.y
            }

        val screenDensity: Float
            get() = Resources.getSystem().displayMetrics.density

        val screenDensityDpi: Int
            get() = Resources.getSystem().displayMetrics.densityDpi

        fun setFullScreen(activity: Activity) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        fun setNonFullScreen(activity: Activity) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        fun toggleFullScreen(activity: Activity) {
            val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
            val window = activity.window
            if (window.attributes.flags and fullScreenFlag == fullScreenFlag) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
        }

        fun isFullScreen(activity: Activity): Boolean {
            val fullScreenFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN
            return activity.window.attributes.flags and fullScreenFlag == fullScreenFlag
        }

        fun setLandscape(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        fun setPortrait(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        val isLandscape: Boolean
            get() = BaseApplication.getApplictionContext.resources.configuration.orientation === Configuration.ORIENTATION_LANDSCAPE

        val isPortrait: Boolean
            get() = BaseApplication.getApplictionContext.resources.configuration.orientation === Configuration.ORIENTATION_PORTRAIT

        fun getScreenRotation(activity: Activity): Int {
            return when (activity.windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }
        }

        /**
         * Return the bitmap of screen.
         */
        fun screenShot(activity: Activity): Bitmap? {
            return screenShot(activity, false)
        }

        /**
         * Return the bitmap of screen.
         */
        fun screenShot(activity: Activity, isDeleteStatusBar: Boolean = false): Bitmap? {
            val decorView = activity.window.decorView
            decorView.isDrawingCacheEnabled = true
            decorView.setWillNotCacheDrawing(false)
            val bmp = decorView.drawingCache ?: return null
            val dm = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(dm)
            val ret: Bitmap
            ret = if (isDeleteStatusBar) {
                val resources = activity.resources
                val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                val statusBarHeight = resources.getDimensionPixelSize(resourceId)
                Bitmap.createBitmap(
                    bmp,
                    0,
                    statusBarHeight,
                    dm.widthPixels,
                    dm.heightPixels - statusBarHeight
                )
            } else {
                Bitmap.createBitmap(bmp, 0, 0, dm.widthPixels, dm.heightPixels)
            }
            decorView.destroyDrawingCache()
            return ret
        }

        /**
         * Return whether screen is locked.
         */
        val isScreenLock: Boolean
            get() {
                val km =
                    BaseApplication.getApplictionContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                return km.inKeyguardRestrictedInputMode()
            }

        /**
         * Set the duration of sleep.
         */
        var sleepDuration: Int
            get() {
                return try {
                    Settings.System.getInt(
                        BaseApplication.getApplictionContext.contentResolver,
                        Settings.System.SCREEN_OFF_TIMEOUT
                    )
                } catch (e: Settings.SettingNotFoundException) {
                    e.printStackTrace()
                    -123
                }

            }
            @RequiresPermission(WRITE_SETTINGS)
            set(duration) {
                Settings.System.putInt(
                    BaseApplication.getApplictionContext.contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT,
                    duration
                )
            }

        /**
         * Return whether device is tablet.
         */
        val isTablet: Boolean
            get() = BaseApplication.getApplictionContext.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE

        /**
         * Adapt the screen for vertical slide.
         */
        fun adaptScreen4VerticalSlide(
            activity: Activity,
            designWidthInPx: Int
        ) {
            adaptScreen(activity, designWidthInPx, true)
        }

        /**
         * Adapt the screen for horizontal slide.
         */
        fun adaptScreen4HorizontalSlide(
            activity: Activity,
            designHeightInPx: Int
        ) {
            adaptScreen(activity, designHeightInPx, false)
        }

        /**
         * Reference from: https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA
         */
        private fun adaptScreen(
            activity: Activity,
            sizeInPx: Int,
            isVerticalSlide: Boolean
        ) {
            val systemDm = Resources.getSystem().displayMetrics
            val appDm = BaseApplication.getApplictionContext.resources.displayMetrics
            val activityDm = activity.resources.displayMetrics
            if (isVerticalSlide) {
                activityDm.density = activityDm.widthPixels / sizeInPx.toFloat()
            } else {
                activityDm.density = activityDm.heightPixels / sizeInPx.toFloat()
            }
            activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density)
            activityDm.densityDpi = (160 * activityDm.density).toInt()

            appDm.density = activityDm.density
            appDm.scaledDensity = activityDm.scaledDensity
            appDm.densityDpi = activityDm.densityDpi

            mSizeInPx = sizeInPx
            mIsVerticalSlide = isVerticalSlide
        }

        /**
         * Cancel adapt the screen.
         */
        fun cancelAdaptScreen(activity: Activity) {
            val systemDm = Resources.getSystem().displayMetrics
            val appDm = BaseApplication.getApplictionContext.resources.displayMetrics
            val activityDm = activity.resources.displayMetrics
            activityDm.density = systemDm.density
            activityDm.scaledDensity = systemDm.scaledDensity
            activityDm.densityDpi = systemDm.densityDpi

            appDm.density = systemDm.density
            appDm.scaledDensity = systemDm.scaledDensity
            appDm.densityDpi = systemDm.densityDpi
        }


        /**
         * Cancel adapt the screen.
         */
        fun cancelAdaptScreen() {
            val systemDm = Resources.getSystem().displayMetrics
            val appDm = BaseApplication.getApplictionContext.resources.displayMetrics
            val activity = AppManager.getAppManager.currentActivity()
            if (activity != null) {
                val activityDm = activity.resources.displayMetrics
                activityDm.density = systemDm.density
                activityDm.scaledDensity = systemDm.scaledDensity
                activityDm.densityDpi = systemDm.densityDpi
            }
            appDm.density = systemDm.density
            appDm.scaledDensity = systemDm.scaledDensity
            appDm.densityDpi = systemDm.densityDpi
        }

        /**
         * Restore adapt the screen.
         */
        fun restoreAdaptScreen() {
            val systemDm = Resources.getSystem().displayMetrics
            val appDm = BaseApplication.getApplictionContext.resources.displayMetrics
            val activity = AppManager.getAppManager.currentActivity()
            if (activity != null) {
                val activityDm = activity.resources.displayMetrics
                if (mIsVerticalSlide) {
                    activityDm.density = activityDm.widthPixels / mSizeInPx.toFloat()
                } else {
                    activityDm.density = activityDm.heightPixels / mSizeInPx.toFloat()
                }
                activityDm.scaledDensity = activityDm.density * (systemDm.scaledDensity / systemDm.density)
                activityDm.densityDpi = (160 * activityDm.density).toInt()

                appDm.density = activityDm.density
                appDm.scaledDensity = activityDm.scaledDensity
                appDm.densityDpi = activityDm.densityDpi
            } else {
                if (mIsVerticalSlide) {
                    appDm.density = appDm.widthPixels / mSizeInPx.toFloat()
                } else {
                    appDm.density = appDm.heightPixels / mSizeInPx.toFloat()
                }
                appDm.scaledDensity = appDm.density * (systemDm.scaledDensity / systemDm.density)
                appDm.densityDpi = (160 * appDm.density).toInt()
            }
        }

        /**
         * Return whether adapt screen.
         */
        val isAdaptScreen: Boolean
            get() {
                val systemDm = Resources.getSystem().displayMetrics
                val appDm = BaseApplication.getApplictionContext.resources.displayMetrics
                return systemDm.density != appDm.density
            }
    }
}

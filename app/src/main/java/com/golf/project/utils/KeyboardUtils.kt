package com.golf.project.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.golf.project.base.BaseApplication

import java.lang.reflect.Field

/**
 * 作者： Zun.
 * 创建： 2018/11/5.
 * 说明： 系统软键盘管理类
 */
class KeyboardUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    interface OnSoftInputChangedListener {
        fun onSoftInputChanged(height: Int)
    }

    companion object {

        private var sDecorViewInvisibleHeightPre: Int = 0
        private var onGlobalLayoutListener: OnGlobalLayoutListener? = null
        private var onSoftInputChangedListener: OnSoftInputChangedListener? = null
        private var sContentViewInvisibleHeightPre5497: Int = 0

        /**
         * 弹出软键盘
         */
        fun showSoftInput(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager ?: return
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
                view.isFocusable = true
                view.isFocusableInTouchMode = true
                view.requestFocus()
            }
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }

        /**
         * 弹出软键盘
         */
        fun showSoftInput(view: View) {
            val imm = BaseApplication.getApplictionContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.requestFocus()

            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
        }

        /**
         * Show the soft input using toggle.
         */
        fun showSoftInputUsingToggle(activity: Activity) {
            if (isSoftInputVisible(activity)) return
            toggleSoftInput()
        }

        /**
         * 隐藏软键盘
         */
        fun hideSoftInput(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager ?: return
            var view = activity.currentFocus
            if (view == null) view = View(activity)
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        /**
         * 隐藏软键盘
         */
        fun hideSoftInput(view: View) {
            val imm =BaseApplication.getApplictionContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        /**
         * 隐藏软键盘
         */
        fun hideSoftInputUsingToggle(activity: Activity) {
            if (!isSoftInputVisible(activity)) return
            toggleSoftInput()
        }

        /**
         * Toggle the soft input display or not.
         */
        fun toggleSoftInput() {
            val imm = BaseApplication.getApplictionContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        /**
         * Return whether soft input is visible.
         */
        fun isSoftInputVisible(activity: Activity): Boolean {
            return getDecorViewInvisibleHeight(activity) > 0
        }

        private var sDecorViewDelta = 0

        private fun getDecorViewInvisibleHeight(activity: Activity): Int {
            val decorView = activity.window.decorView ?: return sDecorViewInvisibleHeightPre
            val outRect = Rect()
            decorView.getWindowVisibleDisplayFrame(outRect)
            Log.d("KeyboardUtils", "getDecorViewInvisibleHeight: " + (decorView.bottom - outRect.bottom))
            val delta = Math.abs(decorView.bottom - outRect.bottom)
            if (delta <= navBarHeight) {
                sDecorViewDelta = delta
                return 0
            }
            return delta - sDecorViewDelta
        }

        /**
         * Register soft input changed listener.
         */
        fun registerSoftInputChangedListener(
            activity: Activity,
            listener: OnSoftInputChangedListener
        ) {
            val flags = activity.window.attributes.flags
            if (flags and WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS != 0) {
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
            val contentView = activity.findViewById<FrameLayout>(android.R.id.content)
            sDecorViewInvisibleHeightPre = getDecorViewInvisibleHeight(activity)
            onSoftInputChangedListener = listener
            onGlobalLayoutListener = OnGlobalLayoutListener {
                if (onSoftInputChangedListener != null) {
                    val height = getDecorViewInvisibleHeight(activity)
                    if (sDecorViewInvisibleHeightPre != height) {
                        onSoftInputChangedListener!!.onSoftInputChanged(height)
                        sDecorViewInvisibleHeightPre = height
                    }
                }
            }
            contentView.viewTreeObserver
                .addOnGlobalLayoutListener(onGlobalLayoutListener)
        }

        /**
         * Unregister soft input changed listener.
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        fun unregisterSoftInputChangedListener(activity: Activity) {
            val contentView = activity.findViewById<View>(android.R.id.content)
            contentView.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
            onSoftInputChangedListener = null
            onGlobalLayoutListener = null
        }

        /**
         * Fix the bug of 5497 in Android.
         *
         * Don't set adjustResize
         */
        fun fixAndroidBug5497(activity: Activity) {
            val contentView = activity.findViewById<FrameLayout>(android.R.id.content)
            val contentViewChild = contentView.getChildAt(0)
            val paddingBottom = contentViewChild.paddingBottom
            sContentViewInvisibleHeightPre5497 = getContentViewInvisibleHeight(activity)
            contentView.viewTreeObserver
                .addOnGlobalLayoutListener {
                    val height = getContentViewInvisibleHeight(activity)
                    if (sContentViewInvisibleHeightPre5497 != height) {
                        contentViewChild.setPadding(
                            contentViewChild.paddingLeft,
                            contentViewChild.paddingTop,
                            contentViewChild.paddingRight,
                            paddingBottom + getDecorViewInvisibleHeight(activity)
                        )
                        sContentViewInvisibleHeightPre5497 = height
                    }
                }
        }

        private fun getContentViewInvisibleHeight(activity: Activity): Int {
            val contentView =
                activity.findViewById<View>(android.R.id.content) ?: return sContentViewInvisibleHeightPre5497
            val outRect = Rect()
            contentView.getWindowVisibleDisplayFrame(outRect)
            Log.d("KeyboardUtils", "getContentViewInvisibleHeight: " + (contentView.bottom - outRect.bottom))
            val delta = Math.abs(contentView.bottom - outRect.bottom)
            return if (delta <= statusBarHeight + navBarHeight) {
                0
            } else delta
        }

        /**
         * Fix the leaks of soft input.
         *
         * Call the function in [Activity.onDestroy].
         */
        fun fixSoftInputLeaks(context: Context?) {
            if (context == null) return
            val imm = BaseApplication.getApplictionContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val strArr = arrayOf("mCurRootView", "mServedView", "mNextServedView", "mLastSrvView")
            for (i in 0..3) {
                try {

                    val declaredField = imm.javaClass.getDeclaredField(strArr[i]) ?: continue
                    if (!declaredField.isAccessible) {
                        declaredField.isAccessible = true
                    }
                    val obj = declaredField.get(imm)
                    if (obj == null || obj !is View) continue
                    if (obj.context === context) {
                        declaredField.set(imm, null)
                    } else {
                        return
                    }
                } catch (th: Throwable) {
                    th.printStackTrace()
                }

            }
        }

        private val statusBarHeight: Int
            get() {
                val resources = Resources.getSystem()
                val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
                return resources.getDimensionPixelSize(resourceId)
            }

        private val navBarHeight: Int
            get() {
                val res = Resources.getSystem()
                val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
                return if (resourceId != 0) {
                    res.getDimensionPixelSize(resourceId)
                } else {
                    0
                }
            }
    }
}

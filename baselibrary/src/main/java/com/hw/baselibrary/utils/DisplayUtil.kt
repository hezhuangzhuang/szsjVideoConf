package com.hw.baselibrary.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics

/**
 *author：pc-20171125
 *data:2019/11/7 16:08
 */
object DisplayUtil {
    fun px2dp(pxValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun dp2px(dipValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    fun px2sp(pxValue: Float): Int {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    fun sp2px(spValue: Float): Int {
        val fontScale = Resources.getSystem().displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun getScreenWidth(): Int {
        val dm = Resources.getSystem().displayMetrics
        return dm.widthPixels
    }

    fun getScreenHeight(): Int {
        val dm = Resources.getSystem().displayMetrics
        return dm.heightPixels
    }

    fun getNavigationBarHeight(context: Context): Int {
        val mInPortrait =
            context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val result = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavBar(context as Activity)) {
                val key: String
                if (mInPortrait) {
                    key = "navigation_bar_height"
                } else {
                    key = "navigation_bar_height_landscape"
                }
                return getInternalDimensionSize(context, key)
            }
        }
        return result
    }

    private fun hasNavBar(activity: Activity): Boolean {
        //判断小米手机是否开启了全面屏,开启了，直接返回false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Settings.Global.getInt(activity.contentResolver, "force_fsg_nav_bar", 0) != 0) {
                return false
            }
        }
        //其他手机根据屏幕真实高度与显示高度是否相同来判断
        val windowManager = activity.windowManager
        val d = windowManager.defaultDisplay

        val realDisplayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics)
        }

        val realHeight = realDisplayMetrics.heightPixels
        val realWidth = realDisplayMetrics.widthPixels

        val displayMetrics = DisplayMetrics()
        d.getMetrics(displayMetrics)

        val displayHeight = displayMetrics.heightPixels
        val displayWidth = displayMetrics.widthPixels

        return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
    }


    private fun getInternalDimensionSize(context: Context, key: String): Int {
        var result = 0
        try {
            val resourceId = context.resources.getIdentifier(key, "dimen", "android")
            if (resourceId > 0) {
                result =
                    Math.round(context.resources.getDimensionPixelSize(resourceId) * Resources.getSystem().displayMetrics.density / context.resources.displayMetrics.density)
            }
        } catch (ignored: Resources.NotFoundException) {
            return 0
        }

        return result
    }
}
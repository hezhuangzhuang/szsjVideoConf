package com.hw.baselibrary.common

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import java.util.*

/**
 *author：pc-20171125
 *data:2019/11/7 15:40
 */
class AppManager private constructor() {
    private val activityStack: Stack<Activity> = Stack();

    companion object {
        val instance: AppManager by lazy {
            AppManager()
        }
    }

    fun getCurActivity(): Activity? {
        if (activityStack.isEmpty()) {
            return null
        }
        var lastElement: Activity? = activityStack.lastElement()
        if (null == lastElement) {
            popup()

            lastElement = getCurActivity()
        }
        return lastElement
    }

    fun popup() {
        if (activityStack.isEmpty()) {
            return
        }

        val activity = activityStack.pop()
        if (null != activity) {
//            LogUtil.d(UIConstants.DEMO_TAG, activity!!.getLocalClassName())
            activity!!.finish()
        }
    }

    /**
     * activity入栈
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    /**
     * activity出栈
     */
    fun pushActivity(activity: Activity) {
        activity.finish()
        activityStack.remove(activity)
    }

    /**
     * 结束指定类名的activity
     *
     * @param cls
     */
    fun pushActivity(cls: Class<*>) {
        var activity: Activity? = null
        for (a in activityStack) {
            if (a.javaClass == cls) {
                activity = a
                break
            }
        }
        if (activity != null)
            pushActivity(activity)
    }

    /**
     * 清理栈
     */
    fun finishAllActivity() {
        for (activity in activityStack) {
            activity.finish()
        }
        activityStack.clear()
    }

    /**
     * 退出应用程序
     */
    fun exitApp(context: Context) {
        finishAllActivity()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.killBackgroundProcesses(context.packageName)
        System.exit(0)
    }

}
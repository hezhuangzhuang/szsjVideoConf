package com.hw.baselibrary.utils

import android.widget.Toast
import com.hw.baselibrary.common.BaseApp

/**
 *author：pc-20171125
 *data:2019/11/8 11:18
 */
object ToastHelper {
    private fun ToastHelper() {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    var isShow = true

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    fun showShort(message: String) {
        if (isShow)
            Toast.makeText(BaseApp.context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    fun showShort(message: Int) {
        if (isShow)
            Toast.makeText(BaseApp.context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    fun showLong(message: CharSequence) {
        if (isShow)
            Toast.makeText(BaseApp.context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    fun showLong(message: Int) {
        if (isShow)
            Toast.makeText(BaseApp.context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    fun show(message: CharSequence, duration: Int) {
        if (isShow)
            Toast.makeText(BaseApp.context, message, duration).show()
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    fun show(message: Int, duration: Int) {
        if (isShow)
            Toast.makeText(BaseApp.context, message, duration).show()
    }
}

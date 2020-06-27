package com.zxwl.vclibrary.util;

import android.widget.Toast;

import com.huawei.opensdk.commonservice.common.LocContext;


/**
 * Created by Administrator on 2017/11/27.
 */

public class ToastHelper {

    private ToastHelper() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isShow = true;

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(CharSequence message) {
        if (isShow) {
            Toast mToast = Toast.makeText(LocContext.getContext(), "", Toast.LENGTH_SHORT);
            mToast.setText(message);
            mToast.show();
        }
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(int message) {
        if (isShow) {
            Toast mToast = Toast.makeText(LocContext.getContext(), "", Toast.LENGTH_SHORT);
            mToast.setText(message);
            mToast.show();
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(CharSequence message) {
        if (isShow) {
            Toast mToast = Toast.makeText(LocContext.getContext(), "", Toast.LENGTH_SHORT);
            mToast.setText(message);
            mToast.show();
        }
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(int message) {
        if (isShow) {
            Toast mToast = Toast.makeText(LocContext.getContext(), "", Toast.LENGTH_SHORT);
            mToast.setText(message);
            mToast.show();
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    public static void show(CharSequence message, int duration) {
        if (isShow) {
            Toast mToast = Toast.makeText(LocContext.getContext(), "", duration);
            mToast.setText(message);
            mToast.show();
        }
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    public static void show(int message, int duration) {
        if (isShow) {
            Toast mToast = Toast.makeText(LocContext.getContext(), "", duration);
            mToast.setText(message);
            mToast.show();
        }
    }

}

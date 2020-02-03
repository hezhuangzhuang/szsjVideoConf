package com.hw.baselibrary.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.hw.baselibrary.common.BaseApp;


public class VersionUtils {
    /**
     * 获取当前版本号
     * @return
     */
    public static int getVerCode() {
        Context context = BaseApp.context;
        PackageManager manager = context.getPackageManager();
        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 获取当前的版本名称
     * @return
     */
    public static String getVerName() {
        Context context = BaseApp.context;
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }
}

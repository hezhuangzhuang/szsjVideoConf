package com.hw.baselibrary.utils.rom;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;

import com.hw.baselibrary.utils.LogUtils;

import java.lang.reflect.Method;

public class MeizuUtils {
    private static final String TAG = "MeizuUtils";

    /**
     * 检测 meizu 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.KITKAT) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    /**
     * 去魅族权限申请页面
     */
    public static void applyPermission(Context context) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.putExtra("packageName", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                LogUtils.e(" applyPermission Exception ");
                // 最新的魅族flyme 6.2.5 用上述方法获取权限失败, 不过又可以用下述方法获取权限了
                FloatWindowsManager.commonROMPermissionApplyInternal(context);
            } catch (Exception eFinal) {
                LogUtils.e(" commonROMPermissionApplyInternal Exception ");
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                LogUtils.e(" checkOp Exception ");
            }
        } else {
            LogUtils.e(" Below API 19 cannot invoke ");
        }
        return false;
    }
}

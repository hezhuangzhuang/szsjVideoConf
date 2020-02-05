package com.hw.huaweivclib.inter;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.huawei.application.BaseApp;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ecsdk.common.HuaweiConstants;
import com.huawei.opensdk.ecsdk.common.UIConstants;
import com.huawei.opensdk.ecsdk.logic.CallFunc;
import com.huawei.opensdk.ecsdk.logic.ConfFunc;
import com.huawei.opensdk.ecsdk.logic.LoginFunc;
import com.huawei.opensdk.ecsdk.utils.FileUtil;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.servicemgr.ServiceMgr;
import com.huawei.utils.ZipUtil;
import com.hw.baselibrary.utils.ToastHelper;
import com.hw.provider.huawei.commonservice.common.LocContext;
import com.hw.provider.huawei.commonservice.util.CrashUtil;
import com.hw.provider.huawei.commonservice.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * author：pc-20171125
 * data:2020/1/15 17:30
 * 初始化华为的接口
 */
public class HuaweiInitImp {

    //初始化时需要的常量
    public static final String APPLICATION_ID = "com.zxwl.frame";

    private static final int EXPECTED_FILE_LENGTH = 7;


    public static boolean initHuawei(Context application, String appName) {
        //判断进程是否还在
        if (!isFrontProcess(application, APPLICATION_ID)) {
            LocContext.init(application);
            CrashUtil.getInstance().init(application);
            Log.i("SDKDemo", "onCreate: PUSH Process.");
            return true;
        }

        String appPath = application.getApplicationInfo().dataDir + "/lib";
        boolean falg = ServiceMgr.getServiceMgr().startService(application, appPath);

        Log.i(UIConstants.DEMO_TAG, "onCreate: MAIN Process.初始化-->" + falg);

        LoginMgr.getInstance().regLoginEventNotification(LoginFunc.getInstance());
        CallMgr.getInstance().regCallServiceNotification(CallFunc.getInstance());
        MeetingMgr.getInstance().regConfServiceNotification(ConfFunc.getInstance());

        initResourceFile();
        return false;
    }

    /**
     * 华为初始化
     *
     * @param context
     * @param frontPkg
     * @return
     */
    private static boolean isFrontProcess(Context context, String frontPkg) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> infos = null;
        if (manager != null) {
            infos = manager.getRunningAppProcesses();
        }
        if (infos == null || infos.isEmpty()) {
            return false;
        }

        final int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid) {
                Log.i(UIConstants.DEMO_TAG, "processName-->" + info.processName);
                return frontPkg.equals(info.processName);
            }
        }
        return false;
    }

    /**
     * 华为初始化
     */
    private static void initResourceFile() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                initDataConfRes();

                importMediaFile();
                importBmpFile();
                importAnnotFile();
            }
        });
    }

    /**
     * 华为初始化
     */
    private static void initDataConfRes() {
        String path =
                BaseApp.getApp().getFilesDir() + "/AnnoRes";
        File file = new File(path);
        if (file.exists()) {
            LogUtil.i(UIConstants.DEMO_TAG, file.getAbsolutePath());
            File[] files = file.listFiles();
            if (null != files && EXPECTED_FILE_LENGTH == files.length) {
                return;
            } else {
                FileUtil.deleteFile(file);
            }
        }

        try {
            InputStream inputStream = BaseApp.getApp().getAssets().open("AnnoRes.zip");
            ZipUtil.unZipFile(inputStream, path);
        } catch (IOException e) {
//            LogUtil.i(UIConstants.DEMO_TAG, "close...Exception->e" + e.toString());
        }
    }

    private static void importBmpFile() {
        if (FileUtil.isSdCardExist()) {
            try {
                String bmpPath = Environment.getExternalStorageDirectory() + File.separator + HuaweiConstants.BMP_FILE;
                InputStream bmpInputStream = BaseApp.getApp().getAssets().open(HuaweiConstants.BMP_FILE);
                FileUtil.copyFile(bmpInputStream, bmpPath);
            } catch (IOException e) {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }
    }

    private static void importAnnotFile() {
        if (FileUtil.isSdCardExist()) {
            try {
                String bmpPath = Environment.getExternalStorageDirectory() + File.separator + HuaweiConstants.ANNOT_FILE;
                File file = new File(bmpPath);
                if (!file.exists()) {
                    file.mkdir();
                }

                String[] bmpNames = new String[]{
                        "check.bmp",
                        "xcheck.bmp",
                        "lpointer.bmp",
                        "rpointer.bmp",
                        "upointer.bmp",
                        "dpointer.bmp",
                        "lp.bmp"};
                String[] paths = new String[bmpNames.length];

                for (int list = 0; list < paths.length; ++list) {
                    paths[list] = bmpPath + File.separator + bmpNames[list];
                    InputStream bmpInputStream = BaseApp.getApp().getAssets().open(bmpNames[list]);
                    FileUtil.copyFile(bmpInputStream, paths[list]);
                }

            } catch (IOException e) {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }
    }

    private static void importMediaFile() {
        if (FileUtil.isSdCardExist()) {
            try {
                String mediaPath = Environment.getExternalStorageDirectory() + File.separator + HuaweiConstants.RINGING_FILE;
                InputStream mediaInputStream = BaseApp.getApp().getAssets().open(HuaweiConstants.RINGING_FILE);
                FileUtil.copyFile(mediaInputStream, mediaPath);

                String ringBackPath = Environment.getExternalStorageDirectory() + File.separator + HuaweiConstants.RING_BACK_FILE;
                InputStream ringBackInputStream = BaseApp.getApp().getAssets().open(HuaweiConstants.RING_BACK_FILE);
                FileUtil.copyFile(ringBackInputStream, ringBackPath);
            } catch (IOException e) {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }
    }

}

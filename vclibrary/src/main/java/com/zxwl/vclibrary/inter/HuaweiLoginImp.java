package com.zxwl.vclibrary.inter;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.NonNull;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.ctdservice.CtdMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.util.DeviceManager;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.ConfConvertUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.esdk.common.UIConstants;
import com.huawei.opensdk.esdk.login.CallFunc;
import com.huawei.opensdk.esdk.login.ConfFunc;
import com.huawei.opensdk.esdk.login.LoginFunc;
import com.huawei.opensdk.esdk.utils.FileUtil;
import com.huawei.opensdk.loginmgr.LoginConstant;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.loginmgr.LoginParam;
import com.huawei.opensdk.servicemgr.ServiceMgr;
import com.huawei.utils.ZipUtil;
import com.zxwl.vclibrary.util.AppForeBackUtil;
import com.zxwl.vclibrary.util.Constants;
import com.zxwl.vclibrary.util.DateUtil;
import com.zxwl.vclibrary.util.LogUtils;
import com.zxwl.vclibrary.util.ToastHelper;
import com.zxwl.vclibrary.util.sharedpreferences.SPStaticUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * author：pc-20171125
 * data:2019/1/3 11:11
 * 登录对外暴露的接口
 */
public class HuaweiLoginImp {
    private static HuaweiLoginImp loginImp = new HuaweiLoginImp();

    private String TAG = HuaweiLoginImp.class.getSimpleName();

    public static HuaweiLoginImp getInstance() {
        return loginImp;
    }

    private static final int LOGIN_FAILED = 100;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case LOGIN_FAILED:
                    LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_FAILED, msg.obj);
                    break;
            }
        }
    };


    /**
     * 登录
     *
     * @param userName          用户名
     * @param password          密码
     * @param smcRegisterServer smc地址
     * @param smcRegisterPort   smc端口
     * @return
     */
    public int loginRequest(String userName, String password, String smcRegisterServer, String smcRegisterPort) {
        if (null == Looper.myLooper()) {
            Looper.prepare();
        }

        //判断是否有网络
        if (!DeviceManager.isNetworkAvailable(LocContext.getContext())) {
            ToastHelper.showShort("请检查您的网络");
            mHandler.sendEmptyMessage(LOGIN_FAILED);
            return -1;
        }

        LoginParam loginParam = new LoginParam();

        loginParam.setServerUrl(smcRegisterServer);
        loginParam.setServerPort(Integer.parseInt(smcRegisterPort));
        loginParam.setUserName(userName);
        loginParam.setPassword(password);

        LogUtils.i("smcRegisterServer:"+smcRegisterServer+" , smcRegisterPort:"+smcRegisterPort+" , userName:"+userName+" , pwd :"+password);

        SPStaticUtils.put(UIConstants.SMC_URL, smcRegisterServer);
        SPStaticUtils.put(UIConstants.SMC_PORT, smcRegisterPort);
        SPStaticUtils.put(UIConstants.USER_NAME, userName);
        SPStaticUtils.put(UIConstants.PASS_WORD, password);

        loginParam.setVPN(false);

        importFile();

        int loginResult = LoginMgr.getInstance().login(loginParam);
        if (0 != loginResult) {
            mHandler.sendEmptyMessage(LOGIN_FAILED);
        }
        return loginResult;
    }

    /**
     * 登出
     */
    public void logOut() {
        LoginMgr.getInstance().logout();
    }


    /**
     * import file.
     */
    private void importFile() {
        LogUtil.i(UIConstants.DEMO_TAG, "import media file!~");
        Executors.newFixedThreadPool(LoginConstant.FIXED_NUMBER).execute(new Runnable() {
            @Override
            public void run() {
                importMediaFile();
                importBmpFile();
                importAnnotFile();
            }
        });
    }

    private void importBmpFile() {
        if (FileUtil.isSdCardExist()) {
            try {
//                String bmpPath = Environment.getExternalStorageDirectory() + File.separator + Constants.BMP_FILE;
                String bmpPath = LocContext.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + Constants.BMP_FILE;
                InputStream bmpInputStream = LocContext.getContext().getAssets().open(Constants.BMP_FILE);
                FileUtil.copyFile(bmpInputStream, bmpPath);
            } catch (IOException e) {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }
    }

    private void importAnnotFile() {
        if (FileUtil.isSdCardExist()) {
            try {
//                String bmpPath = Environment.getExternalStorageDirectory() + File.separator + Constants.ANNOT_FILE;
                String bmpPath = LocContext.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + Constants.ANNOT_FILE;
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
                        "lp.bmp"
                };
                String[] paths = new String[bmpNames.length];

                for (int list = 0; list < paths.length; ++list) {
                    paths[list] = bmpPath + File.separator + bmpNames[list];
                    InputStream bmpInputStream = LocContext.getContext().getAssets().open(bmpNames[list]);
                    FileUtil.copyFile(bmpInputStream, paths[list]);
                }

            } catch (IOException e) {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }
    }

    private void importMediaFile() {
        if (FileUtil.isSdCardExist()) {
            try {
//                String mediaPath = Environment.getExternalStorageDirectory() + File.separator + Constants.RINGING_FILE;
                String mediaPath = LocContext.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC) + File.separator + Constants.RINGING_FILE;
                InputStream mediaInputStream = LocContext.getContext().getAssets().open(Constants.RINGING_FILE);
                FileUtil.copyFile(mediaInputStream, mediaPath);

//                String ringBackPath = Environment.getExternalStorageDirectory() + File.separator + Constants.RING_BACK_FILE;
                String ringBackPath = LocContext.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC) + File.separator + Constants.RING_BACK_FILE;
                InputStream ringBackInputStream = LocContext.getContext().getAssets().open(Constants.RING_BACK_FILE);
                FileUtil.copyFile(ringBackInputStream, ringBackPath);
            } catch (IOException e) {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }
    }

    /**
     * IDO协议
     */
    private int mIdoProtocol = 0;

    /**
     * 文件长度
     */
    private static final int EXPECTED_FILE_LENGTH = 7;

    /********************************************资源初始化部分 Begin ******************************************************/
    public void initHuawei(Application application, String pageName) {
        //判断时间
        if (DateUtil.test(DateUtil.LEGITIMATE_TIME)) {
            return;
        }

        LocContext.init(application);
        if (!isFrontProcess(application, pageName)) {
//            CrashUtil.getInstance().init(application);
            Log.i("SDKDemo", "onCreate: PUSH Process.");
            return;
        }

        MeetingMgr.getInstance().setConfProtocol(ConfConvertUtil.convertConfctrlProtocol(0));

        String appPath = application.getApplicationInfo().dataDir + "/lib";
        boolean startServiceResult = ServiceMgr.getServiceMgr().startService(application, appPath, mIdoProtocol);
        Log.i("SDKDemo", "onCreate: MAIN Process.");

        LoginMgr.getInstance().regLoginEventNotification(LoginFunc.getInstance());
        CallMgr.getInstance().regCallServiceNotification(CallFunc.getInstance());
        CtdMgr.getInstance().regCtdNotification(CallFunc.getInstance());
        MeetingMgr.getInstance().regConfServiceNotification(ConfFunc.getInstance());
//        EnterpriseAddressBookMgr.getInstance().registerNotification(EnterpriseAddrBookFunc.getInstance());

        ServiceMgr.getServiceMgr().securityParam(
                Constants.SRTP_MODE,
                Constants.SIP_TRANSPORT_MODE,
                Constants.APP_CONFIG,
                Constants.TUNNEL_MODE
        );

        ServiceMgr.getServiceMgr().networkParam(
                Constants.UDP_DEFAULT,
                Constants.TLS_DEFAULT,
                Constants.PORT_CONFIG_PRIORITY
        );

        initResourceFile();

        appForeBackUtil = new AppForeBackUtil();
        appForeBackUtil.register(application, new AppForeBackUtil.AppForeBackExchangeListener() {
            @Override
            public void toFront() {
                //TODO 应用进入前台后的操作
//                ToastHelper.showShort("回到前台");

                if (!LoginMgr.getInstance().isLogin()) {
                    String smcUrl = SPStaticUtils.getString(UIConstants.SMC_URL);
                    String smcPort = SPStaticUtils.getString(UIConstants.SMC_PORT);
                    String userName = SPStaticUtils.getString(UIConstants.USER_NAME);
                    String pwd = SPStaticUtils.getString(UIConstants.PASS_WORD);

                    if (TextUtils.isEmpty(smcUrl)
                            || TextUtils.isEmpty(userName)
                            || TextUtils.isEmpty(pwd)
                    ) {
                        return;
                    }

//                    loginRequest(userName, pwd, smcUrl, smcPort);
                }
            }

            @Override
            public void toBack() {
                //TODO 应用进入后台后的操作
//                ToastHelper.showShort("进入后台");
            }
        });
    }

    private AppForeBackUtil appForeBackUtil;

    /**
     * 是否在前台
     *
     * @param context
     * @param frontPkg
     * @return
     */
    private static boolean isFrontProcess(Context context, String frontPkg) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
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
     * 引入资源
     */
    private void initResourceFile() {
        Executors.newFixedThreadPool(LoginConstant.FIXED_NUMBER)
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        initDataConfRes();
                    }
                });
    }

    private void initDataConfRes() {
        String path = LocContext.getContext().getFilesDir() + "/AnnoRes";
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

//        try {
//            InputStream inputStream = LocContext.getContext().getAssets().open("AnnoRes.zip");
//            ZipUtil.unZipFile(inputStream, path);
//        } catch (IOException e) {
//            LogUtil.i(UIConstants.DEMO_TAG, "close...Exception->e" + e.toString());
//        }
    }

    /**
     * 移除视频栈的list
     *
     * @param context
     */
    private void removeMoreTask(Context context) {
        List<String> confActions = Arrays.asList(new String[]{
                "android.intent.action.voice_conf_manager_activity",
                "android.intent.action.conf_manager_activity",
                "android.intent.action.select_people_activity"});
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTasks) {
            if (confActions.contains(appTask.getTaskInfo().baseIntent.getAction())) {
                appTask.finishAndRemoveTask();
            }
        }
    }

    private void removeMoreTask() {
        List<String> confAction = Arrays.asList("android.intent.action.voice_conf_manager_activity", "android.intent.action.conf_manager_activity", "android.intent.action.select_people_activity");
        List<String> confClass = Arrays.asList("com.hw.cloudlibrary.activity.SelectPeopleActivity");
        ActivityManager activityManager = (ActivityManager) LocContext.getContext().getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
//        for (ActivityManager.AppTask appTask : appTasks) {
//            if (confAction.contains(appTask.getTaskInfo().baseIntent.getAction())) {
//                appTask.finishAndRemoveTask();
//            } else if (confClass.contains(appTask.getTaskInfo().baseIntent.getComponent().getClassName())) {
//                appTask.finishAndRemoveTask();
//            }
//        }
    }

    /********************************************资源初始化部分 End ******************************************************/

}

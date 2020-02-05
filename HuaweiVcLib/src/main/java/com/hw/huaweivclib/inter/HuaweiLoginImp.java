package com.hw.huaweivclib.inter;

import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;

import com.huawei.opensdk.ecsdk.common.UIConstants;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.loginmgr.LoginParam;
import com.huawei.utils.DeviceManager;
import com.hw.baselibrary.common.BaseApp;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.huaweivclib.service.AudioStateWatchService;
import com.hw.provider.huawei.commonservice.common.LocContext;
import com.hw.provider.huawei.commonservice.localbroadcast.CustomBroadcastConstants;
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcast;
import com.hw.provider.huawei.commonservice.util.LogUtil;

/**
 * author：pc-20171125
 * data:2020/1/15 17:30
 * 华为登录相关的接口
 */
public class HuaweiLoginImp {
    /**
     * 登录华为平台
     */
    public static void login(String userName,
                             String password,
                             String smcRegisterServer,
                             String smcRegisterPort) {

        LogUtil.d("userName-->" + userName +
                ",password-->" + password +
                ",smcRegisterServer-->" + smcRegisterServer +
                ",smcRegisterPort-->" + smcRegisterPort
        );
        if (!DeviceManager.isNetworkAvailable(BaseApp.context)) {
            return;
        }

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            return;
        }

        String regServerAddress = smcRegisterServer;
        String serverPort = smcRegisterPort;

        if (TextUtils.isEmpty(regServerAddress)) {
            return;
        }

        if (TextUtils.isEmpty(serverPort)) {
            return;
        }

        if (null == Looper.myLooper()) {
            Looper.prepare();
        }

        LoginParam loginParam = new LoginParam();

        loginParam.setServerPort(Integer.parseInt(serverPort));
        loginParam.setProxyPort(Integer.parseInt(serverPort));
        loginParam.setServerUrl(regServerAddress);
        loginParam.setProxyUrl(regServerAddress);
        loginParam.setUserName(userName);
        loginParam.setPassword(password);

        loginParam.setVPN(false);

        loginParam.setSrtpMode(0);
        int mode = "5061".equals(serverPort) ? 1 : 0;

        //TLS:5061
        //UDP:5060
        //TCP:5060
        loginParam.setSipTransportMode(mode);//UDP:0,TLS:1,TCP:2
        loginParam.setServerType(2);

        final int login = LoginMgr.getInstance().login(loginParam);
    }

    /**
     * 登出
     */
    public static void logOut() {
        SPStaticUtils.put(UIConstants.IS_LOGOUT, true);

        String state = "";
        try {
            state = SPStaticUtils.getString(UIConstants.REGISTER_RESULT_TEMP);
        } catch (Exception e) {
        }
        if (!"3".equals(state)) {
            //没有调用登出接口
            //如果网络连接成功
            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGOUT, null);
        } else {
            LoginMgr.getInstance().logout();
        }
        LocContext.getContext().stopService(new Intent(LocContext.getContext(), AudioStateWatchService.class));
    }
}

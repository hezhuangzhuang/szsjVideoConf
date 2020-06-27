package com.zxwl.vclibrary.arouter;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hw.provider.router.RouterPath;
import com.hw.provider.router.provider.huawei.IHuaweiModuleService;
import com.zxwl.vclibrary.inter.HuaweiCallImp;
import com.zxwl.vclibrary.inter.HuaweiLoginImp;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * author：pc-20171125
 * data:2020/1/16 10:18
 */
@Route(path = RouterPath.Huawei.HUAWEI_MODULE_SERVICE)
public class HuaweiModuleServiceImp implements IHuaweiModuleService {
    @Override
    public void login(@NotNull String userName, @NotNull String password, @NotNull String smcRegisterServer, @NotNull String smcRegisterPort) {
        HuaweiLoginImp.getInstance().loginRequest(userName, password, smcRegisterServer, smcRegisterPort);
    }

    @Override
    public void logOut() {
        HuaweiLoginImp.getInstance().logOut();
    }

    @Override
    public void callSite(@NotNull String siteNumber, boolean isVideoCall) {
        HuaweiCallImp.getInstance().callSite(siteNumber, isVideoCall);
    }

    @Override
    public void createConfNetWork(@NotNull String confName,
                                  @NotNull String duration,
                                  @NotNull String accessCode,
                                  @NotNull String memberSipList,
                                  @NotNull String groupId,
                                  @NotNull int type) {
//        HuaweiCallImp.createConfNetWork(
//                confName,
//                duration,
//                accessCode,
//                memberSipList,
//                groupId,
//                type);
    }

    @Override
    public void init(Context context) {

    }

    /**
     * 预约会议
     *
     * @param confName      会议名称
     * @param duration      会议时间
     * @param accessCode    会议接入码
     * @param memberSipList 参会人员
     * @param groupId       群组id
     * @param type          0：语音会议，1：视频会议
     * @param confType      0：即时会议，1：预约会议
     * @param startTime     会议开始时间
     */
    @Override
    public boolean reservedConfNetWork(@NotNull String confName, @NotNull String duration, @NotNull String accessCode, @NotNull String memberSipList, @NotNull String groupId, int type, @NotNull String confType, @NotNull String startTime) {
//        return HuaweiCallImp.reservedConfNetWork(
//                confName,
//                duration,
//                accessCode,
//                memberSipList,
//                groupId,
//                type,
//                confType,
//                startTime);
        return false;
    }

    @Override
    public void initHuawei(@NotNull Context application, @NotNull String appName) {
        HuaweiLoginImp.getInstance().initHuawei((Application) application, appName);
    }

    @Override
    public boolean joinConfNetWork(@NotNull String smcConfId, @NotNull String siteUri) {
//        return HuaweiCallImp.joinConfNetWork(smcConfId, siteUri);
        return false;
    }

    @Override
    public void createConf(@NotNull String confName, @NotNull List<String> memberSipList, boolean isVideoConf) {
        HuaweiCallImp.getInstance().createConf(confName, memberSipList, isVideoConf);
    }

    @Override
    public void joinConf(@NotNull String accessCode) {
//        HuaweiCallImp.getInstance().joinConf(accessCode);
        HuaweiCallImp.getInstance().joinConf(accessCode, accessCode);

//        HuaweiCallImp.getInstance().callSite(accessCode, true);
    }
}

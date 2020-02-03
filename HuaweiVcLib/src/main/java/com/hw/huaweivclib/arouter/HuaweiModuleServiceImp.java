package com.hw.huaweivclib.arouter;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hw.huaweivclib.inter.HuaweiCallImp;
import com.hw.huaweivclib.inter.HuaweiInitImp;
import com.hw.huaweivclib.inter.HuaweiLoginImp;
import com.hw.provider.router.RouterPath;
import com.hw.provider.router.provider.huawei.IHuaweiModuleService;

import org.jetbrains.annotations.NotNull;

/**
 * author：pc-20171125
 * data:2020/1/16 10:18
 */
@Route(path = RouterPath.Huawei.HUAWEI_MODULE_SERVICE)
public class HuaweiModuleServiceImp implements IHuaweiModuleService {
    @Override
    public void login(@NotNull String userName, @NotNull String password, @NotNull String smcRegisterServer, @NotNull String smcRegisterPort) {
        HuaweiLoginImp.login(userName, password, smcRegisterServer, smcRegisterPort);
    }

    @Override
    public void logOut() {
        HuaweiLoginImp.logOut();
    }

    @Override
    public void callSite(@NotNull String siteNumber, boolean isVideoCall) {
        HuaweiCallImp.callSite(siteNumber, isVideoCall);
    }

    @Override
    public void createConfNetWork(@NotNull String confName,
                                  @NotNull String duration,
                                  @NotNull String accessCode,
                                  @NotNull String memberSipList,
                                  @NotNull String groupId,
                                  @NotNull int type) {
        HuaweiCallImp.createConfNetWork(
                confName,
                duration,
                accessCode,
                memberSipList,
                groupId,
                type);
    }

    @Override
    public void init(Context context) {

    }

    @Override
    public void joinConf(@NotNull String accessCode) {
        HuaweiCallImp.joinConf(accessCode);
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
        return HuaweiCallImp.reservedConfNetWork(
                confName,
                duration,
                accessCode,
                memberSipList,
                groupId,
                type,
                confType,
                startTime);
    }

    @Override
    public void initHuawei(@NotNull Context application, @NotNull String appName) {
        HuaweiInitImp.initHuawei(application,appName);
    }
}

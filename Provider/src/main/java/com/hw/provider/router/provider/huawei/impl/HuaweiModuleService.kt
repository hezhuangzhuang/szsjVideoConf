package com.hw.provider.router.provider.huawei.impl

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.hw.provider.router.provider.huawei.IHuaweiModuleService

/**
 *author：pc-20171125
 *data:2020/1/16 10:18
 */
object HuaweiModuleService {
    val navigation =
        ARouter.getInstance().navigation(IHuaweiModuleService::class.java)


    /**
     * 初始化华为
     */
    fun initHuawei(application: Context, appName: String) {
        navigation.initHuawei(application, appName)
    }

    /**
     * 登录的方法
     */
    fun login(
        userName: String,
        password: String,
        smcRegisterServer: String,
        smcRegisterPort: String
    ) {
        navigation.login(userName, password, smcRegisterServer, smcRegisterPort)
    }

    /**
     * 登出
     */
    fun logOut() {
        navigation.logOut()
    }

    /**
     * 呼叫会场
     */
    fun callSite(siteNumber: String, isVideoCall: Boolean) {
        navigation.callSite(siteNumber, isVideoCall)
    }

    /**
     * 通过后台接口召集会议
     * @param confName      会议名称
     * @param duration      会议时长，单位(分钟)
     * @param memberSipList 参会人员的sip号码，多个以逗号分隔
     * @param groupId
     * @param accessCode    会议接入码
     * @param type          0：语音会议，1：视频会议
     */
    fun createConfNetWork(
        confName: String,
        duration: String,
        accessCode: String,
        memberSipList: String,
        groupId: String,
        type: Int
    ) {
        navigation.createConfNetWork(confName, duration, accessCode, memberSipList, groupId, type)
    }

    /**
     * 通过后台接口预约会议
     * @param confName      会议名称
     * @param duration      会议时长，单位(分钟)
     * @param memberSipList 参会人员的sip号码，多个以逗号分隔
     * @param groupId
     * @param accessCode    会议接入码
     * @param type          0：语音会议，1：视频会议
     * @param confType          0：即使会议，1：预约会议
     * @param startTime     会议开始时间
     */
    fun reservedConfNetWork(
        confName: String,
        duration: String,
        accessCode: String,
        memberSipList: String,
        groupId: String,
        type: Int,
        confType: String,
        startTime: String
    ): Boolean {
        return navigation.reservedConfNetWork(
            confName,
            duration,
            accessCode,
            memberSipList,
            groupId,
            type,
            confType,
            startTime
        )
    }

    /**
     * 呼叫会场
     */
    fun callSite(accessCode: String) {
        navigation.joinConf(accessCode)
    }
}
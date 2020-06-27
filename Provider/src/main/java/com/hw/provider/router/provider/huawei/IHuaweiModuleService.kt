package com.hw.provider.router.provider.huawei

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

/**
 *author：pc-20171125
 *data:2020/1/16 10:09
 *
 * 华为模块提供的方法
 */
interface IHuaweiModuleService : IProvider {

    /**
     * 登录的方法
     */
    fun login(
        userName: String,
        password: String,
        smcRegisterServer: String,
        smcRegisterPort: String
    )

    /**
     * 登出
     */
    fun logOut()

    /**
     * 呼叫会场
     */
    fun callSite(siteNumber: String, isVideoCall: Boolean)

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
    )

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
    ): Boolean

    /**
     * 加入会议
     */
    fun joinConf(accessCode: String)

    /**
     * 加入会议调用网络接口
     */
    fun joinConfNetWork(
        smcConfId: String,
        siteUri: String
    ): Boolean

    /**
     * 初始化华为
     */
    fun initHuawei(application: Context, appName: String)


    /**
     * 通过sdk接口召集会议
     * @param confName      会议名称
     * @param memberSipList 参会人员的sip号码，多个以逗号分隔
     * @param isVideoConf   false：语音会议，true：视频会议
     */
    fun createConf(
        confName: String,
        memberSipList: List<String>,
        isVideoConf: Boolean
    )
}
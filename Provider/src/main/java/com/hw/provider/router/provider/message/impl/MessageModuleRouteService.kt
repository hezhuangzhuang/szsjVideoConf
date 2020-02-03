package com.hw.provider.router.provider.message.impl

import com.alibaba.android.arouter.launcher.ARouter
import com.hw.provider.chat.bean.MessageBody
import com.hw.provider.router.provider.message.IMessageModuleService

/**
 *author：pc-20171125
 *data:2020/1/15 10:10
 */
object MessageModuleRouteService {

    //初始化数据库
    fun initDb() {
        ARouter.getInstance().navigation(IMessageModuleService::class.java).initDb()
    }

    /**
     * 发送消息
     */
    fun sendMessage(message: MessageBody): Boolean{
        val navigation = ARouter.getInstance().navigation(IMessageModuleService::class.java)
        return navigation.sendMessage(message)
    }
}
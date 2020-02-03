package com.hw.provider.router.provider.message

import com.alibaba.android.arouter.facade.template.IProvider
import com.hw.provider.chat.bean.MessageBody

/**
 *author：pc-20171125
 *data:2020/1/15 10:06
 * 数据库暴露出来的接口
 */
interface IMessageModuleService :IProvider {

    /**
     * 初始化数据库
     */
    fun initDb()

    /**
     * 发送消息
     */
    fun sendMessage(message: MessageBody): Boolean

}
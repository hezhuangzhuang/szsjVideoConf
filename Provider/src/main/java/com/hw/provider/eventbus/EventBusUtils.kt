package com.hw.provider.eventbus

import org.greenrobot.eventbus.EventBus

/**
 *author：pc-20171125
 *data:2020/1/14 13:45
 * event的工具类
 */
object EventBusUtils {
    /**
     * 发送消息
     */
    fun sendMessage(message: String, t: Any) {
        var eventMsg = EventMsg<Any>(message, t)
        EventBus.getDefault().post(eventMsg)
    }
}
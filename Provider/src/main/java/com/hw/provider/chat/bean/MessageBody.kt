package com.hw.provider.chat.bean

/**
 *author：pc-20171125
 *data:2020/1/13 16:22
 * 消息体
 */
data class MessageBody(
    //由于sendId与receiveId设计为数字型，导致有可能重复，目前存在以下限制
    //sendId只能为用户ID，不能为群组ID
    var sendId: String,
    var sendName: String,
    //当type = 1时，receiveId为用户ID
    //当type = 2时，receiveId为群组ID
    var receiveId: String,
    var receiveName: String,
    // 1:私聊；2:群聊
    var type: Int,
    var real: MessageReal
) {

    companion object {
        /**
         * 私聊
         */
        val TYPE_PERSONAL = 1

        /**
         * 群聊
         */
        val TYPE_COMMON = 2
    }
}
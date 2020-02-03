package com.hw.provider.eventbus

/**
 *author：pc-20171125
 *data:2020/1/14 11:59
 * 发送evebtbus时的消息
 */
data class EventMsg<T>(
    var message: String,
    var messageData: T
) {

    companion object {
        //收到消息
        val RECEIVE_SINGLE_MESSAGE = "RECEIVE_SINGLE_MESSAGE"

        //发出的消息更新，一般是视频之后发送
        val SEND_SINGLE_MESSAGE = "SEND_SINGLE_MESSAGE"

        //刷新首页消息
        val REFRESH_HOME_MESSAGE = "REFRESH_HOME_MESSAGE"

        //更新消息的阅读状态
        val UPDATE_MESSAGE_READ_STATUS = "UPDATE_MESSAGE_READ_STATUS"

        //更新首页消息提醒
        val UPDATE_MAIN_NOTIF = "UPDATE_MAIN_NOTIF"

        //网络连接成功
        val NET_WORK_CONNECT = "NET_WORK_CONNECT"

        //网络连接断开
        val NET_WORK_DISCONNECT = "NET_WORK_DISCONNECT"

        //登出
        val LOGOUT = "LOGOUT"

        //更新群聊列表,修改群名称
        val UPDATE_GROUP_CHAT = "UPDATE_GROUP_CHAT"

        //删除群聊
        val DELETE_GROUP_CHAT = "DELETE_GROUP_CHAT"

        //添加人员到群组
        val ADD_PEOPLE_TO_GROUPCHAT = "ADD_PEOPLE_TO_GROUPCHAT"
    }

}
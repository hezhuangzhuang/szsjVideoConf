package com.hw.provider.chat.utils

import com.hw.provider.chat.ChatMultipleItem
import com.hw.provider.chat.bean.ChatBean
import com.hw.provider.chat.bean.MessageBody
import com.hw.provider.chat.bean.MessageReal
import java.util.*

/**
 *author：pc-20171125
 *data:2020/1/15 11:44
 * 消息的工具类
 */
object MessageUtils {
    /**
     * 将接收到的点对点messagebody转成chatbean对象
     */
    fun receiveMessageToChatBean(messageBody: MessageBody): ChatBean {
        return ChatBean.Builder()
            //消息类型,发送消息
            .setMessageType(
                getReceiveMessageType(
                    messageBody.real.type
                )
            )
            //发送人名称
            .setName(messageBody.sendName)
            //发送时间
            .setSendDate(Date())
            //消息内容
            .setContent(getMessageContent(messageBody))
            //是否发送
            .setSend(false)
            //会话id
            .setConversationId(messageBody.sendId)
            //会话名称
            .setConversationUserName(messageBody.sendName)
            //是否群聊
            .setGroup(messageBody.type == MessageBody.TYPE_COMMON)
            //是否已读
            .setRead(false)
            .builder()
    }

    /**
     * 将发出的点对点messagebody转成chatbean对象
     */
    fun sendMessageToChatBean(messageBody: MessageBody): ChatBean {
        return ChatBean.Builder()
            //消息类型,发送消息
            .setMessageType(getSendMessageType(messageBody.real.type))
            //发送人名称
            .setName(messageBody.sendName)
            //发送时间
            .setSendDate(Date())
            //消息内容
            .setContent(getMessageContent(messageBody))
            //是否发送
            .setSend(true)
            //会话id
            .setConversationId(messageBody.receiveId)
            //会话名称
            .setConversationUserName(messageBody.receiveName)
            //是否群聊
            .setGroup(messageBody.type == MessageBody.TYPE_COMMON)
            //是否已读
            .setRead(true)
            .builder()
    }

    /**
     * 将接收到的群聊messagebody转成chatbean对象
     */
    fun receiveGroupMessageToChatBean(messageBody: MessageBody): ChatBean {
        return ChatBean.Builder()
            //消息类型,发送消息
            .setMessageType(
                getReceiveMessageType(
                    messageBody.real.type
                )
            )
            //发送人名称
            .setName(messageBody.sendName)
            //发送时间
            .setSendDate(Date())
            //消息内容
            .setContent(getMessageContent(messageBody))
            //是否发送
            .setSend(false)
            //会话id
            .setConversationId(messageBody.receiveId)
            //会话名称
            .setConversationUserName(messageBody.receiveName)
            //是否群聊
            .setGroup(true)
            //是否已读
            .setRead(false)
            .builder()
    }

    /**
     * 获取收到消息的类型
     */
    fun getReceiveMessageType(type: Int): Int {
        when (type) {
            //文字
            MessageReal.TYPE_STR ->
                return ChatMultipleItem.FORM_TEXT

            //图片
            MessageReal.TYPE_IMG ->
                return ChatMultipleItem.FORM_IMG

            //附件，语音
            MessageReal.TYPE_APPENDIX ->
                return ChatMultipleItem.FORM_VOICE

            //视频呼叫
            MessageReal.TYPE_VIDEO_CALL ->
                return ChatMultipleItem.FORM_VIDEO_CALL

            //音频呼叫
            MessageReal.TYPE_VOICE_CALL ->
                return ChatMultipleItem.FORM_VOICE_CALL

            else ->
                return ChatMultipleItem.FORM_TEXT
        }
    }

    /**
     * 获取发出消息的类型
     */
    fun getSendMessageType(type: Int): Int {
        when (type) {
            //文字
            MessageReal.TYPE_STR ->
                return ChatMultipleItem.SEND_TEXT

            //图片
            MessageReal.TYPE_IMG ->
                return ChatMultipleItem.SEND_IMG

            //附件，语音
            MessageReal.TYPE_APPENDIX ->
                return ChatMultipleItem.SEND_VOICE

            //视频呼叫
            MessageReal.TYPE_VIDEO_CALL ->
                return ChatMultipleItem.SEND_VIDEO_CALL

            //音频呼叫
            MessageReal.TYPE_VOICE_CALL ->
                return ChatMultipleItem.SEND_VOICE_CALL

            else ->
                return ChatMultipleItem.SEND_TEXT
        }
    }

    /**
     * 获取消息的内容
     */
    fun getMessageContent(messageBody: MessageBody): String {
        when (messageBody.real.type) {
            //文字
            MessageReal.TYPE_STR ->
                return messageBody.real.message

            //图片
            MessageReal.TYPE_IMG ->
                return messageBody.real.imgUrl

//            //附件，语音
//            MessageReal.TYPE_APPENDIX ->
//                return ChatMultipleItem.FORM_VOICE
//
//            //视频呼叫
//            MessageReal.TYPE_VIDEO_CALL ->
//                return ChatMultipleItem.FORM_VIDEO_CALL
//
//            //音频呼叫
//            MessageReal.TYPE_VOICE_CALL ->
//                return ChatMultipleItem.FORM_VOICE_CALL

            else ->
                return messageBody.real.message
        }
    }
}
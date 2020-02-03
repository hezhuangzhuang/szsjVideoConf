package com.hw.provider.chat

/**
 *author：pc-20171125
 *data:2020/1/13 20:29
 */
object ChatMultipleItem {
    val SEND_TEXT = 1//发送纯文本
    val FORM_TEXT = 2//收到纯文本

    val SEND_IMG = 3//发送图片
    val FORM_IMG = 4//收到图片

    val SEND_VOICE = 5//发送语音
    val FORM_VOICE = 6//收到语音

    val SEND_FILE = 7//发送文件
    val FORM_FILE = 8//收到文件

    val NOTIFY = 9//收到通知

    val SEND_VIDEO_CALL = 10//发出的视频呼叫
    val FORM_VIDEO_CALL = 11//收到的视频呼叫

    val SEND_VOICE_CALL = 12//发出的语音呼叫
    val FORM_VOICE_CALL = 13//收到的语音呼叫
}

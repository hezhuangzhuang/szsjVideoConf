package com.hw.provider.chat.bean

/**
 *author：pc-20171125
 *data:2020/1/13 16:24
 * 消息内容
 */
data class MessageReal(
    var message: String,
    var type: Int,
    //图片的全路径，只有在type为TYPE_IMG时才有用
    var imgUrl: String
) {
    companion object {
        /**
         * 文字
         */
        val TYPE_STR = 1

        /**
         * 图片
         */
        val TYPE_IMG = 2

        /**
         * 表情
         */
        val TYPE_EMOJI = 3

        /**
         * 附件
         */
        val TYPE_APPENDIX = 4

        /**
         * 通知
         */
        val TYPE_NOTIFY = 5

        /**
         * 视频呼叫
         */
        val TYPE_VIDEO_CALL = 6

        /**
         * 语音呼叫
         */
        val TYPE_VOICE_CALL = 7
    }

}
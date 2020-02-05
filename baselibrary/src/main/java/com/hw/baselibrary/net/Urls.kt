package com.hw.baselibrary.net

import okhttp3.MediaType

/**
 *author：pc-20171125
 *data:2019/11/8 11:51
 */
object Urls {
    val MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8")

    const val BASE_URL = "http://61.182.50.12:8085/xjdj/"

    //公司环境-start
    //即时通讯和基础业务的路径
    const val WEBSOCKET_TEST = "http://demo.szzxwl.com:9016/"
    //上传文件的服务器
    const val FILE_TEST = "http://demo.szzxwl.com:9012/videoConf/"
    //公司环境-end


    //客户环境-start
    //即时通讯和基础业务的路径
    const val WEBSOCKET_FORMAL = "http://120.221.95.142:7001/"
    //上传文件的服务器
    const val FILE_FORMAL = "http://120.221.95.142:7002/videoConf/"
    //客户环境-end

    //即时通讯和基础业务的路径
    public var WEBSOCKET_URL = WEBSOCKET_TEST

    //上传文件的服务器
    public var FILE_URL = FILE_TEST

}
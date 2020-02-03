package com.hw.baselibrary.net.networkmonitor

import java.lang.reflect.Method

/**
 *author：pc-20171125
 *data:2020/1/18 10:38
 */
class MethodManager {
    //被注解方法的参数类型 NetType netType
    private var type: Class<*>? = null

    //需要监听的网络类型
    private var netType: NetType? = null

    //需要执行的方法
    private var method: Method? = null

     constructor(type: Class<*>, netType: NetType, method: Method) {
        this.type = type
        this.netType = netType
        this.method = method
    }

    fun getType(): Class<*>? {
        return type
    }

    fun setType(type: Class<*>) {
        this.type = type
    }

    fun getNetType(): NetType? {
        return netType
    }

    fun setNetType(netType: NetType) {
        this.netType = netType
    }

    fun getMethod(): Method? {
        return method
    }

    fun setMethod(method: Method) {
        this.method = method
    }
}
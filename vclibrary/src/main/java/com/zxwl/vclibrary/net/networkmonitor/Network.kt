package com.hw.baselibrary.net.networkmonitor


/**
 *author：pc-20171125
 *data:2020/1/18 10:39
 */
@Target(AnnotationTarget.FUNCTION)//描述方法的注解
@Retention(AnnotationRetention.RUNTIME)//运行时注解
annotation class Network(val netType: NetType = NetType.AUTO)
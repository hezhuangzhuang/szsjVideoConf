package com.hw.baselibrary.net.networkmonitor

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import com.hw.baselibrary.utils.LogUtils
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 *author：pc-20171125
 *data:2020/1/18 10:31
 * 监听网络是否连接
 */
class NetworkCallbackImpl : ConnectivityManager.NetworkCallback() {
    /**
     * 当网络连接的属性被修改时调用
     */
    override fun onLinkPropertiesChanged(network: Network?, linkProperties: LinkProperties?) {
        super.onLinkPropertiesChanged(network, linkProperties)
    }

    /**
     * 当网络连接超时或网络请求达不到可用要求时调用
     */
    override fun onUnavailable() {
        super.onUnavailable()
    }

    /**
     * 当网络正在断开连接时调用
     */
    override fun onLosing(network: Network?, maxMsToLive: Int) {
        super.onLosing(network, maxMsToLive)
        LogUtils.i("NetworkCallback-->网络正在断开")
    }


    private val TAG = "NetworkCallbackImpl"
    private var networkList: MutableMap<Any, List<MethodManager>>? =
        java.util.HashMap()

    /**
     * 网络连接成功，通知可以使用的时候调用
     */
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        LogUtils.i("NetworkCallback-->网络连接了")
    }


    /**
     * 当网络已断开连接时调用
     */
    override fun onLost(network: Network) {
        super.onLost(network)
        LogUtils.i("NetworkCallback-->网络断开了")
        post(NetType.NONE)
    }

    /**
     * 当网络状态修改但仍旧是可用状态时调用
     */
    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                post(NetType.WIFI)
            } else {
                post(NetType.MOBILE)
            }
        }
    }

    /**
     * 通知所有注册的方法，网络发生了改变
     * @param netType
     */
    private fun post(netType: NetType) {
        val sets = networkList!!.keys
        for (observer in sets) {
            val methodList = networkList!!.get(observer)
            methodList!!.forEach { method ->
                if (method.getType()!!.isAssignableFrom(netType.javaClass)) {
                    if (method.getNetType() === netType ||
                        netType === NetType.NONE ||
                        method.getNetType() === NetType.AUTO
                    ) {
                        invoke(method, observer, netType)
                    }
                }
            }
        }
    }

    private operator fun invoke(methodManager: MethodManager, observer: Any, netType: NetType) {
        try {
            val execute = methodManager.getMethod()
            execute!!.invoke(observer, netType)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }

    }

    /**
     * 注册监听
     * @param observer
     */
    fun registerObserver(observer: Any) {
        var methodList: List<MethodManager>? = networkList!!.get(observer)
        if (methodList == null) {
            methodList = getAnnotationMethod(observer)
            networkList!!.put(observer, methodList)
        }
    }

    /**
     * 遍历注册类中的所有方法，收集被注解方法的信息
     * @param observer
     * @return
     */
    private fun getAnnotationMethod(observer: Any): List<MethodManager> {
        val methodList = ArrayList<MethodManager>()
        val methods = observer.javaClass.methods
        for (method in methods) {
            val network = method.getAnnotation(com.hw.baselibrary.net.networkmonitor.Network::class.java!!) ?: continue
            Log.e(TAG, "NETWORK.....")
            //校验返回值
            val returnType = method.genericReturnType
            if ("void" != returnType.toString()) {
                throw RuntimeException(method.name + "return type should be null")
            }
            //校验参数
            val parameterTypes = method.parameterTypes
            if (parameterTypes.size != 1) {
                throw RuntimeException(method.name + "arguments should be one")
            }

            val methodManager = MethodManager(
                parameterTypes[0],
                network.netType,
                method
            )
            methodList.add(methodManager)
        }
        return methodList
    }

    fun unRegisterObserver(observer: Any) {
        if (!networkList!!.isEmpty()) {
            networkList!!.remove(observer)
        }
    }

    fun unRegisterAllObserver() {
        if (!networkList!!.isEmpty()) {
            networkList!!.clear()
        }
        NetworkManager.getConnectivityManager()!!.unregisterNetworkCallback(this)
        networkList = null
    }

}
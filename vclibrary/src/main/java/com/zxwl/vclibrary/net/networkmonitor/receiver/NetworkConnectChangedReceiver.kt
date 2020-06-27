package com.hw.baselibrary.net.networkmonitor.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.hw.baselibrary.net.networkmonitor.MethodManager
import com.hw.baselibrary.net.networkmonitor.NetType
import com.hw.baselibrary.net.networkmonitor.Network
import com.hw.baselibrary.net.networkmonitor.NetworkManager
import com.zxwl.vclibrary.util.NetWorkUtils
import java.lang.reflect.InvocationTargetException
import java.util.*

class NetworkConnectChangedReceiver : BroadcastReceiver() {

    private var networkList: MutableMap<Any, List<MethodManager>> = HashMap()

    override fun onReceive(context: Context, intent: Intent) {
        try{
            //网络变化时会收到该广播
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                val netType = NetWorkUtils.getNetType()
                post(netType)
            }
        }catch (e:Exception){

        }
    }

    /**
     * 通知所有注册的方法，网络发生了改变
     *
     * @param netType
     */
    private fun post(netType: NetType) {
        val sets = networkList!!.keys
        for (observer in sets) {
            val methodList = networkList.get(observer)
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

    fun registerObserver(observer: Any) {
        var methodList: List<MethodManager>? = networkList.get(observer)
        if (methodList == null) {
            methodList = getAnnotationMethod(observer)
            networkList.put(observer, methodList)
        }
    }

    private fun getAnnotationMethod(observer: Any): List<MethodManager> {
        val methodList = ArrayList<MethodManager>()
        val methods = observer.javaClass.methods
        for (method in methods) {
            val network = method.getAnnotation(Network::class.java!!) ?: continue
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
        if (!networkList.isEmpty()) {
            networkList.remove(observer)
        }
    }

    fun unRegisterAllObserver() {
        if (!networkList.isEmpty()) {
            networkList.clear()
        }
        NetworkManager.unRegisterObserver(this)
    }
}

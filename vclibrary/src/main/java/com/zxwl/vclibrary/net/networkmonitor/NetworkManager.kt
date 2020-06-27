package com.hw.baselibrary.net.networkmonitor

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Build
import com.huawei.opensdk.commonservice.common.LocContext
import com.hw.baselibrary.net.networkmonitor.receiver.NetworkConnectChangedReceiver

/**
 *author：pc-20171125
 *data:2020/1/18 11:12
 */
object NetworkManager {
    private var receiver: NetworkConnectChangedReceiver?=null
    private var cmgr: ConnectivityManager? = null
    private var networkCallback: NetworkCallbackImpl? = null

    private fun NetworkManager() {
        receiver = NetworkConnectChangedReceiver()
    }


    fun init() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val filter = IntentFilter()
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            LocContext.getContext().registerReceiver(receiver, filter)
        } else {
            networkCallback = NetworkCallbackImpl()
            val request = NetworkRequest.Builder().build()
            cmgr =  LocContext.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cmgr != null) {
                cmgr!!.registerNetworkCallback(request, networkCallback)
            }
        }
    }


    fun getConnectivityManager(): ConnectivityManager? {
        return cmgr
    }

    fun registerObserver(`object`: Any) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            receiver!!.registerObserver(`object`)
        } else {
            networkCallback?.registerObserver(`object`)
        }
    }

    fun unRegisterObserver(`object`: Any) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            receiver!!.unRegisterObserver(`object`)
        } else {
            networkCallback!!.unRegisterObserver(`object`)
        }

    }

    fun unRegisterAllObserver() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            receiver!!.unRegisterAllObserver()
        } else {
            networkCallback!!.unRegisterAllObserver()
        }
    }

    private object SingletonHolder {
        private val instance = NetworkManager()
    }
}
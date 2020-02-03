package com.hw.baselibrary.utils

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.format.Formatter
import android.util.Log
import androidx.annotation.RequiresPermission
import com.hw.baselibrary.common.BaseApp
import com.hw.baselibrary.net.networkmonitor.NetType
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.net.UnknownHostException
import java.util.*

/**
 *author：pc-20171125
 *data:2019/11/7 16:39
 * 网络工具类
 */
object NetWorkUtils {

    private fun NetworkUtils() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    enum class NetworkType {
        NETWORK_ETHERNET,
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    /**
     * 打开网络设置
     *
     * Open the settings of wireless.
     *
     */
    fun openWirelessSettings() {
        BaseApp.context.startActivity(
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    /**
     * ping网络查看网络是否可用
     *
     * Return whether network is available using ping.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * The default ping ip: 223.5.5.5
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(INTERNET)
    fun isAvailableByPing(): Boolean {
        return isAvailableByPing(null)
    }

    /**
     * 查看网络是否连接
     *
     * Return whether network is connected.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: connected<br></br>`false`: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun isConnected(): Boolean {
        val info = getActiveNetworkInfo()
        return info != null && info.isConnected
    }

    /**
     * 使用ping返回网络是否可用。
     *
     * Return whether network is available using ping.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param ip The ip address.
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(INTERNET)
    fun isAvailableByPing(ip: String?): Boolean {
        var ip = ip
        if (ip == null || ip.length <= 0) {
            ip = "223.5.5.5"// default ping ip
        }
        val result = ShellUtils.execCmd(String.format("ping -c 1 %s", ip), false)
        val ret = result.result === 0
        if (result.errorMsg != null) {
            Log.d("NetworkUtils", "isAvailableByPing() called" + result.errorMsg)
        }
        if (result.successMsg != null) {
            Log.d("NetworkUtils", "isAvailableByPing() called" + result.successMsg)
        }
        return ret
    }


    @RequiresPermission(INTERNET)
    fun isAvailableByDns(ip: String) {

    }

    interface Callback {
        fun call(isSuccess: Boolean)
    }

    /**
     * 是否启用移动数据
     *
     * Return whether mobile data is enabled.
     *
     * @return `true`: enabled<br></br>`false`: disabled
     */
    fun getMobileDataEnabled(): Boolean {
        try {
            val tm = BaseApp.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                ?: return false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.isDataEnabled
            }
            @SuppressLint("PrivateApi")
            val getMobileDataEnabledMethod = tm.javaClass.getDeclaredMethod("getDataEnabled")
            if (null != getMobileDataEnabledMethod) {
                return getMobileDataEnabledMethod.invoke(tm) as Boolean
            }
        } catch (e: Exception) {
            Log.e("NetworkUtils", "getMobileDataEnabled: ", e)
        }

        return false
    }

    /**
     * 启用或禁用移动数据
     *
     * Enable or disable mobile data.
     *
     * Must hold `android:sharedUserId="android.uid.system"`,
     * `<uses-permission android:name="android.permission.MODIFY_PHONE_STATE" />`
     *
     * @param enabled True to enabled, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @RequiresPermission(MODIFY_PHONE_STATE)
    fun setMobileDataEnabled(enabled: Boolean): Boolean {
        try {
            val tm = BaseApp.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                ?: return false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tm.isDataEnabled = enabled
                return false
            }
            val setDataEnabledMethod =
                tm.javaClass.getDeclaredMethod("setDataEnabled", Boolean::class.javaPrimitiveType)
            if (null != setDataEnabledMethod) {
                setDataEnabledMethod.invoke(tm, enabled)
                return true
            }
        } catch (e: Exception) {
            Log.e("NetworkUtils", "setMobileDataEnabled: ", e)
        }

        return false
    }

    /**
     *返回是否使用移动数据。
     * Return whether using mobile data.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun isMobileData(): Boolean {
        val info = getActiveNetworkInfo()
        return (null != info
                && info.isAvailable
                && info.type == ConnectivityManager.TYPE_MOBILE)
    }

    /**
     * 返回是否使用4G
     * Return whether using 4G.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun is4G(): Boolean {
        val info = getActiveNetworkInfo()
        return (info != null
                && info.isAvailable
                && info.subtype == TelephonyManager.NETWORK_TYPE_LTE)
    }

    /**
     * 返回wifi是否已启用。
     * Return whether wifi is enabled.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`
     *
     * @return `true`: enabled<br></br>`false`: disabled
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    fun getWifiEnabled(): Boolean {
        @SuppressLint("WifiManagerLeak")
        val manager = BaseApp.context.getSystemService(WIFI_SERVICE) as WifiManager
            ?: return false
        return manager.isWifiEnabled
    }

    /**
     * 启用或禁用wifi。
     * Enable or disable wifi.
     *
     * Must hold `<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />`
     *
     * @param enabled True to enabled, false otherwise.
     */
    @RequiresPermission(CHANGE_WIFI_STATE)
    fun setWifiEnabled(enabled: Boolean) {
        @SuppressLint("WifiManagerLeak")
        val manager = BaseApp.context.getSystemService(WIFI_SERVICE) as WifiManager
            ?: return
        if (enabled == manager.isWifiEnabled) return
        manager.isWifiEnabled = enabled
    }

    /**
     * 返回wifi是否已连接。
     *
     * Return whether wifi is connected.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: connected<br></br>`false`: disconnected
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun isWifiConnected(): Boolean {
        val cm =
            BaseApp.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return false
        val ni = cm.activeNetworkInfo
        return ni != null && ni.type == ConnectivityManager.TYPE_WIFI
    }

    /**
     * 返回wifi是否可用。
     *
     * Return whether wifi is available.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />`,
     * `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @return `true`: available<br></br>`false`: unavailable
     */
    @SuppressLint("SupportAnnotationUsage")
    @RequiresPermission(allOf = arrayOf(ACCESS_WIFI_STATE, INTERNET))
    fun isWifiAvailable(): Boolean {
        return getWifiEnabled() && isAvailableByPing()
    }

    /**
    返回网络操作的名称。
     *
     * Return the name of network operate.
     *
     * @return the name of network operate
     */
    fun getNetworkOperatorName(): String {
        val tm = BaseApp.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            ?: return ""
        return tm.networkOperatorName
    }

    /**
     * Return type of network.
     *
     * Must hold `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return type of network
     *
     *  * [NetworkUtils.NetworkType.NETWORK_ETHERNET]
     *  * [NetworkUtils.NetworkType.NETWORK_WIFI]
     *  * [NetworkUtils.NetworkType.NETWORK_4G]
     *  * [NetworkUtils.NetworkType.NETWORK_3G]
     *  * [NetworkUtils.NetworkType.NETWORK_2G]
     *  * [NetworkUtils.NetworkType.NETWORK_UNKNOWN]
     *  * [NetworkUtils.NetworkType.NETWORK_NO]
     *
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    fun getNetworkType(): NetworkType {
        if (isEthernet()) {
            return NetworkType.NETWORK_ETHERNET
        }
        val info = getActiveNetworkInfo()
        if (info != null && info.isAvailable) {
            if (info.type == ConnectivityManager.TYPE_WIFI) {
                return NetworkType.NETWORK_WIFI
            } else if (info.type == ConnectivityManager.TYPE_MOBILE) {
                when (info.subtype) {
                    TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> return NetworkType.NETWORK_2G

                    TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> return NetworkType.NETWORK_3G

                    TelephonyManager.NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> return NetworkType.NETWORK_4G

                    else -> {
                        val subtypeName = info.subtypeName
                        if (subtypeName.equals("TD-SCDMA", ignoreCase = true)
                            || subtypeName.equals("WCDMA", ignoreCase = true)
                            || subtypeName.equals("CDMA2000", ignoreCase = true)
                        ) {
                            return NetworkType.NETWORK_3G
                        }
                    }
                }
            }
        }
        return NetworkType.NETWORK_UNKNOWN
    }

    /**
     * 返回是否使用以太网
     *
     * Return whether using ethernet.
     *
     * Must hold
     * `<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />`
     *
     * @return `true`: yes<br></br>`false`: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    private fun isEthernet(): Boolean {
        val cm =
            BaseApp.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return false
        val info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
        val state = info.state ?: return false
        return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    private fun getActiveNetworkInfo(): NetworkInfo? {
        val cm =
            BaseApp.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return null
        return cm.activeNetworkInfo
    }

    /**
     * 返回ip地址。
     * Return the ip address.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param useIPv4 True to use ipv4, false otherwise.
     * @return the ip address
     */
    @RequiresPermission(INTERNET)
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            val adds = LinkedList<InetAddress>()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp || ni.isLoopback) continue
                val addresses = ni.inetAddresses
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement())
                }
            }
            for (add in adds) {
                if (!add.isLoopbackAddress) {
                    val hostAddress = add.hostAddress
                    val isIPv4 = hostAddress.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return hostAddress
                    } else {
                        if (!isIPv4) {
                            val index = hostAddress.indexOf('%')
                            return if (index < 0)
                                hostAddress.toUpperCase()
                            else
                                hostAddress.substring(0, index).toUpperCase()
                        }
                    }
                }
            }
//        } catch (e: SocketException) {
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 返回广播的ip地址。
     *
     * Return the ip address of broadcast.
     *
     * @return the ip address of broadcast
     */
    fun getBroadcastIpAddress(): String {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            val adds = LinkedList<InetAddress>()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                if (!ni.isUp || ni.isLoopback) continue
                val ias = ni.interfaceAddresses
                var i = 0
                val size = ias.size
                while (i < size) {
                    val ia = ias[i]
                    val broadcast = ia.broadcast
                    if (broadcast != null) {
                        return broadcast.hostAddress
                    }
                    i++
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * 返回域地址。
     *
     * Return the domain address.
     *
     * Must hold `<uses-permission android:name="android.permission.INTERNET" />`
     *
     * @param domain The name of domain.
     * @return the domain address
     */
    @RequiresPermission(INTERNET)
    fun getDomainAddress(domain: String): String {
        val inetAddress: InetAddress
        try {
            inetAddress = InetAddress.getByName(domain)
            return inetAddress.hostAddress
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            return ""
        }

    }

    /**
     * 通过wifi返回ip地址。
     *
     * Return the ip address by wifi.
     *
     * @return the ip address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    fun getIpAddressByWifi(): String {
        @SuppressLint("WifiManagerLeak")
        val wm = BaseApp.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            ?: return ""
        return Formatter.formatIpAddress(wm.dhcpInfo.ipAddress)
    }

    /**
     * 通过wifi返回登机口
     *
     * Return the gate way by wifi.
     *
     * @return the gate way by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    fun getGatewayByWifi(): String {
        @SuppressLint("WifiManagerLeak")
        val wm = BaseApp.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            ?: return ""
        return Formatter.formatIpAddress(wm.dhcpInfo.gateway)
    }

    /**
     * 通过wifi返回网络掩码
     *
     * Return the net mask by wifi.
     *
     * @return the net mask by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    fun getNetMaskByWifi(): String {
        @SuppressLint("WifiManagerLeak")
        val wm = BaseApp.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            ?: return ""
        return Formatter.formatIpAddress(wm.dhcpInfo.netmask)
    }

    /**
     * 通过wifi返回服务器地址
     *
     * Return the server address by wifi.
     *
     * @return the server address by wifi
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    fun getServerAddressByWifi(): String {
        @SuppressLint("WifiManagerLeak")
        val wm = BaseApp.context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            ?: return ""
        return Formatter.formatIpAddress(wm.dhcpInfo.serverAddress)
    }


    /**
     * 获取当前的网络类型
     */
    fun getNetType(): NetType {
        val connMgr = BaseApp.context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            ?: return NetType.NONE
        //获取当前激活的网络连接状态
        val networkInfo = connMgr.activeNetworkInfo ?: return NetType.NONE

        val nType = networkInfo.type
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            return NetType.MOBILE
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            return NetType.WIFI
        }
        return NetType.NONE
    }
}
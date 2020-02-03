package com.hw.baselibrary.utils

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresPermission
import com.hw.baselibrary.common.BaseApp

/**
 *author：pc-20171125
 *data:2019/11/7 17:24
 */
object PhoneUtils {
    private fun PhoneUtils() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * 返回设备是否为电话。
     *
     * Return whether the device is phone.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isPhone(): Boolean {
        val tm = getTelephonyManager()
        return tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
    }

    /**
     * 返回唯一的设备id。
     *
     * Return the unique device id.
     *
     * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return the unique device id
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getDeviceId(): String {
        val tm = getTelephonyManager()
        val deviceId = tm.deviceId
        if (!TextUtils.isEmpty(deviceId)) return deviceId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val imei = tm.imei
            if (!TextUtils.isEmpty(imei)) return imei
            val meid = tm.meid
            return if (TextUtils.isEmpty(meid)) "" else meid
        }
        return ""
    }

    /**
     * 返回设备序列号。
     *
     * Return the serial of device.
     *
     * @return the serial of device
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getSerial(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial() else Build.SERIAL
    }

    /**
     * 返回IMEI
     * Return the IMEI.
     *
     * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return the IMEI
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getIMEI(): String {
        val tm = getTelephonyManager()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm.imei
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                val clazz = tm.javaClass
                val getImeiMethod = clazz.getDeclaredMethod("getImei")
                getImeiMethod.isAccessible = true
                val imei = getImeiMethod.invoke(tm) as String
                if (imei != null) return imei
            } catch (e: Exception) {
                Log.e("PhoneUtils", "getIMEI: ", e)
            }

        }
        val imei = tm.deviceId
        return if (imei != null && imei.length == 15) {
            imei
        } else ""
    }

    /**
     * Return the IMEI.
     *
     * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @param slotId of which deviceID is returned
     * @return the IMEI
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getIMEI(slotId: Int): String {
        val tm = getTelephonyManager()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return tm.getImei(slotId)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                val clazz = tm.javaClass

                val getImeiMethod = clazz.getDeclaredMethod("getImei", Int::class.javaPrimitiveType)
                getImeiMethod.isAccessible = true
                val imei = getImeiMethod.invoke(tm, slotId) as String
                if (imei != null) return imei
            } catch (e: Exception) {
                Log.e("PhoneUtils", "getIMEI: ", e)
            }

        }
        return getIMEI()
    }

    /**
     * Return the MEID.
     *
     * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return the MEID
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getMEID(): String {
        val tm = getTelephonyManager()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tm.meid
        } else tm.deviceId
    }

    /**
     * Return the MEID.
     *
     * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return the MEID
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getMEID(slotId: Int): String {
        val tm = getTelephonyManager()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tm.getMeid(slotId)
        } else getMEID()
    }

    /**
     * Return the IMSI.
     *
     * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return the IMSI
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getIMSI(): String {
        val tm = getTelephonyManager()
        return tm.subscriberId
    }

    /**
     * 返回当前电话类型
     *
     * Returns the current phone type.
     *
     * @return the current phone type
     *
     *  * [TelephonyManager.PHONE_TYPE_NONE]
     *  * [TelephonyManager.PHONE_TYPE_GSM]
     *  * [TelephonyManager.PHONE_TYPE_CDMA]
     *  * [TelephonyManager.PHONE_TYPE_SIP]
     *
     */
    fun getPhoneType(): Int {
        val tm = getTelephonyManager()
        return tm.phoneType
    }

    /**
     * 返回sim卡状态是否就绪。
     *
     * Return whether sim card state is ready.
     *
     * @return `true`: yes<br></br>`false`: no
     */
    fun isSimCardReady(): Boolean {
        val tm = getTelephonyManager()
        return tm.simState == TelephonyManager.SIM_STATE_READY
    }

    /**
     * 返回sim卡操作员姓名
     *
     * Return the sim operator name.
     *
     * @return the sim operator name
     */
    fun getSimOperatorName(): String {
        val tm = getTelephonyManager()
        return tm.simOperatorName
    }

    /**
     * 使用mnc返回sim操作员。
     *
     * Return the sim operator using mnc.
     *
     * @return the sim operator
     */
    fun getSimOperatorByMnc(): String {
        val tm = getTelephonyManager()
        val operator = tm.simOperator ?: return ""
        when (operator) {
            "46000", "46002", "46007", "46020" -> return "中国移动"
            "46001", "46006", "46009" -> return "中国联通"
            "46003", "46005", "46011" -> return "中国电信"
            else -> return operator
        }
    }

    /**
     * 返回电话状态。
     *
     * Return the phone status.
     *
     * Must hold `<uses-permission android:name="android.permission.READ_PHONE_STATE" />`
     *
     * @return DeviceId = 99000311726612<br></br>
     * DeviceSoftwareVersion = 00<br></br>
     * Line1Number =<br></br>
     * NetworkCountryIso = cn<br></br>
     * NetworkOperator = 46003<br></br>
     * NetworkOperatorName = 中国电信<br></br>
     * NetworkType = 6<br></br>
     * PhoneType = 2<br></br>
     * SimCountryIso = cn<br></br>
     * SimOperator = 46003<br></br>
     * SimOperatorName = 中国电信<br></br>
     * SimSerialNumber = 89860315045710604022<br></br>
     * SimState = 5<br></br>
     * SubscriberId(IMSI) = 460030419724900<br></br>
     * VoiceMailNumber = *86<br></br>
     */
    @SuppressLint("HardwareIds")
    @RequiresPermission(READ_PHONE_STATE)
    fun getPhoneStatus(): String {
        val tm = getTelephonyManager()
        var str = ""

        str += "DeviceId(IMEI) = " + tm.deviceId + "\n"
        str += "DeviceSoftwareVersion = " + tm.deviceSoftwareVersion + "\n"
        str += "Line1Number = " + tm.line1Number + "\n"
        str += "NetworkCountryIso = " + tm.networkCountryIso + "\n"
        str += "NetworkOperator = " + tm.networkOperator + "\n"
        str += "NetworkOperatorName = " + tm.networkOperatorName + "\n"
        str += "NetworkType = " + tm.networkType + "\n"
        str += "PhoneType = " + tm.phoneType + "\n"
        str += "SimCountryIso = " + tm.simCountryIso + "\n"
        str += "SimOperator = " + tm.simOperator + "\n"
        str += "SimOperatorName = " + tm.simOperatorName + "\n"
        str += "SimSerialNumber = " + tm.simSerialNumber + "\n"
        str += "SimState = " + tm.simState + "\n"
        str += "SubscriberId(IMSI) = " + tm.subscriberId + "\n"
        str += "VoiceMailNumber = " + tm.voiceMailNumber
        return str
    }

    /**
     * Skip to dial.
     *
     * @param phoneNumber The phone number.
     * @return `true`: operate successfully<br></br>`false`: otherwise
     */
    fun dial(phoneNumber: String): Boolean {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        if (isIntentAvailable(intent)) {
            BaseApp.context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return true
        }
        return false
    }

    /**
     * 打电话
     *
     * Make a phone call.
     *
     * Must hold `<uses-permission android:name="android.permission.CALL_PHONE" />`
     *
     * @param phoneNumber The phone number.
     * @return `true`: operate successfully<br></br>`false`: otherwise
     */
    @RequiresPermission(CALL_PHONE)
    fun call(phoneNumber: String): Boolean {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
        if (isIntentAvailable(intent)) {
            BaseApp.context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return true
        }
        return false
    }

    /**
     * 发送短信
     *
     * Send sms.
     *
     * @param phoneNumber The phone number.
     * @param content     The content.
     * @return `true`: operate successfully<br></br>`false`: otherwise
     */
    fun sendSms(phoneNumber: String, content: String): Boolean {
        val uri = Uri.parse("smsto:$phoneNumber")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        if (isIntentAvailable(intent)) {
            intent.putExtra("sms_body", content)
            BaseApp.context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return true
        }
        return false
    }

    /**
     * Send sms silently.
     *
     * Must hold `<uses-permission android:name="android.permission.SEND_SMS" />`
     *
     * @param phoneNumber The phone number.
     * @param content     The content.
     */
    @RequiresPermission(SEND_SMS)
    fun sendSmsSilent(phoneNumber: String, content: String) {
        if (TextUtils.isEmpty(content)) return
        val sentIntent = PendingIntent.getBroadcast(BaseApp.context, 0, Intent("send"), 0)
        val smsManager = SmsManager.getDefault()
        if (content.length >= 70) {
            val ms = smsManager.divideMessage(content)
            for (str in ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null)
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null)
        }
    }

    private fun getTelephonyManager(): TelephonyManager {
        return BaseApp.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    private fun isIntentAvailable(intent: Intent): Boolean {
        return BaseApp.context
            .getPackageManager()
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            .size> 0
    }
}
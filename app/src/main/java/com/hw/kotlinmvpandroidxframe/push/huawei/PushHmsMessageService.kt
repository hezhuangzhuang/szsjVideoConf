package com.hw.kotlinmvpandroidxframe.push.huawei

import com.huawei.hms.push.HmsMessageService
import com.huawei.hms.push.RemoteMessage
import com.hw.baselibrary.net.RetrofitManager
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.kotlinmvpandroidxframe.net.api.PushApi
import com.hw.provider.user.UserContants

class PushHmsMessageService : HmsMessageService() {

    val TAG = "PushHmsMessageService"

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        LogUtils.i(TAG, "onNewToken-->" + token!!)
        saveHuaweiPushToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        LogUtils.i(TAG, "onMessageReceived-->" + remoteMessage!!.toString())
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()

        LogUtils.i(TAG, "onDeletedMessages-->")
    }

    override fun onMessageSent(s: String?) {
        super.onMessageSent(s)

        LogUtils.i(TAG, "onMessageSent-->" + s!!)
    }

    /**
     * 保存华为的推送token
     */
    private fun saveHuaweiPushToken(token: String?) {
        try{
            RetrofitManager
                .create(PushApi::class.java, Urls.FILE_URL)
                .saveHuaweiToken(SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT),token!!)
                .compose(CustomCompose())
                .subscribe({
                    LogUtils.i(TAG, "saveHuaweiPushToken-->onSuccess-->" + it.toString())
                },{
                    LogUtils.i(TAG, "saveHuaweiPushToken-->onError-->" + it.toString())
                })
        }catch (e:Exception){

        }
    }
}

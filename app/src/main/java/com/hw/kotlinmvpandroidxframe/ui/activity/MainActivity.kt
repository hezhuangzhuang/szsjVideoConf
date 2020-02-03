package com.hw.kotlinmvpandroidxframe.ui.activity

import android.annotation.SuppressLint
import android.location.Address
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.flyco.tablayout.bean.TabEntity
import com.flyco.tablayout.listener.CustomTabEntity
import com.huawei.hms.aaid.HmsInstanceId
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.net.networkmonitor.NetType
import com.hw.baselibrary.net.networkmonitor.Network
import com.hw.baselibrary.net.networkmonitor.NetworkManager
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.PermissionUtils
import com.hw.baselibrary.utils.rom.SystemUtil
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.kotlinmvpandroidxframe.R
import com.hw.kotlinmvpandroidxframe.push.PushConstant
import com.hw.provider.chat.bean.ConstactsBean
import com.hw.provider.chat.utils.GreenDaoUtil
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.constacts.impl.ContactsModuleRouteService
import com.hw.provider.user.UserContants
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.OnReverseGeocodingListener
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.geofencing.model.GeofenceModel
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.Executors

@Route(path = RouterPath.Main.PATH_MAIN)
class MainActivity : BaseActivity() {
    var TAG = MainActivity::javaClass.name

    //是否有设置权限
    val setPermission by lazy {
        SPStaticUtils.getBoolean(UserContants.SETTING_PERMISSION)
    }

    override fun setListeners() {

    }

    override fun onError(text: String) {
    }

    override fun initData(bundle: Bundle?) {
        //初始化网络监听
        NetworkManager.init()
        NetworkManager.registerObserver(this)
    }


    @Network(netType = NetType.AUTO)
    fun onNetChanged(netType: NetType) {
        when (netType) {
            NetType.WIFI -> {
                LogUtils.e(TAG, "AUTO监控：WIFI CONNECT")
            }

            NetType.MOBILE -> {
                LogUtils.e(TAG, "AUTO监控：MOBILE CONNECT")
            }

            NetType.AUTO -> {
                LogUtils.e(TAG, "AUTO监控：AUTO CONNECT")
            }

            NetType.NONE -> {
                LogUtils.e(TAG, "AUTO监控：NONE CONNECT")
            }

            else -> {
            }
        }
    }

    @Network(netType = NetType.WIFI)
    fun onWifiChanged(netType: NetType) {
        when (netType) {
            NetType.WIFI -> {
                LogUtils.e(TAG, "wifi监控：WIFI CONNECT")
                EventBusUtils.sendMessage(EventMsg.NET_WORK_CONNECT, "")
            }

            NetType.NONE -> {
                LogUtils.e(TAG, "wifi监控：NONE CONNECT")
                EventBusUtils.sendMessage(EventMsg.NET_WORK_DISCONNECT, "")
            }
        }
    }

    @Network(netType = NetType.MOBILE)
    fun onMobileChanged(netType: NetType) {
        when (netType) {
            NetType.MOBILE -> {
                LogUtils.e(TAG, "Mobile监控：MOBILE CONNECT")
                EventBusUtils.sendMessage(EventMsg.NET_WORK_CONNECT, "")
            }

            NetType.NONE -> {
                LogUtils.e(TAG, "Mobile监控：NONE CONNECT")
                EventBusUtils.sendMessage(EventMsg.NET_WORK_DISCONNECT, "")
            }
        }
    }

    override fun bindLayout(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?, contentView: View) {

        EventBus.getDefault().register(this)
    }

    override fun doBusiness() {
        if (!setPermission) {
            //判断是否是小米并且似乎否有后台弹出权限
            if (SystemUtil.isMIUI()) {
                showBackroundDialog("后台弹出权限,锁屏显示权限")
            }//判断其他型号手机是否有悬浮窗权限
            else {
                showBackroundDialog("悬浮窗权限,锁屏显示权限")
            }
        }

        //申请白名单权限
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//            //判断是否在白名单
//            if (!SystemUtil.isIgnoringBatteryOptimizations()) {
//                SystemUtil.requestIgnoreBatteryOptimizations()
//            }
//        }
    }

    /**
     * 显示是否有后台弹出权限
     */
    private fun showBackroundDialog(permissionName: String) {
        MaterialDialog.Builder(this)
            .title("提示")
            .content("为了更好的体验应用,请开启$permissionName")
            .positiveText("去设置")
            .negativeText("取消")
            .onPositive { dialog, which ->
                SPStaticUtils.put(UserContants.SETTING_PERMISSION, true)
                if ("后台弹出权限" == permissionName) {
                    PermissionUtils.launchAppPermissionSettings()
                } else {
                    PermissionUtils.launchAppPermissionSettings()
                }
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()

        EventBus.getDefault().unregister(this)
    }


    /**
     * 主线程中处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun mainEvent(messageEvent: EventMsg<Any>) {
        when (messageEvent.message) {
            //更新消息提醒
            EventMsg.UPDATE_MAIN_NOTIF -> {
            }
        }
    }

    private fun showLogOutDialog() {
        MaterialDialog.Builder(mActivity)
            .title("提示")
            .content("是否退出登录?")
            .positiveText("确定")
            .negativeText("取消")
            .onPositive(object : MaterialDialog.SingleButtonCallback {
                override fun onClick(@NonNull dialog: MaterialDialog, @NonNull which: DialogAction) {
                    logOut()
                }
            })
            .show()
    }

    private fun logOut() {

    }


    /**
     * 清空用户信息
     */
    private fun clearUserInfo() {
        //是否登录
        SPStaticUtils.put(UserContants.HAS_LOGIN, false)

        //显示的名称
        SPStaticUtils.put(UserContants.DISPLAY_NAME, "")

        //用户id
        SPStaticUtils.put(UserContants.USER_ID, "")

        //登录的用户名密码
//        SPStaticUtils.put(UserContants.USER_NAME, userInfo.data.accountName)
        SPStaticUtils.put(UserContants.PASS_WORD, "")

        //华为登录密码
        SPStaticUtils.put(UserContants.HUAWEI_ACCOUNT, "")
        SPStaticUtils.put(UserContants.HUAWEI_PWD, "")

        //华为登录地址
        SPStaticUtils.put(UserContants.HUAWEI_SMC_IP, "")
        SPStaticUtils.put(UserContants.HUAWEI_SMC_PORT, "")
    }

    override fun onBackPressed() {
        showLogOutDialog()
    }

    /**
     * 保存数据状态
     *
     * @param outState
     */
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //        super.onSaveInstanceState(outState);
    }

}

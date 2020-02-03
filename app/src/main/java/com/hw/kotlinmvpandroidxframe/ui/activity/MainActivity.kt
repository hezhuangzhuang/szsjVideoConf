package com.hw.kotlinmvpandroidxframe.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.net.networkmonitor.NetType
import com.hw.baselibrary.net.networkmonitor.Network
import com.hw.baselibrary.net.networkmonitor.NetworkManager
import com.hw.baselibrary.ui.activity.BaseMvpActivity
import com.hw.baselibrary.utils.DynamicTimeFormat
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.PermissionUtils
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.rom.SystemUtil
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.huaweivclib.inter.HuaweiCallImp
import com.hw.kotlinmvpandroidxframe.BuildConfig
import com.hw.kotlinmvpandroidxframe.R
import com.hw.kotlinmvpandroidxframe.injection.component.DaggerMainComponent
import com.hw.kotlinmvpandroidxframe.injection.module.MainModule
import com.hw.kotlinmvpandroidxframe.mvp.contract.MainContract
import com.hw.kotlinmvpandroidxframe.mvp.presenter.ConfPresenter
import com.hw.kotlinmvpandroidxframe.ui.adapter.ConfAdapter
import com.hw.provider.eventbus.EventBusUtils
import com.hw.provider.eventbus.EventMsg
import com.hw.provider.huawei.commonservice.localbroadcast.CustomBroadcastConstants
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcast
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcastReceiver
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import com.hw.provider.user.UserContants
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = RouterPath.Main.PATH_MAIN)
class MainActivity : BaseMvpActivity<ConfPresenter>(), MainContract.View {
    override fun authenticationSuccess() {
        //开始登录
        HuaweiModuleService.login(siteUri, passWord, smcUrl, smcPort)
    }

    override fun authenticationFail() {
        ToastHelper.showShort("登录失败")
    }

    lateinit var confAdapter: ConfAdapter

    //TYPE_CREATE_CONF:创建会议，JOIN_CONF:加入会议
    var type: Int = 0

    lateinit var siteUri: String

    lateinit var displayName: String

    lateinit var passWord: String

    lateinit var smcUrl: String

    lateinit var smcPort: String

    //type为CREATE_CONF时获取会议名称，时长，参会列表
    lateinit var confName: String
    lateinit var duration: String
    lateinit var sites: String

    //type为JOIN_CONF时获取smc会议id
    lateinit var smcConfId: String

    //鉴权信息
    lateinit var appPackageName: String
    lateinit var secretKey: String

    override fun initComponent() {
        DaggerMainComponent.builder().activityComponent(mActivityComponent)
            .mainModule(MainModule())
            .build()
            .inject(this)

        mPresenter.mRootView = this
    }

    /**
     * 查询会议成功
     */
    override fun queryConfSuccess(baseData: String) {

    }

    /**
     * 查询会议失败
     */
    override fun queryConfFail(errorMsg: String) {

    }

    var TAG = MainActivity::javaClass.name

    //请求页码
    private var pageNum = 0

    //是否有设置权限
    val setPermission by lazy {
        SPStaticUtils.getBoolean(UserContants.SETTING_PERMISSION)
    }

    override fun setListeners() {
        titleBar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(v: View?) {
                finish()
            }

            override fun onRightClick(v: View?) {
            }

            override fun onTitleClick(v: View?) {
            }
        })
    }

    override fun onError(text: String) {
    }

    override fun initData(bundle: Bundle?) {
        //初始化华为
        HuaweiModuleService.initHuawei(application, BuildConfig.APPLICATION_ID)

        //初始化网络监听
        NetworkManager.init()
        NetworkManager.registerObserver(this)

        //注册登录广播
        LocBroadcast.getInstance().registerBroadcast(loginReceiver, loginActions)

        //获取鉴权信息
        appPackageName = intent.getStringExtra(RouterPath.Huawei.FILED_APP_PACKAGE_NAME)
        secretKey = intent.getStringExtra(RouterPath.Huawei.FILED_SECRET_KEY)

        type = intent.getIntExtra(RouterPath.Huawei.FILED_TYPE, RouterPath.Huawei.TYPE_CREATE_CONF)
        siteUri = intent.getStringExtra(RouterPath.Huawei.FILED_USER_NAME)
        displayName = intent.getStringExtra(RouterPath.Huawei.FILED_DISPLAY_NAME)
        passWord = intent.getStringExtra(RouterPath.Huawei.FILED_PASS_WORD)
        smcUrl = intent.getStringExtra(RouterPath.Huawei.FILED_HUAWEI_SMC_URL)
        smcPort = intent.getStringExtra(RouterPath.Huawei.FILED_HUAWEI_SMC_PORT)
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

        mPresenter.getConfList(siteUri)


        initRefreshLayout()
        initAdapter()
    }

    override fun doBusiness() {
        if (!setPermission) {
            //判断是否是小米并且似乎否有后台弹出权限
            if (SystemUtil.isMIUI()) {
                showBackroundRuningDialog("后台弹出权限,锁屏显示权限")
            }//判断其他型号手机是否有悬浮窗权限
            else {
                showBackroundRuningDialog("悬浮窗权限,锁屏显示权限")
            }
        }

        //开始鉴权
        mPresenter.authentication(appPackageName, secretKey)
    }

    /**
     * 显示是否有后台弹出权限
     */
    private fun showBackroundRuningDialog(permissionName: String) {
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

        //取消注册登录广播
        LocBroadcast.getInstance().unRegisterBroadcast(loginReceiver, loginActions)

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
        //super.onSaveInstanceState(outState);
    }

    /**
     * 初始化刷新控件
     */
    private fun initRefreshLayout() {
        //设置头部
        refreshLayout.setRefreshHeader(
            ClassicsHeader(this).setSpinnerStyle(SpinnerStyle.Translate)
                .setTimeFormat(DynamicTimeFormat("更新于 %s"))
                .setAccentColor(ContextCompat.getColor(this, R.color.color_999))
        )
        //设置尾部
        refreshLayout.setRefreshFooter(ClassicsFooter(this).setSpinnerStyle(SpinnerStyle.Translate))

        refreshLayout.setOnRefreshListener {
            mPresenter.getConfList(siteUri)
        }

        //是否启用刷新
        refreshLayout.setEnableRefresh(true)
        refreshLayout.setEnableLoadMore(false)

        //设置是否在没有更多数据之后 Footer 跟随内容
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true)
    }

    /**
     * 初始化适配器
     */
    private fun initAdapter() {
        confAdapter = ConfAdapter(R.layout.item_conf, ArrayList<String>())
        confAdapter.setOnItemChildClickListener { adapter, view, position ->
            var dateBean = confAdapter.getItem(position)
            ToastHelper.showShort(dateBean!!)
//            HuaweiModuleService.callSite(dateBean?.accessCode!!)
        }
        rvList.adapter = confAdapter
        rvList.layoutManager = LinearLayoutManager(this)
    }


    /*华为登录相关start*/
    val loginActions = arrayOf<String>(
        CustomBroadcastConstants.LOGIN_SUCCESS,
        CustomBroadcastConstants.LOGIN_FAILED,
        CustomBroadcastConstants.LOGOUT
    )

    private val loginReceiver = object : LocBroadcastReceiver {
        override fun onReceive(broadcastName: String?, obj: Any?) {
            Log.i(Companion.TAG, "loginReceiver-->$broadcastName")
            when (broadcastName) {
                CustomBroadcastConstants.LOGIN_SUCCESS -> {
                    dismissLoading()

                    handlerHuaweiLoginSuccess()
                }

                CustomBroadcastConstants.LOGIN_FAILED -> {
                    dismissLoading()

                    handlerHuaweiLoginFail()
                }

                CustomBroadcastConstants.LOGOUT -> {
//                    ToastHelper.showShort("登出")
                }

                else -> {
                }
            }
        }
    }

    /**
     * 登录华为成功
     */
    private fun handlerHuaweiLoginSuccess() {
        //控制类型
        when (type) {
            //创建会议
            RouterPath.Huawei.TYPE_CREATE_CONF -> {
                confName = intent.getStringExtra(RouterPath.Huawei.FILED_CONF_NAME)
                duration = intent.getStringExtra(RouterPath.Huawei.FILED_DURATION)
                sites = intent.getStringExtra(RouterPath.Huawei.FILED_SITES)

                //自动添加召集者本身
                if (!sites.contains(siteUri)) {
                    sites += "$siteUri,"
                }

                //创建视频会议
                HuaweiModuleService.createConfNetWork(
                    confName, duration, "", sites, "", 1
                )
            }

            //加入会议
            RouterPath.Huawei.TYPE_JOIN_CONF -> {
                smcConfId = intent.getStringExtra(RouterPath.Huawei.FILED_SMC_CONF_ID)

                //加入会议
                HuaweiModuleService.joinConfNetWork(smcConfId, siteUri)
            }
        }
    }

    /**
     * 登录华为失败
     */
    private fun handlerHuaweiLoginFail() {

    }
}

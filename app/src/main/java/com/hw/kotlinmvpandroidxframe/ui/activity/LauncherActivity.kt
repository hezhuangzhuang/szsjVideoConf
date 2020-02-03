package com.hw.kotlinmvpandroidxframe.ui.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.hw.baselibrary.common.BaseApp
import com.hw.baselibrary.constant.PermissionConstants
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.baselibrary.utils.LogUtils
import com.hw.baselibrary.utils.PermissionUtils
import com.hw.baselibrary.utils.PhoneUtils
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils
import com.hw.huaweivclib.inter.HuaweiInitImp
import com.hw.kotlinmvpandroidxframe.BuildConfig
import com.hw.kotlinmvpandroidxframe.R
import com.hw.provider.huawei.commonservice.localbroadcast.CustomBroadcastConstants
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcast
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcastReceiver
import com.hw.provider.router.RouterPath
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService
import com.hw.provider.router.provider.user.impl.UserModuleRouteService
import com.hw.provider.user.UserContants
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity : BaseActivity() {

    override fun initData(bundle: Bundle?) {
        //是否是正式环境
        var isFormalUrl = SPStaticUtils.getBoolean(UserContants.FORMAL_URL, true)

        //正式环境
        if (isFormalUrl) {
            Urls.FILE_URL = Urls.FILE_FORMAL
            Urls.WEBSOCKET_URL = Urls.WEBSOCKET_FORMAL
        } else {//测试环境
            Urls.FILE_URL = Urls.FILE_TEST
            Urls.WEBSOCKET_URL = Urls.WEBSOCKET_TEST
        }
    }

    override fun bindLayout(): Int = R.layout.activity_launcher

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
    }

    override fun doBusiness() {
        startAnimation()
    }

    private fun checkPermission() {
        PermissionUtils.permission(
            PermissionConstants.STORAGE,
            PermissionConstants.CAMERA,
            PermissionConstants.MICROPHONE
        )
            //.rationale { shouldRequest -> DialogHelper.showRationaleDialog(shouldRequest) }
            .callback(object : PermissionUtils.FullCallback {
                override fun onGranted(permissionsGranted: List<String>) {
                    //注册广播
                    LocBroadcast.getInstance().registerBroadcast(loginReceiver, mActions)

                    //初始化华为
                    HuaweiInitImp.initHuawei(BaseApp.context, BuildConfig.APPLICATION_ID)

                    //是否已登录
                    val hasLogin = SPStaticUtils.getBoolean(UserContants.HAS_LOGIN)

                    val userName = SPStaticUtils.getString(UserContants.USER_NAME)
                    val pwd = SPStaticUtils.getString(UserContants.PASS_WORD)

                    if (hasLogin) {
                        val login =
                            UserModuleRouteService.login(userName, pwd, PhoneUtils.getDeviceId())
                        login.subscribe({ baseDate ->
                            if (NetWorkContants.RESPONSE_CODE == baseDate.responseCode) {
                                //开始登录华为
                                baseDate.data.apply {
                                    HuaweiModuleService.login(
                                        sipAccount,
                                        sipPassword,
                                        scIp,
                                        scPort
                                    )
                                }
                            } else {
                                startLoginActivity()
                            }
                        }, {
                            startLoginActivity()
                        })
                    } else {
                        //跳转到登录界面
                        startLoginActivity()
                    }
                }

                override fun onDenied(
                    permissionsDeniedForever: List<String>,
                    permissionsDenied: List<String>
                ) {
                    LogUtils.d(permissionsDeniedForever, permissionsDenied)
                    if (!permissionsDeniedForever.isEmpty()) {
                        return
                    }
                    finish()
                }
            })
            .request()
    }

    private fun startLoginActivity() {
        Handler().postDelayed(Runnable {
            ARouter.getInstance()
                .build(RouterPath.UserCenter.PATH_LOGIN)
                .navigation()
            finish()
        }, 1000)
    }

    override fun setListeners() {}

    override fun onError(text: String) {}

    override fun onDestroy() {
        super.onDestroy()

        //取消注册广播
        LocBroadcast.getInstance().unRegisterBroadcast(loginReceiver, mActions)
    }

    /*华为登录相关start*/
    var mActions = arrayOf<String>(
        CustomBroadcastConstants.LOGIN_SUCCESS,
        CustomBroadcastConstants.LOGIN_FAILED,
        CustomBroadcastConstants.LOGOUT
    )

    private val loginReceiver = object : LocBroadcastReceiver {
        override fun onReceive(broadcastName: String?, obj: Any?) {
            Log.i(TAG, "loginReceiver-->$broadcastName")
            when (broadcastName) {
                CustomBroadcastConstants.LOGIN_SUCCESS -> {
                    dismissLoading()

                    ToastHelper.showShort("登录成功")

                    ARouter.getInstance()
                        .build(RouterPath.Main.PATH_MAIN)
                        .navigation(application, object : NavigationCallback {
                            override fun onLost(postcard: Postcard?) {
                            }

                            override fun onFound(postcard: Postcard?) {
                            }

                            override fun onInterrupt(postcard: Postcard?) {
                            }

                            override fun onArrival(postcard: Postcard?) {
                                finish()
                            }
                        })
                }

                CustomBroadcastConstants.LOGIN_FAILED -> {
                    ToastHelper.showShort("登录华为失败")
                    ARouter.getInstance()
                        .build(RouterPath.UserCenter.PATH_LOGIN)
                        .navigation(application, object : NavigationCallback {
                            override fun onLost(postcard: Postcard?) {
                            }

                            override fun onFound(postcard: Postcard?) {
                            }

                            override fun onInterrupt(postcard: Postcard?) {
                            }

                            override fun onArrival(postcard: Postcard?) {
                                finish()
                            }
                        })
                }

                CustomBroadcastConstants.LOGOUT -> {
                    ToastHelper.showShort("登出")
                }

                else -> {
                }
            }
        }
    }

    private var alphaAnimation: AlphaAnimation? = null
    private var scaleAnimation: ScaleAnimation? = null

    private fun startAnimation() {
        val animationSet = AnimationSet(true)

        scaleAnimation =
            ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, 1, 0.5f)
        scaleAnimation!!.duration = 1000

        alphaAnimation = AlphaAnimation(0.2f, 1.0f)
        alphaAnimation!!.duration = 1000

        animationSet.addAnimation(scaleAnimation)
        animationSet.addAnimation(alphaAnimation)

        icImg.startAnimation(animationSet)

        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                checkPermission()
            }
        })
    }
}

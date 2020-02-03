package com.hw.provider.router.provider.user.impl

import com.alibaba.android.arouter.launcher.ARouter
import com.hw.baselibrary.utils.LogUtils
import com.hw.provider.net.respone.user.LoginBean
import com.hw.provider.router.provider.user.IUserModuleService
import io.reactivex.Observable

/**
 *author：pc-20171125
 *data:2020/1/17 15:53
 */
object UserModuleRouteService{

    /**
     * 登录的接口
     */
    fun login(name: String, pwd: String, deviceID: String): Observable<LoginBean> {
        val navigation = ARouter.getInstance().navigation(IUserModuleService::class.java)
        LogUtils.i("UserModuleRouteService-->"+navigation)
        return navigation.login(name, pwd, deviceID)
    }
}
package com.hw.provider.router.provider.user

import com.alibaba.android.arouter.facade.template.IProvider
import com.hw.provider.net.respone.user.LoginBean
import io.reactivex.Observable

/**
 *author：pc-20171125
 *data:2020/1/17 15:50
 * 登录的接口
 */
interface IUserModuleService : IProvider {

    /**
     * 登录的接口
     */
    fun login(name: String, pwd: String, deviceID: String): Observable<LoginBean>
}
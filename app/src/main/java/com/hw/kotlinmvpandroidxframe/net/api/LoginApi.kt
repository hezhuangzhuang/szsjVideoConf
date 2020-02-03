package com.hw.kotlinmvpandroidxframe.net.api

import com.hw.kotlinmvpandroidxframe.net.bean.LoginBean
import io.reactivex.Observable
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *author：pc-20171125
 *data:2019/11/8 14:01
 */
interface LoginApi {
    /**
     * 登录
     * accountAction_login.action
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("operatorAction_loginApp.action")
    fun login(
        @Query("account") account: String,
        @Query("password") password: String,
        @Query("deviceID") deviceID: String
    ): Observable<LoginBean>
}
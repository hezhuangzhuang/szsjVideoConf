package com.hw.kotlinmvpandroidxframe.net.api

import com.hw.huaweivclib.net.respone.BaseData
import io.reactivex.Observable
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ConfApi {

    /**
     * 查询会议列表
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("api/conference?")
    fun queryConfList(
        @Query("siteUri") siteUri: String
    ): Observable<BaseData<String>>


    /**
     * 开始鉴权
     *
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("api/app/authentication")
    fun authentication(
        @Query("appPackageName") appPackageName: String,
        @Query("secretKey") secretKey: String
    ): Observable<BaseData<String>>
}
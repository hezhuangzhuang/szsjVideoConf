package com.hw.kotlinmvpandroidxframe.net.api

import io.reactivex.Observable
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface PushApi {
    /*
     *保存华为推送的token
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("site/confAction_putUriAndToken.action")
     fun saveHuaweiToken(
        @Query("uri") siteUri: String,
        @Query("token") token: String
    ): Observable<String>
}
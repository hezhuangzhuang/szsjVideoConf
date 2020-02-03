package com.hw.kotlinmvpandroidxframe.mvp.model

import com.hw.baselibrary.net.RetrofitManager
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.hw.huaweivclib.net.respone.BaseData
import com.hw.kotlinmvpandroidxframe.net.api.ConfApi
import io.reactivex.Observable
import retrofit2.http.Query
import javax.inject.Inject

/**
 * 会议的服务
 */
class ConfService @Inject constructor() {

    /**
     * 查询会议详情
     */
    fun queryConfList(
        siteUri: String
    ): Observable<BaseData<String>> {
        return RetrofitManager.create(ConfApi::class.java, Urls.BASE_URL)
            .queryConfList(siteUri)
            .compose(CustomCompose())
    }

    /**
     * 鉴权
     */
    fun authentication(
        appPackageName: String,
        secretKey: String
    ): Observable<BaseData<String>> {
        return RetrofitManager.create(ConfApi::class.java, Urls.BASE_URL)
            .authentication(appPackageName, secretKey)
            .compose(CustomCompose())
    }


}
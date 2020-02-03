package com.hw.kotlinmvpandroidxframe.net.api

import com.hw.baselibrary.common.BaseData
import com.hw.kotlinmvpandroidxframe.net.bean.NewBean
import io.reactivex.Observable
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *author：pc-20171125
 *data:2019/11/11 16:31
 */
interface StudyApi {
    /**
     * 登录
     * accountAction_login.action
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("studyNewsAction_queryHotStudyList.action")
    fun getStudyList(): Observable<BaseData<NewBean>>

    /**
     * 获取理论学习接口
     * studyEduAction_queryListApp
     * columnId     推荐：1，政治理论：2
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @POST("studyNewsAction_queryListApp.action")
    fun queryStudys(
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int,
        @Query("columnId") columnId: Int
    ): Observable<BaseData<NewBean>>
}
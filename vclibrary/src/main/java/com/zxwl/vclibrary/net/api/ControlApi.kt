package com.hw.baselibrary.net.api

import com.zxwl.vclibrary.bean.BaseData
import com.zxwl.vclibrary.bean.OrganBean
import com.zxwl.vclibrary.bean.PoliceBean
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 *author：pc-20171125
 *data:2019/11/8 13:58
 */
interface ControlApi {

//    /**
//     * 登录
//     * accountAction_login.action
//     */
//    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
//    @POST("operatorAction_loginApp.action")
//    fun login(
//        @Query("account") account: String,
//        @Query("password") password: String,
//        @Query("deviceID") deviceID: String
//    ): Observable<LoginBean>

    /**
     * 获取组织
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @GET("api/organization")
    fun getOrgans(
        @Query("deptId") deviceID: String
    ): Observable<BaseData<OrganBean>>

    /**
     * 获取成员
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @GET("api/police")
    fun getUsers(
        @Query("deptId") deviceID: String
    ): Observable<BaseData<PoliceBean>>

    /**
     * 搜索成员
     */
    @Headers("Content-Type: application/json", "Accept: application/json")//需要添加头
    @GET("api/police")
    fun searchUsers(
        @Query("keyWord") keyWord: String
    ): Observable<BaseData<PoliceBean>>

}
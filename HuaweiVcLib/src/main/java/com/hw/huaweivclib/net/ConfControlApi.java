package com.hw.huaweivclib.net;

import com.hw.huaweivclib.net.respone.BaseData;
import com.hw.huaweivclib.net.respone.ConfBeanRespone;
import com.hw.huaweivclib.net.respone.CreateConfResponeBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * author：pc-20171125
 * data:2020/1/15 20:00
 */
public interface ConfControlApi {
    /**
     * 创建会议
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/scheduleConf")
    Observable<CreateConfResponeBean> createConf(
            @Body RequestBody body
    );

    /**
     * 加入会议
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/scheduleConf")
    Observable<BaseData> joinConf(
            @Query("smcConfId") String smcConfId,
            @Query("siteUri") String siteUri
    );

    /**
     * 设置麦克风静音
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/setSiteMute")
    Observable<BaseData> setSiteMute(
            @Query("smcConfId") String smcConfId,
            @Query("siteUri") String siteUri,
            @Query("isMute") String isMute
    );

    /**
     * 设置扬声器静音
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/setSitesQuiet")
    Observable<BaseData> setSitesQuiet(
            @Query("smcConfId") String smcConfId,
            @Query("siteUri") String siteUri,
            @Query("isQuiet") String isQuiet
    );

    /**
     * 离开会议
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/disconnectSite")
    Observable<BaseData> leaveConf(
            @Query("smcConfId") String smcConfId,
            @Query("siteUri") String siteUri
    );

    /**
     * 结束会议
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/stopConf")
    Observable<BaseData> stopConf(
            @Query("smcConfId") String smcConfId
    );

    /**
     * 切换会议模式
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/changeConfMode")
    Observable<BaseData> changeConfMode(
            @Query("smcConfId") String smcConfId,
            @Query("confMode") String confMode
    );

    /**
     * 查询会议详情
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/queryBySmcConfIdOrAccessCode")
    Observable<ConfBeanRespone> queryConfDetail(
            @Query("accessCode") String accessCode
    );

    /**
     * 广播会场
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/setBroadcastSite")
    Observable<BaseData> setBroadcastSite(
            @Query("smcConfId") String smcConfId,
            @Query("siteUri") String siteUri,
            @Query("isBroadcast") String isBroadcast
    );

    /**
     * 呼叫会场
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/connectSite")
    Observable<BaseData> connectSite(
            @Query("smcConfId") String smcConfId,
            @Query("siteUri") String siteUri
    );

    /**
     * 选看会场
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/setVideoSource")
    Observable<BaseData> setVideoSource(
            @Query("smcConfId") String smcConfId,
            @Query("siteUri") String siteUri,
            @Query("videoSourceUri") String videoSourceUri
    );

    /**
     * 添加会场
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/addSiteToConf")
    Observable<BaseData> addSiteToConf(
            @Query("smcConfId") String smcConfId,
            @Query("siteUri") String siteUri
    );

    /**
     * 设置主席
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("conf/forceSetConfChair")
    Observable<BaseData> setConfChair(
            @Query("smcConfId") String smcConfId,
            @Query("siteUri") String siteUri
    );


}

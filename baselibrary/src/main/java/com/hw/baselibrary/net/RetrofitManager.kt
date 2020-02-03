package com.hw.baselibrary.net

import com.hw.baselibrary.common.BaseApp
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 *author：pc-20171125
 *data:2019/11/8 11:50
 */
object RetrofitManager {

    //    val connectTimeout(60L, TimeUnit.SECONDS)
//    readTimeout(60L, TimeUnit.SECONDS)
//    writeTimeout(60L, TimeUnit.SECONDS)

    public lateinit var BASE_URL: String

    val CONNECT_TIME_OUT: Long = 15//以秒为单位
    val READ_TIME_OUT: Long = 15   //以秒为单位
    val WRITE_TIME_OUT: Long = 15  //以秒为单位

    /*
       具体服务实例化
    */
    fun <T> create(service: Class<T>): T {
        return getRetrofit().create(service)
    }

    /*
       具体服务实例化
    */
    fun <T> create(service: Class<T>, baseUrl: String): T {
        return getRetrofit(baseUrl).create(service)
    }

    private fun getRetrofit(): Retrofit {
        // 获取retrofit的实例
        return Retrofit.Builder()
            .baseUrl(Urls.BASE_URL)  //自己配置
            .client(getOkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    private fun getRetrofit(baseUrl: String): Retrofit {
        // 获取retrofit的实例
        return Retrofit.Builder()
            .baseUrl(baseUrl)  //自己配置
            .client(getOkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        //添加一个log拦截器,打印所有的log
        val httpLoggingInterceptor = LogInterceptor

//        //添加一个log拦截器,打印所有的log
//        val httpLoggingInterceptor = HttpLoggingInterceptor()
//        //可以设置请求过滤的水平,body,basic,headers
//        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        //设置 请求的缓存的大小跟位置
        val cacheFile = File(BaseApp.context.cacheDir, "cache")
        val cache = Cache(cacheFile, 1024 * 1024 * 50) //50Mb 缓存的大小

        return OkHttpClient.Builder()
            .addInterceptor(addQueryParameterInterceptor())  //参数添加
            .addInterceptor(addHeaderInterceptor()) // token过滤
//              .addInterceptor(addCacheInterceptor())
            .addInterceptor(httpLoggingInterceptor) //日志,所有的请求响应度看到
            .cache(cache)  //添加缓存
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * 设置公共参数
     */
    private fun addQueryParameterInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val request: Request
            val modifiedUrl = originalRequest.url().newBuilder()
                // Provide your custom parameter here
//                .addQueryParameter("udid", "d2807c895f0348a180148c9dfa6f2feeac0781b5")
//                .addQueryParameter("deviceModel", AppUtils.getMobileModel())
                .build()
            request = originalRequest.newBuilder().url(modifiedUrl).build()
            chain.proceed(request)
        }
    }

    /**
     * 设置头
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                // Provide your custom header here
//                .header("token", token)
                .method(originalRequest.method(), originalRequest.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

}
package com.hw.baselibrary.net

import com.zxwl.vclibrary.util.IOUtils
import com.zxwl.vclibrary.util.LogUtils
import okhttp3.*
import okio.Buffer
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 *author：pc-20171125
 *data:2019/11/8 13:54
 */
object LogInterceptor : Interceptor {
    private val TAG: String = "Hewei"

    private val UTF8 = Charset.forName("UTF-8")

    enum class Level {
        NONE, //不打印log
        BASIC, //只打印 请求首行 和 响应首行
        HEADERS, //打印请求和响应的所有 Header
        BODY        //所有数据全部打印
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val printLevel = Level.BODY
        var request = chain.request()

        //请求日志拦截
        logForRequest(request, chain.connection())

        //执行请求，计算请求时间
        var startNs = System.nanoTime()

        //响应
        var response: Response? = null;

        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            log("<-- HTTP FAILED: $e")
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        return logForResponse(response, tookMs, chain)
    }

    /**
     * 打印结果
     */
    private fun logForResponse(
        response: Response,
        tookMs: Long,
        chain: Interceptor.Chain
    ): Response {
        var builder = response.newBuilder()
        var clone = builder.build()
        var responseBody = clone.body()

        //打印头部
        logHeaders(clone)

        if (null == responseBody) {
            return response
        }

        if (isPlaintext(responseBody.contentType())) {
            val bytes = IOUtils.toByteArray(responseBody.byteStream())
            val contentType = responseBody.contentType()
            val body = String(bytes, getCharset(contentType)!!)
            val requestUrl = response.request().url()
            log("\trequest:\n$requestUrl\n \tbody:\n$body \n请求结束")
            responseBody = ResponseBody.create(responseBody.contentType(), bytes)

            //TODO:条件断点调试用
            // newResponse.request().url().url.contains("pointsAction_queryTotalPoint")
            // newResponse.request().url().url.contains("newsAction_queryBannerList")

            if (body.contains("账号未登录")) {

            }
            return response.newBuilder().body(responseBody).build()
        } else {
            log("\tbody: maybe [binary body], omitted!")
        }

        return response
    }

    private fun logHeaders(clone: Response) {
        val headers = clone.headers()
        var i = 0
        val count = headers.size()
        var headerBuilder = StringBuilder()
        while (i < count) {
            headerBuilder.append("\t" + headers.name(i) + ": " + headers.value(i) + "\n")
            i++
        }
        log("Header-->\n" + headerBuilder.toString())
    }

    private fun logForRequest(request: Request?, connection: Connection?) {
        log("requestUrl-->\n$request!!.url()\n请求开始")
    }

    private fun getCharset(contentType: MediaType?): Charset? {
        var charset: Charset? = if (contentType != null) contentType.charset() else UTF8
        if (charset == null) charset = UTF8
        return charset
    }

    private fun isPlaintext(mediaType: MediaType?): Boolean {
        if (mediaType == null) return false
        if (mediaType.type() != null && mediaType.type() == "text") {
            return true
        }
        var subtype: String? = mediaType.subtype()
        if (subtype != null) {
            subtype = subtype.toLowerCase()
            if (subtype.contains("x-www-form-urlencoded")
                || subtype.contains("json")
                || subtype.contains("xml")
                || subtype.contains("html")
            )
                return true
        }
        return false
    }

    private fun bodyToString(request: Request): String? {
        try {
            val copy = request.newBuilder().build()
            val body = copy.body() ?: return null
            val buffer = Buffer()
            body.writeTo(buffer)
            val charset = getCharset(body.contentType())
            val str = buffer.readString(charset!!)
            log("\tbody-->\n$str")
            return str
        } catch (e: Exception) {
            log(e.toString())
            return null
        }
    }

    fun log(msg: String) {
        LogUtils.dTag(TAG, msg)
    }
}
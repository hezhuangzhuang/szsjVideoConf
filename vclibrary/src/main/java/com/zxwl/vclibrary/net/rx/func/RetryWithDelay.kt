package com.huawei.app.net.rx.func

import io.reactivex.Observable
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

/**
 *author：pc-20171125
 *data:2019/12/1 16:54
 * 请求重试的Transformer
 */
class RetryWithDelay(val maxRetries: Int, val retryDelayMillis: Long) :
    Function<Observable<out Throwable>, Observable<*>> {

    private var retryCount: Int = 0

    override fun apply(t: Observable<out Throwable>): Observable<*> {
        return t.flatMap(object : Function<Throwable, Observable<*>> {
            override fun apply(throwable: Throwable): Observable<*> {
                if (++retryCount <= maxRetries) {
                    return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS)
                }
                return error(throwable)
            }
        })
    }
}
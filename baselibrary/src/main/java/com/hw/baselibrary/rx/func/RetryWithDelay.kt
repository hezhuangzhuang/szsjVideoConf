package com.hw.baselibrary.rx.func

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

//class RetryWithDelay(private val maxRetries: Int, private val retryDelayMillis: Int) :
//    NotificationUtils.Func1<Observable<out Throwable>, Observable<*>> {
//    private var retryCount: Int = 0
//
//    fun call(attempts: Observable<out Throwable>): Observable<*> {
//        return attempts
//            .flatMap(object : NotificationUtils.Func1<Throwable, Observable<*>>() {
//                fun call(throwable: Throwable): Observable<*> {
//                    return if (++retryCount <= maxRetries) {
//                        // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).
//                        //                            printLog(tvLogs, "", "get error, it will try after " + retryDelayMillis
//                        //                                    + " millisecond, retry count " + retryCount);
//                        Observable.timer(
//                            retryDelayMillis,
//                            TimeUnit.MILLISECONDS
//                        )
//                    } else Observable.error(throwable)
//                    // Max retries hit. Just pass the error along.
//                }
//            })
//    }
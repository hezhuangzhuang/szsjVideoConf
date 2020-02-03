package com.hw.baselibrary.rx.scheduler

import com.hw.baselibrary.rx.func.RetryWithDelay
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

/**
 *author：pc-20171125
 *data:2019/12/1 17:11
 */
class CustomCompose<T> : ObservableTransformer<T, T> {
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream.compose(SchedulerUtils.ioToMain())
            .retryWhen(RetryWithDelay(3, 1500L))
    }
//    fun call(o: Any): Any {
//        return (o as Observable)
//            .subscribeOn(Schedulers.io())
//            .unsubscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .retryWhen(RetryWithDelay(3, 300))
//    }
}

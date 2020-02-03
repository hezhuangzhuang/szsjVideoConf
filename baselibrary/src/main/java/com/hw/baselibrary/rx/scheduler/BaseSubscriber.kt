package com.hw.baselibrary.rx.scheduler

import com.hw.baselibrary.common.IBaseView
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

/**
 *author：Thinkpad
 *data:2019/12/7 09:02
 */
open class BaseSubscriber<T>(var baseView: IBaseView): Subscriber<T> {
    override fun onSubscribe(s: Subscription?) {
    }

    override fun onComplete() {
        baseView.dismissLoading()
    }

    override fun onNext(t: T) {
    }

    override fun onError(t: Throwable?) {
        baseView.dismissLoading()
    }
}
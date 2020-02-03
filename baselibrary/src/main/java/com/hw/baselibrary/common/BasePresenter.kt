package com.hw.baselibrary.common

import android.content.Context
import com.hw.baselibrary.utils.NetWorkUtils
import com.trello.rxlifecycle2.LifecycleProvider
import io.reactivex.disposables.Disposable
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2019/11/8 16:03
 */
open class BasePresenter<T : IBaseView> : IPresenter<T> {

    //rootview不能为空
    //TODO:需要在initComponent()里调用
    lateinit var mRootView: T

    @Inject
    lateinit var context: Context

    //Dagger注入，Rx生命周期管理
    @Inject
    lateinit var lifecycleProvider: LifecycleProvider<*>

    /*
        检查网络是否可用
     */
    fun checkNetWork(): Boolean {
        if (NetWorkUtils.isConnected()) {
            return true
        }
        mRootView.onError("网络不可用")
        return false
    }

    /**
     * rxjava的请求
     */
//    private var compositeDisposable = CompositeDisposable()

    override fun attachView(mRootView: T) {
        this.mRootView = mRootView
    }

    override fun detachView() {
        /**
         * 保证activity结束时取消所有正在执行的订阅
         */
//        if (!compositeDisposable.isDisposed) {
//            compositeDisposable.dispose()
//        }
    }

    private val isViewAttached: Boolean
        get() = (null != mRootView)

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun addSubscription(disposable: Disposable) {
//        compositeDisposable.add(disposable)
    }

    private class MvpViewNotAttachedException internal constructor() :
        RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")

}
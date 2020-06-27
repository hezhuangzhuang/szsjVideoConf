package com.hw.kotlinmvpandroidxframe.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.bindLife
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.kotlinmvpandroidxframe.mvp.contract.HomeContract
import com.hw.kotlinmvpandroidxframe.mvp.model.HomeService

/**
 *author：pc-20171125
 *data:2019/11/11 16:38
 */
class HomePresenter : BasePresenter<HomeContract.View>(), HomeContract.Presenter {
    val homeModel by lazy {
        HomeService()
    }

    override fun queryFirstStudys(pageNum: Int) {
        checkViewAttached()
        mRootView?.showLoading()

        var disposable = homeModel.queryStudys(pageNum)
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.SUCCESS == baseData.result) {
                        showFirstNewList(baseData.dataList)
                    } else {
                        showError(baseData.msg)
                    }
                }
            }, { t ->
                mRootView?.apply {
                    dismissLoading()
                    showError(ExceptionHandle.handleException(t))
                }
            })

        addSubscription(disposable)
    }

    override fun loadMoreStudys(pageNum: Int) {
        checkViewAttached()
        mRootView?.showLoading()

        var disposable = homeModel.queryStudys(pageNum)
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.SUCCESS == baseData.result) {
                        showMoreNewList(baseData.dataList)
                    } else {
                        showError(baseData.msg)
                    }
                }
            }, { t ->
                mRootView?.apply {
                    showError(ExceptionHandle.handleException(t))
                }
            })

        addSubscription(disposable)

    }

}
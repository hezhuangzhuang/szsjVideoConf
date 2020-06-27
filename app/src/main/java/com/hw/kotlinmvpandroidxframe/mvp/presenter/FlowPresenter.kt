package com.hw.kotlinmvpandroidxframe.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.bindLife
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.kotlinmvpandroidxframe.mvp.contract.FlowContract
import com.hw.kotlinmvpandroidxframe.mvp.model.FlowService
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2019/12/6 10:19
 */
class FlowPresenter @Inject constructor() : BasePresenter<FlowContract.View>(),
    FlowContract.Presenter {

    @Inject
    lateinit var flowModel: FlowService

    override fun queryFirstStudys(pageNum: Int) {
        checkViewAttached()
        mRootView?.showLoading()

        flowModel.queryStudys(pageNum)
            .bindLife(lifecycleProvider)
            .subscribe({ baseData ->
                mRootView?.apply {
                    //隐藏对话框
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
    }

    override fun loadMoreStudys(pageNum: Int) {
        checkViewAttached()
        mRootView?.showLoading()

        flowModel.queryStudys(pageNum)
            .subscribe({ baseData ->
                mRootView?.apply {
                    //隐藏对话框
                    dismissLoading()
                    if (NetWorkContants.SUCCESS == baseData.result) {
                        showMoreNewList(baseData.dataList)
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
    }

}
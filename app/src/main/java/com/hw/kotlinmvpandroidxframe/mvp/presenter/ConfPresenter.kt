package com.hw.kotlinmvpandroidxframe.mvp.presenter

import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.net.NetWorkContants
import com.hw.kotlinmvpandroidxframe.mvp.contract.MainContract
import com.hw.kotlinmvpandroidxframe.mvp.model.ConfService
import javax.inject.Inject

/**
 * 主界面
 */
class ConfPresenter @Inject constructor() : BasePresenter<MainContract.View>(),
    MainContract.Presenter {
    @Inject
    lateinit var confService: ConfService

    override fun getConfList(siteUri: String) {
        checkViewAttached()
        mRootView?.showLoading()

        confService.queryConfList(siteUri)
            .subscribe({ baseData ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.RESPONSE_CODE == baseData.code) {
                        queryConfSuccess(baseData.data.toString())
                    } else {
                        dismissLoading()
                        queryConfFail(baseData.msg)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    queryConfFail(ExceptionHandle.handleException(it))
                }
            })
    }


}
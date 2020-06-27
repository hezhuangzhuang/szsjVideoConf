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

    override fun authentication(appPackageName: String, secretKey: String) {
        checkViewAttached()
        mRootView?.showLoading()

        confService.authentication(appPackageName, secretKey)
            .subscribe({ baseDate ->
                mRootView?.apply {
                    dismissLoading()
                    if (NetWorkContants.SUCCESS.equals(baseDate.msg)) {
                        authenticationSuccess()
                    } else {
                        authenticationFail(baseDate.msg)
                    }
                }
            }, {
                mRootView?.apply {
                    dismissLoading()
                    authenticationFail(ExceptionHandle.handleException(it))
                }
            })
    }

    @Inject
    lateinit var confService: ConfService

    override fun getConfList(siteUri: String) {
        checkViewAttached()
//        mRootView?.showLoading()

        confService.queryConfList(siteUri)
            .subscribe({ baseData ->
                mRootView?.apply {
                    //dismissLoading()
                    if (NetWorkContants.SUCCESS.equals(baseData.msg)) {
                        queryConfSuccess(baseData.data)
                    } else {
                        queryConfFail(baseData.msg)
                    }
                }
            }, {
                mRootView?.apply {
                    //dismissLoading()
                    queryConfFail(ExceptionHandle.handleException(it))
                }
            })
    }


}
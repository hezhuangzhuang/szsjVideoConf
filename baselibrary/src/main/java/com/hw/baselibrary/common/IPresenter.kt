package com.hw.baselibrary.common

/**
 *author：pc-20171125
 *data:2019/11/8 16:01
 */
interface IPresenter<in V:IBaseView> {
    fun attachView(mRootView: V)

    fun detachView()
}
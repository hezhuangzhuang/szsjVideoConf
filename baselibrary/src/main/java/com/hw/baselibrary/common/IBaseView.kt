package com.hw.baselibrary.common

/**
 *author：pc-20171125
 *data:2019/11/8 09:30
 */
interface IBaseView {
    fun showLoading()

    fun dismissLoading()

    fun onError(text:String)
}
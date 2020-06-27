package com.hw.kotlinmvpandroidxframe.mvp.contract

import com.hw.baselibrary.common.BaseData
import com.hw.baselibrary.common.IBaseView
import com.hw.kotlinmvpandroidxframe.net.bean.ConfBean

interface MainContract {
    interface View : IBaseView {
        fun queryConfSuccess(baseData: List<ConfBean>)

        fun queryConfFail(errorMsg: String)

        //鉴权成功
        fun authenticationSuccess()

        //鉴权失败
        fun authenticationFail(errorMsg: String)

    }

    interface Presenter {

        /**
         * 获取所有会议列表
         */
        fun getConfList(siteUri: String)

        /**
         *
         * 鉴权
         */
        fun authentication(
            appPackageName: String,
            secretKey: String
        )
    }
}
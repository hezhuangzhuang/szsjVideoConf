package com.hw.kotlinmvpandroidxframe.mvp.contract

import com.hw.baselibrary.common.BaseData
import com.hw.baselibrary.common.IBaseView

interface MainContract {
    interface View : IBaseView {

        fun queryConfSuccess(baseData: String)

        fun queryConfFail(errorMsg: String)

        //鉴权成功
        fun authenticationSuccess()

        //鉴权失败
        fun authenticationFail()
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
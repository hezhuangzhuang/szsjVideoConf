package com.hw.kotlinmvpandroidxframe.mvp.contract

import com.hw.baselibrary.common.IBaseView
import com.hw.kotlinmvpandroidxframe.net.bean.NewBean

/**
 *author：pc-20171125
 *data:2019/11/11 16:35
 */
interface HomeContract {

    interface View : IBaseView {
        fun showFirstNewList(studyList: ArrayList<NewBean>)

        fun showMoreNewList(studyList: ArrayList<NewBean>)

        fun showError(errorMsg: String)

        fun showEmptyView()
    }

    interface Presenter {
        fun queryFirstStudys(pageNum: Int)

        fun loadMoreStudys(pageNum: Int)
    }

}
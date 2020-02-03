package com.hw.kotlinmvpandroidxframe.mvp.model

import com.hw.baselibrary.common.BaseData
import com.hw.baselibrary.net.RetrofitManager
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.hw.kotlinmvpandroidxframe.net.api.StudyApi
import com.hw.kotlinmvpandroidxframe.net.bean.NewBean
import io.reactivex.Observable

/**
 *author：pc-20171125
 *data:2019/11/11 16:38
 */
class HomeService {
    companion object {
        const val pageSize: Int = 10
        const val columnId: Int = 1
    }

    fun queryStudys(pageNum: Int): Observable<BaseData<NewBean>> {
        return RetrofitManager
            .create(StudyApi::class.java)
            .queryStudys(pageNum, pageSize, columnId)
            .compose(CustomCompose())
    }
}
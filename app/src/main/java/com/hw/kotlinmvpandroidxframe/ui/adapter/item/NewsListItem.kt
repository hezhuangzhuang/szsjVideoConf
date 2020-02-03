package com.hw.kotlinmvpandroidxframe.ui.adapter.item

import com.chad.library.adapter.base.entity.SectionMultiEntity
import com.hw.kotlinmvpandroidxframe.net.bean.NewBean

/**
 *author：pc-20171125
 *data:2019/11/11 17:13
 */
class NewsListItem(var newBean: NewBean) : SectionMultiEntity<NewBean>(newBean) {

    override fun getItemType(): Int {
        return newBean.titleType
    }
}
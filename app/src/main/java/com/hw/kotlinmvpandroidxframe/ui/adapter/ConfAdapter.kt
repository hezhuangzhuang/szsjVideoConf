package com.hw.kotlinmvpandroidxframe.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * 会议的适配器
 */
class ConfAdapter : BaseQuickAdapter<String, BaseViewHolder> {
    constructor(layoutResId: Int, data: MutableList<String>?) : super(layoutResId, data)


    override fun convert(helper: BaseViewHolder, item: String?) {

    }
}
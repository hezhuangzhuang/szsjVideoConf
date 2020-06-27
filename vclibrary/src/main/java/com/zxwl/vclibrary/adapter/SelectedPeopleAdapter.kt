package com.zxwl.vclibrary.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.huawei.opensdk.demoservice.Member
import com.zxwl.vclibrary.R
import com.zxwl.vclibrary.bean.PoliceBean

/**
 * 已选择的成员列表
 */
class SelectedPeopleAdapter : BaseQuickAdapter<PoliceBean, BaseViewHolder> {

    constructor(members: MutableList<PoliceBean>) : super(R.layout.item_selected_people, members)

    override fun convert(helper: BaseViewHolder, item: PoliceBean?) {
        helper?.apply {
            setText(R.id.tvAddtendName, item?.userName)
                .addOnClickListener(R.id.tvAddtendName)
        }
    }
}
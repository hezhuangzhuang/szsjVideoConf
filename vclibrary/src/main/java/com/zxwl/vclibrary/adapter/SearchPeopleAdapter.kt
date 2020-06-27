package com.zxwl.vclibrary.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.huawei.opensdk.demoservice.Member
import com.zxwl.vclibrary.R
import com.zxwl.vclibrary.adapter.item.OrganOrUserBeanItem
import com.zxwl.vclibrary.bean.OrganBean
import com.zxwl.vclibrary.bean.PoliceBean
import kotlinx.android.synthetic.main.cl_bottom_control.*

/**
 * 搜索的成员列表
 */
class SearchPeopleAdapter(data: MutableList<OrganOrUserBeanItem>?) :
    BaseSectionMultiItemQuickAdapter<OrganOrUserBeanItem, BaseViewHolder>(
        R.layout.item_organ,
        data
    ) {

    override fun convert(helper: BaseViewHolder, item: OrganOrUserBeanItem?) {
        when (item?.itemType) {
            //用户的item
            TYPE_USER_BEAN -> {
                val policeBean = item.bean as PoliceBean
                helper.apply {
                    setText(R.id.tvAddtendName, policeBean.userName)
                        .addOnClickListener(R.id.tvAddtendName)

                    var tvAddtendName = helper?.getView<TextView>(R.id.tvAddtendName)

                    tvAddtendName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        if (policeBean.check) R.mipmap.ic_check_true else R.mipmap.ic_check_false,
                        0,
                        0,
                        0
                    )
                }
            }

            //组织的item
            TYPE_ORGAN_BEAN -> {
                val organBean = item.bean as OrganBean
                helper?.apply {
                    setText(R.id.tvOrganName, organBean.name)
                        .addOnClickListener(R.id.tvOrganName)
                }
            }
        }
    }

    init {
        addItemType(TYPE_USER_BEAN, R.layout.item_search_people)
        addItemType(TYPE_ORGAN_BEAN, R.layout.item_organ)
    }

    override fun convertHead(helper: BaseViewHolder?, item: OrganOrUserBeanItem?) {

    }

    companion object {
        val TYPE_USER_BEAN = 0
        val TYPE_ORGAN_BEAN = 1
    }
}
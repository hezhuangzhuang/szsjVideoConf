package com.hw.kotlinmvpandroidxframe.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.baselibrary.utils.DateUtils
import com.hw.kotlinmvpandroidxframe.R
import com.hw.kotlinmvpandroidxframe.net.bean.ConfBean
import com.hw.kotlinmvpandroidxframe.net.bean.SiteStatusInfo

/**
 * 会议的适配器
 */
class ConfAdapter : BaseQuickAdapter<ConfBean, BaseViewHolder> {
    companion object {
        //预约的会议
        val STATE_ORDER = 2

        //正在召开的会议
        val STATE_MEETING = 3
    }
    
    constructor(data: MutableList<ConfBean>?) : super(R.layout.item_conf, data)

    override fun convert(helper: BaseViewHolder, item: ConfBean?) {
        item?.apply {
            helper.setText(R.id.tvConfName, confName)
                .setText(R.id.tvConfTime, getConfTime(beginTime, endTime))
                .setText(R.id.tvAttends, getAttendsName(siteStatusInfoList))
                .setImageResource(R.id.ivConfStatus, getConfStatusDrawable(confStatus))
                .setGone(R.id.ivJoinConf, 3 == confStatus)

            if (3 == confStatus) {
                helper.addOnClickListener(R.id.ivJoinConf)
            }
        }
    }

    /**
     * 获取会议状态的图片
     */
    private fun getConfStatusDrawable(confStatus: Int): Int {
        when (confStatus) {
            //正在召开的会议
            STATE_MEETING -> {
                return R.mipmap.ic_conf_status_running
            }

            //预约的会议
            STATE_ORDER -> {
                return R.mipmap.ic_conf_status_order
            }
        }
        return R.mipmap.ic_conf_status_end
    }

    private fun getAttendsName(siteStatusInfoList: List<SiteStatusInfo>): String {
        val attendsName = siteStatusInfoList.joinToString { siteBean ->
            siteBean.siteName
        }
        return attendsName
    }

    /**
     * 获取会议时间
     */
    private fun getConfTime(beginTime: String, endTime: String): String {
        //开始时间long
        val beginTimeLong =
            DateUtils.stringToLong(beginTime, DateUtils.FORMAT_DATE_TIME_SECOND)
        //结束时间long
        val endTimeLong = DateUtils.stringToLong(endTime, DateUtils.FORMAT_DATE_TIME_SECOND)

        //开始的年月
        val beginDate = DateUtils.longToString(beginTimeLong, DateUtils.FORMAT_DATE)
        val endDate = DateUtils.longToString(endTimeLong, DateUtils.FORMAT_DATE)

        var confTime = ""
        //开始的日期系统
        if (beginDate.equals(endDate)) {
            confTime = "${DateUtils.longToString(
                beginTimeLong,
                DateUtils.FORMAT_DATE_TIME
            )}-${DateUtils.longToString(endTimeLong, DateUtils.FORMAT_TIME)}"
        } else {
            confTime = "${DateUtils.longToString(
                beginTimeLong,
                DateUtils.FORMAT_DATE_TIME
            )}-${DateUtils.longToString(endTimeLong, DateUtils.FORMAT_DATE_TIME)}"
        }
        return confTime
    }
}
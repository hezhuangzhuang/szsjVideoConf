package com.zxwl.vclibrary.adapter

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.huawei.opensdk.demoservice.Member
import com.zxwl.vclibrary.R

/**
 * 会控的适配器
 */
class ConfControlAdapter : BaseQuickAdapter<Member, BaseViewHolder> {

    //自己是否是主席
    public var selfIsChair = false

    //true:视频会议,false:语音会议
    public var isVideoConf = true

    constructor(members: MutableList<Member>) : super(R.layout.item_conf_control, members)

    override fun convert(helper: BaseViewHolder, item: Member?) {
        item?.apply {
            setControlButtonStatus(helper)
        }
    }

    private fun Member.setControlButtonStatus(helper: BaseViewHolder) {
        //是否在线
        var isInConf = isInConf

        //是否主席
        var isChair = isChair

        //是否自己
        var isSelf = isSelf()

        //是否静音
        var isMute = isMute

        //是否广播自己
        var isBroadcastSelf = isBroadcastSelf

        var showName = StringBuffer(displayName)

        if (isSelf && isChair) {
            showName.append(" (本地、主席)")
        } else if (isSelf) {
            showName.append(" (本地)")
        } else if (isChair) {
            showName.append(" (主席)")
        }

        var itemRemove = helper.getView<ImageView>(R.id.ivItemRemove)
        var itemCloseMic = helper.getView<ImageView>(R.id.ivItemCloseMic)
        var itemBroadcast = helper.getView<ImageView>(R.id.ivItemBroadcast)
        var itemWatch = helper.getView<ImageView>(R.id.ivItemWatch)

        itemRemove.tag = "挂断"
        itemCloseMic.tag = "麦克风"
        itemBroadcast.tag = "广播"
        itemWatch.tag = "观看"

        //在线
        if (isInConf) {
            setInConfStatus(
                helper,
                showName,
                isMute,
                isBroadcastSelf,
                itemRemove,
                itemCloseMic,
                itemBroadcast,
                itemWatch
            )

            helper.setText(R.id.tvName, showName.toString())
                .setTextColor(
                    R.id.tvName,
                    mContext.resources.getColor(
                        R.color.black
                    )
                )//呼叫
                .setImageResource(
                    R.id.ivItemRemove,
                    R.mipmap.ic_item_control_hangup
                )//麦克风
                .setImageResource(
                    R.id.ivItemCloseMic,
                    if (isMute) R.mipmap.ic_item_control_close_mic_false else R.mipmap.ic_item_control_close_mic_true
                )//广播
                .setImageResource(
                    R.id.ivItemBroadcast,
                    if (isBroadcastSelf) R.mipmap.ic_item_control_broadcast_false else R.mipmap.ic_item_control_broadcast_true
                )

            itemRemove.isVisible = selfIsChair
            itemCloseMic.isVisible = selfIsChair
            itemBroadcast.isVisible = selfIsChair
            itemWatch.isVisible = selfIsChair

            if (selfIsChair) {
                helper
                    .addOnClickListener(R.id.ivItemRemove)
                    .addOnClickListener(R.id.ivItemCloseMic)
                    .addOnClickListener(R.id.ivItemBroadcast)
                    .addOnClickListener(R.id.ivItemWatch)
            } else {
                helper
                    .addOnClickListener(R.id.ivItemWatch)
            }
        } else {//会场不在线
            //自己是主席则显示呼叫按钮
            setLeaveConfStatus(itemRemove, itemCloseMic, itemBroadcast, itemWatch, helper, showName)
            itemRemove.isVisible = selfIsChair
            itemCloseMic.isVisible = false
            itemBroadcast.isVisible = false
            itemWatch.isVisible = false

            helper.setText(R.id.tvName, showName.toString())
                .setTextColor(
                    R.id.tvName,
                    mContext.resources.getColor(
                        R.color.color_aaa
                    )
                )//呼叫
                .setImageResource(
                    R.id.ivItemRemove,
                    R.mipmap.ic_item_control_call
                )//麦克风
                .setImageResource(
                    R.id.ivItemCloseMic,
                    R.mipmap.ic_item_control_close_mic_false
                )//广播
                .setImageResource(
                    R.id.ivItemBroadcast,
                    R.mipmap.ic_item_control_broadcast_false
                )
                .addOnClickListener(R.id.ivItemRemove)
        }

        //非视频会议则隐藏广播和观看按钮
        if (!isVideoConf) {
            itemBroadcast.isVisible = false
            itemWatch.isVisible = false
        }
    }

    private fun setLeaveConfStatus(
        itemRemove: ImageView,
        itemCloseMic: ImageView,
        itemBroadcast: ImageView,
        itemWatch: ImageView,
        helper: BaseViewHolder,
        showName: StringBuffer
    ) {

    }

    private fun setInConfStatus(
        helper: BaseViewHolder,
        showName: StringBuffer,
        isMute: Boolean,
        isBroadcastSelf: Boolean,
        itemRemove: ImageView,
        itemCloseMic: ImageView,
        itemBroadcast: ImageView,
        itemWatch: ImageView
    ) {

    }
}
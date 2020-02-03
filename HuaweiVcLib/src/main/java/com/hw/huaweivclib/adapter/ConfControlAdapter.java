package com.hw.huaweivclib.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hw.huaweivclib.R;
import com.hw.huaweivclib.net.respone.ConfBeanRespone;

import java.util.List;

/**
 * author：pc-20171125
 * data:2020/1/16 15:43
 */
public class ConfControlAdapter extends BaseQuickAdapter<ConfBeanRespone.DataBean.SiteStatusInfoListBean, BaseViewHolder> {
    //是否是主席
    private boolean isChair = false;

    //会议模式：0表示主席模式，1表示多画面模式
    //（1）主席模式：所有参会会场都观看主席会场，在此模式下，任意会场可以选看任意会场
    //（2）多画面模式：所有参会会场都观看多画面，在此模式下，选看功能无效

    //多画面模式，显示广播按钮
    //选看模式，显示选看按钮
    private int confMode = -1;

    /**
     * 设置是否有主席功能
     *
     * @param chair
     */
    public void setChair(boolean chair) {
        isChair = chair;
    }

    /**
     * 设置设置会议模式
     */
    public void setConfMode(int confMode) {
        this.confMode = confMode;
    }

    public ConfControlAdapter(int layoutResId, @Nullable List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ConfBeanRespone.DataBean.SiteStatusInfoListBean site) {
        helper.setText(R.id.tv_name, site.siteName)
                .setTextColor(R.id.tv_name, ContextCompat.getColor(mContext, 2 == site.siteStatus ? R.color.white : R.color.color_999))
                .setImageResource(R.id.iv_broadcast, 1 == site.broadcastStatus ? R.mipmap.ic_control_broadcast_treue : R.mipmap.ic_control_broadcast_false)
                .setImageResource(R.id.iv_mic, 1 == site.microphoneStatus ? R.mipmap.ic_mic_open : R.mipmap.ic_mic_close)
                .setImageResource(R.id.iv_hangup, 2 == site.siteStatus ? R.mipmap.ic_control_hangup : R.mipmap.ic_control_call)
                .setImageResource(R.id.iv_louder, 1 == site.loudspeakerStatus ? R.mipmap.ic_control_louder_true : R.mipmap.ic_control_louder_false)
                .setImageResource(R.id.iv_watch_site, site.isWatch ? R.mipmap.ic_control_watch_true : R.mipmap.ic_control_watch_false);

        ImageView ivWatchSite = (ImageView) helper.getView(R.id.iv_watch_site);
        ImageView ivBroadcast = (ImageView) helper.getView(R.id.iv_broadcast);
        ImageView ivMic = (ImageView) helper.getView(R.id.iv_mic);
        ImageView ivLouder = (ImageView) helper.getView(R.id.iv_louder);
        ImageView ivHangup = (ImageView) helper.getView(R.id.iv_hangup);

        ivBroadcast.setVisibility(isChair && 1 == confMode ? View.VISIBLE : View.GONE);
        ivWatchSite.setVisibility(0 == confMode ? View.VISIBLE : View.GONE);

        ivMic.setVisibility(isChair ? View.VISIBLE : View.GONE);
        ivHangup.setVisibility(isChair ? View.VISIBLE : View.GONE);
        ivLouder.setVisibility(isChair ? View.VISIBLE : View.GONE);

        ivLouder.setVisibility(View.GONE);

        helper.addOnClickListener(
                R.id.iv_watch_site,
                R.id.iv_broadcast,
                R.id.iv_mic,
                R.id.iv_louder,
                R.id.iv_hangup
        );
    }
}

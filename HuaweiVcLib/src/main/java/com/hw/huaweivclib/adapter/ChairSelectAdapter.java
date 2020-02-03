package com.hw.huaweivclib.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hw.huaweivclib.R;
import com.hw.huaweivclib.net.respone.ConfBeanRespone;

import java.util.List;

/**
 * 选择主席适配器
 */
public class ChairSelectAdapter extends BaseQuickAdapter<ConfBeanRespone.DataBean.SiteStatusInfoListBean, BaseViewHolder> {
    public ChairSelectAdapter(int layoutResId, @Nullable List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ConfBeanRespone.DataBean.SiteStatusInfoListBean item) {
        helper.setText(R.id.tv_name,item.siteName)
                .addOnClickListener(R.id.tv_set_chair);
    }
}

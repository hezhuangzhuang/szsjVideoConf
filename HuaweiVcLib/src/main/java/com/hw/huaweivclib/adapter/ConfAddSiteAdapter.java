package com.hw.huaweivclib.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hw.huaweivclib.R;
import com.hw.huaweivclib.net.respone.ConfControlUserBean;

import java.util.List;

/**
 * author：pc-20171125
 * data:2020/1/16 16:53
 */
public class ConfAddSiteAdapter extends BaseQuickAdapter<ConfControlUserBean, BaseViewHolder> {
    public ConfAddSiteAdapter(int layoutResId, @Nullable List<ConfControlUserBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ConfControlUserBean item) {
        helper.setText(R.id.tv_name,item.name)
                .setImageResource(R.id.iv_check,item.isCheck ? R.mipmap.ic_blue_check_true : R.mipmap.ic_blue_check_false);
    }
}

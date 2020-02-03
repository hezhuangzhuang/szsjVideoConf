package com.hw.huaweivclib.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.opensdk.ecsdk.common.UIConstants;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.huaweivclib.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 召集会议
 */
public class LoadingActivity extends BaseMediaActivity {
    public static final String CONF_NAME = "CONF_NAME";

    private TextView tvName;
    private ImageView ivLoading;

    private ObjectAnimator ra = null;

    public static void startActivty(Context context, String confName) {
        Intent intent = new Intent(context, LoadingActivity.class);
        intent.putExtra(CONF_NAME, confName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (null != ra) {
            ra.cancel();
        }

        //自动接听改为false
       SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, false);
       SPStaticUtils.put(UIConstants.JOIN_CONF, false);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_loading;
    }

    @Override
    public void doBusiness() {
        String confName = getIntent().getStringExtra(CONF_NAME);
        tvName.setText(confName);

        ra = ObjectAnimator.ofFloat(ivLoading, "rotation", 0f, 360f);
        ra.setDuration(1500);
        ra.setRepeatCount(ObjectAnimator.INFINITE);
        ra.setInterpolator(new LinearInterpolator());
        ra.start();

        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 15 * 1000);

        //TODO:VC--关闭会议召集界面
//        AppManager.Companion.getInstance().pushActivity(ConvokeConfNewActivity.class);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState, @NotNull View contentView) {
        tvName = (TextView) findViewById(R.id.tv_name);
        ivLoading = (ImageView) findViewById(R.id.iv_loading);
    }

    @Override
    public void setListeners() {

    }
}

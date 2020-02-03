package com.hw.huaweivclib.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huawei.opensdk.callmgr.CallMgr;
import com.hw.huaweivclib.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 去电界面
 */
public class CallOutActivity extends BaseMediaActivity {
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_call_out;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState, @NotNull View contentView) {

    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void setListeners() {
        ((TextView) findViewById(R.id.tv_number)).setText(TextUtils.isEmpty(String.valueOf(mCallNumber)) ? "" : String.valueOf(mCallNumber));

        findViewById(R.id.tv_hang_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallMgr.getInstance().endCall(mCallID);
                finish();
            }
        });

        ((TextView) findViewById(R.id.tv_number)).setText(TextUtils.isEmpty(mCallNumber) ? "" : mCallNumber);

    }
}


package com.hw.huaweivclib.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.ecsdk.common.UIConstants;
import com.hw.baselibrary.common.AppManager;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.huaweivclib.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 来电显示界面
 */
public class CallerIDActivity extends BaseMediaActivity implements View.OnClickListener {
    private ImageView ivAvatar;
    private TextView tvNumber;
    private TextView tvHangUp;
    private TextView tvAnswer;

    @Override
    public void onClick(View v) {
        if (R.id.tv_hang_up == v.getId()) {
            hangUp();
        } else if (R.id.tv_answer == v.getId()) {
            answer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean isFristClick = true;

    /**
     * 接听
     */
    private void answer() {
        if (isFristClick){
            CallMgr.getInstance().answerCall(mCallID, mIsVideoCall);
        }
    }

    /**
     * 挂断
     */
    private void hangUp() {
        //结束掉等待的对话框
        AppManager.Companion.getInstance().pushActivity(LoadingActivity.class);

        CallMgr.getInstance().endCall(mCallID);
        CallMgr.getInstance().stopPlayRingBackTone();
        CallMgr.getInstance().stopPlayRingingTone();
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        hangUp();
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_caller_id;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState, @NotNull View contentView) {
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        tvHangUp = (TextView) findViewById(R.id.tv_hang_up);
        tvAnswer = (TextView) findViewById(R.id.tv_answer);

        //是否移动端创建会议填false
        SPStaticUtils.put(UIConstants.IS_CREATE, false);
    }

    @Override
    public void doBusiness() {
        tvNumber.setText(TextUtils.isEmpty(String.valueOf(mCallNumber)) ? "" : String.valueOf(mCallNumber));
    }

    @Override
    public void setListeners() {
        tvHangUp.setOnClickListener(this);
        tvAnswer.setOnClickListener(this);
    }
}

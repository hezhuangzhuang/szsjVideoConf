package com.zxwl.vclibrary.activity

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.huawei.opensdk.callmgr.CallMgr
import com.huawei.opensdk.commonservice.util.LogUtil
import com.huawei.opensdk.demoservice.MeetingMgr
import com.huawei.opensdk.esdk.utils.ActivityStack
import com.zxwl.vclibrary.R
import com.zxwl.vclibrary.util.NotificationUtils
import com.zxwl.vclibrary.util.ToastHelper
import com.zxwl.vclibrary.util.permission.PermissionUtils
import kotlinx.android.synthetic.main.activity_caller_id.*
import me.jessyan.autosize.internal.CancelAdapt

/**
 * 来电界面
 */
class CallerIDActivity : BaseMediaActivity(), View.OnClickListener,CancelAdapt {

    companion object {
        val TAG = CallerIDActivity::class.java.simpleName
    }

    override fun initData() {
        super.initData()

        tvNumber.text = mDisplayName

        if (mIsVideoCall) {
            tvCallContent.setText(if (0 == mConfToCallHandle) "想与您视频通话..." else "邀请您加入视频会议...")
            ivVoiceAnsewer.setVisibility(View.VISIBLE)
        } else {
            tvCallContent.setText(if (0 == mConfToCallHandle) "想与您音频通话..." else "邀请您加入语音会议...")
            ivVoiceAnsewer.setVisibility(View.GONE)
            tvAnswer.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                R.mipmap.ic_voice_conf_answer,
                0,
                0
            )
        }
    }

    override fun setListener() {
        super.setListener()
        tvHangUp.setOnClickListener(this)
        tvAnswer.setOnClickListener(this)
        ivVoiceAnsewer.setOnClickListener(this)
    }

    override fun getLayoutId(): Int = R.layout.activity_caller_id

    override fun onClick(v: View?) {
        when (v) {
            tvHangUp -> {
                hangUp()
            }

            tvAnswer -> {
                answer(mIsVideoCall)
            }

            ivVoiceAnsewer -> {
                answer(false)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        NotificationUtils.cancel(NotificationUtils.CALL_IN_ID)
    }

    /**
     * 接听
     */
    private fun answer(isVideoCall: Boolean) {
        PermissionUtils.requestCameraAndMicroPermission(object : PermissionUtils.FullCallback {
            override fun onGranted(permissionsGranted: List<String>) {
                //会议
                if (callInfo!!.isFocus) {
                    CallMgr.getInstance().stopPlayRingingTone()
                    CallMgr.getInstance().stopPlayRingBackTone()

                    LogUtil.i(TAG, "走到接听的回调中来，isVideoCall：$isVideoCall")

                    CallMgr.getInstance().answerCall(mCallID, isVideoCall)
                    MeetingMgr.getInstance().acceptConf(isVideoCall)
                    finish()
                } else {//视频呼叫
                    CallMgr.getInstance().answerCall(mCallID, isVideoCall)
                }
            }

            override fun onDenied(
                permissionsDeniedForever: List<String>,
                permissionsDenied: List<String>
            ) {
                if (isVideoCall) {
                    ToastHelper.showShort("接听需要获取摄像头和麦克风权限")
                } else {
                    ToastHelper.showShort("接听需要获取麦克风权限")
                }
                hangUp()
            }
        })
    }

    /**
     * 挂断
     */
    private fun hangUp() {
        //结束掉等待的对话框
        ActivityStack.getIns().popup(LoadingActivity::class.java)

        if (0 == mConfToCallHandle) {
            CallMgr.getInstance().endCall(mCallID)
        } else {
            CallMgr.getInstance().stopPlayRingingTone()
            CallMgr.getInstance().stopPlayRingBackTone()

            CallMgr.getInstance().endCall(mCallID)
            MeetingMgr.getInstance().rejectConf()
        }
        finish()
    }

    override fun onBackPressed() {

    }

}
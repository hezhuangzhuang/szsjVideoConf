package com.zxwl.vclibrary.activity

import android.content.Intent
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.WindowManager
import com.huawei.opensdk.callmgr.CallInfo
import com.huawei.opensdk.callmgr.CallMgr
import com.huawei.opensdk.commonservice.common.LocContext
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver
import com.huawei.opensdk.commonservice.util.LogUtil
import com.huawei.opensdk.demoservice.MeetingMgr
import com.huawei.opensdk.esdk.common.IntentConstant
import com.huawei.opensdk.esdk.common.UIConstants
import com.huawei.opensdk.esdk.login.CallFunc
import com.huawei.opensdk.esdk.utils.ActivityUtil
import com.zxwl.vclibrary.util.Constants
import com.zxwl.vclibrary.util.GsonUtils
import com.zxwl.vclibrary.util.StatusBarUtils
import com.zxwl.vclibrary.util.sharedpreferences.SPStaticUtils
import java.util.*


abstract class BaseMediaActivity : BaseVcActivity() {

    companion object {
        private val CALL_CONNECTED = 100
        private val CALL_UPGRADE = 101
        private val HOLD_CALL_SUCCESS = 102
        private val VIDEO_HOLD_CALL_SUCCESS = 103
        private val MEDIA_CONNECTED = 104
        private val BLD_TRANSFER_SUCCESS = 105
        private val BLD_TRANSFER_FAILED = 106
    }

    private val mActions = arrayOf(
        CustomBroadcastConstants.ACTION_CALL_CONNECTED,
        CustomBroadcastConstants.CALL_MEDIA_CONNECTED,
        CustomBroadcastConstants.CONF_CALL_CONNECTED,
        CustomBroadcastConstants.ACTION_CALL_END,
        CustomBroadcastConstants.CALL_UPGRADE_ACTION,
        CustomBroadcastConstants.HOLD_CALL_RESULT,
        CustomBroadcastConstants.BLD_TRANSFER_RESULT,
        CustomBroadcastConstants.CALL_TRANSFER_TO_CONFERENCE
    )

    protected lateinit var mCallFunc: CallFunc

    protected var mCallNumber: String? = null
    protected var mDisplayName: String? = null
    protected var mIsVideoCall: Boolean = false
    protected var mCallID: Int = 0
    protected var mConfID: String? = null
    protected var mIsConfCall: Boolean = false

    protected var mConfToCallHandle: Int = 0
    protected lateinit var callInfo: CallInfo


    override fun initData() {
        LocBroadcast.getInstance().registerBroadcast(broadcastReceiver, mActions)

        StatusBarUtils.setColorNew(this)

        // 保持屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mCallFunc = CallFunc.getInstance()

        val callInfoString = SPStaticUtils.getString(UIConstants.CALL_INFO);
        if (!TextUtils.isEmpty(callInfoString)) {
            callInfo = GsonUtils.fromJson(callInfoString, CallInfo::class.java)
            if (null != callInfo) {
                mCallNumber = callInfo.getPeerNumber()
                mDisplayName = callInfo.getPeerDisplayName()

                if (TextUtils.isEmpty(mDisplayName)) {
                    mDisplayName = mCallNumber
                }

                mIsVideoCall = callInfo.isVideoCall()
                mCallID = callInfo.getCallID()
                mConfID = callInfo.getConfID()
                mIsConfCall = callInfo.isFocus()
                if (null != mConfID && callInfo.getConfID() != "") {
                    mConfToCallHandle = Integer.parseInt(callInfo.getConfID())
                }
            }
        }
    }

    override fun setListener() {

    }

    override fun onDestroy() {
        super.onDestroy()
        LocBroadcast.getInstance().unRegisterBroadcast(broadcastReceiver, mActions)
    }

    private val broadcastReceiver =
        LocBroadcastReceiver { broadcastName, obj ->
            when (broadcastName) {
                CustomBroadcastConstants.ACTION_CALL_CONNECTED -> {
                    mHandler.sendMessage(
                        mHandler.obtainMessage(
                            CALL_CONNECTED,
                            obj
                        )
                    )
                }

                CustomBroadcastConstants.CALL_MEDIA_CONNECTED -> {
                    mHandler.sendMessage(
                        mHandler.obtainMessage(
                            MEDIA_CONNECTED,
                            obj
                        )
                    )
                }

                CustomBroadcastConstants.CONF_CALL_CONNECTED -> {
                    finish()
                }

                CustomBroadcastConstants.ACTION_CALL_END -> {
                    finish()
                }

                CustomBroadcastConstants.CALL_UPGRADE_ACTION -> mHandler.sendEmptyMessage(
                    CALL_UPGRADE
                )

                CustomBroadcastConstants.HOLD_CALL_RESULT -> if ("HoldSuccess" == obj) {
                    //mCallNumberTv.setTag("Hold");
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS)
                } else if ("UnHoldSuccess" == obj) {
                    //mCallNumberTv.setTag("UnHold");
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS)
                } else if ("VideoHoldSuccess" == obj) {
                    mHandler.sendEmptyMessage(VIDEO_HOLD_CALL_SUCCESS)
                }
                CustomBroadcastConstants.BLD_TRANSFER_RESULT -> if ("BldTransferSuccess" == obj) {
                    mHandler.sendEmptyMessage(BLD_TRANSFER_SUCCESS)
                } else if ("BldTransferFailed" == obj) {
                    mHandler.sendEmptyMessage(BLD_TRANSFER_FAILED)
                }

                CustomBroadcastConstants.CALL_TRANSFER_TO_CONFERENCE -> Timer().schedule(object :
                    TimerTask() {
                    override fun run() {
                        if (CallMgr.getInstance().isResumeHold) {
                            CallMgr.getInstance().unHoldCall(CallMgr.getInstance().original_CallId)
                        }
                    }
                }, 20000)

                else -> {
                }
            }
        }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            LogUtil.i(UIConstants.DEMO_TAG, "来通知了:" + msg.what)
            when (msg.what) {
                MEDIA_CONNECTED -> {
                    LogUtil.i(UIConstants.DEMO_TAG, "171-->MEDIA_CONNECTED")

//                    mediaConnected(msg)
                }

                CALL_CONNECTED -> {
                    LogUtil.i(UIConstants.DEMO_TAG, "177-->CALL_CONNECTED")
//                    callConnected(msg)
                    mediaConnected(msg)
                }

                //切换到视频通话的请求
                CALL_UPGRADE -> {
                }

                HOLD_CALL_SUCCESS -> {
                    val textDisplayName = if (null == mDisplayName) "" else mDisplayName
                    val textCallNumber = if (null == mCallNumber) "" else mCallNumber
                }

                VIDEO_HOLD_CALL_SUCCESS -> {
                    val textDisplayName = if (null == mDisplayName) "" else mDisplayName
                    var textCallNumber = if (null == mCallNumber) "" else mCallNumber
                    textCallNumber = textCallNumber + "Holding"
                }
                else -> {
                }
            }
        }
    }

    private fun callConnected(msg: Message) {
        if (msg.obj is CallInfo) {
            val callInfo = msg.obj as CallInfo
            LogUtil.i(UIConstants.DEMO_TAG, "78-->$callInfo")
            if (callInfo.isVideoCall) {//视频呼叫
                var isAutoAnswer =
                    MeetingMgr.getInstance().judgeInviteFormMySelf(callInfo.confID)
                try {
                    isAutoAnswer = SPStaticUtils.getBoolean(Constants.IS_AUTO_ANSWER)
                } catch (e: Exception) {
                    isAutoAnswer = false
                }


                val intent = Intent(IntentConstant.VIDEO_ACTIVITY_ACTION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addCategory(IntentConstant.DEFAULT_CATEGORY)

                SPStaticUtils.put(UIConstants.CALL_INFO, GsonUtils.toJson(callInfo))

                //intent.putExtra(UIConstants.IS_MEETING, false);
                ActivityUtil.startActivity(this@BaseMediaActivity, intent)
                finishActivityLine(90)

            } else {//语音呼叫
                val intent = Intent(IntentConstant.VOICE_ACTIVITY_ACTION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addCategory(IntentConstant.DEFAULT_CATEGORY)

                SPStaticUtils.put(UIConstants.CALL_INFO, GsonUtils.toJson(callInfo))

                //                            intent.putExtra(.IS_MEETING, false);
                ActivityUtil.startActivity(this@BaseMediaActivity, intent)
                finishActivityLine(90)
            }
        }
    }

    private fun mediaConnected(msg: Message) {
        if (msg.obj is CallInfo) {
            val callInfo = msg.obj as CallInfo
            LogUtil.i(UIConstants.DEMO_TAG, "78-->$callInfo")
            //视频呼叫
            if (callInfo.isVideoCall) {
                //会议
                if (callInfo.isFocus) {
                    CallMgr.getInstance().videoAnswerTime = System.currentTimeMillis()
                    val intent =
                        Intent(IntentConstant.VIDEO_CONF_MANAGER_ACTIVITY_ACTION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra(UIConstants.CONF_ID, callInfo.confID)
                    intent.putExtra(UIConstants.CALL_ID, callInfo.callID)
                    intent.putExtra(UIConstants.PEER_NUMBER, callInfo.peerNumber)
                    intent.putExtra(UIConstants.IS_VIDEO_CONF, true)
                    ActivityUtil.startActivity(LocContext.getContext(), intent)
                }//点对点呼叫
                else {
                    val intent = Intent(IntentConstant.VIDEO_ACTIVITY_ACTION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addCategory(IntentConstant.DEFAULT_CATEGORY)

                    SPStaticUtils.put(UIConstants.CALL_INFO, GsonUtils.toJson(callInfo))

                    ActivityUtil.startActivity(this@BaseMediaActivity, intent)
                    finishActivityLine(90)
                }
            } else {//语音呼叫
                //会议
                if (callInfo.isFocus) {
                    CallMgr.getInstance().voiceAnswerTime = System.currentTimeMillis()
                    val intent =
                        Intent(IntentConstant.VOICE_CONF_MANAGER_ACTIVITY_ACTION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra(UIConstants.CONF_ID, callInfo.confID)
                    intent.putExtra(UIConstants.CALL_ID, callInfo.callID)
                    intent.putExtra(UIConstants.PEER_NUMBER, callInfo.peerNumber)
                    intent.putExtra(UIConstants.IS_VIDEO_CONF, false)
                    ActivityUtil.startActivity(LocContext.getContext(), intent)
                } else {
                    val intent = Intent(IntentConstant.VOICE_ACTIVITY_ACTION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addCategory(IntentConstant.DEFAULT_CATEGORY)

                    SPStaticUtils.put(UIConstants.CALL_INFO, GsonUtils.toJson(callInfo))

                    ActivityUtil.startActivity(this@BaseMediaActivity, intent)
                    finishActivityLine(90)
                }
            }
        }
    }

    private fun finishActivityLine(line: Int) {
        finish()
    }


}
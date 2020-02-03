package com.hw.huaweivclib.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ecsdk.common.UIConstants;
import com.huawei.opensdk.ecsdk.logic.CallFunc;
import com.huawei.opensdk.ecsdk.utils.ActivityUtil;
import com.huawei.opensdk.ecsdk.utils.IntentConstant;
import com.hw.baselibrary.ui.activity.BaseActivity;
import com.hw.baselibrary.utils.NotificationUtils;
import com.hw.baselibrary.utils.ToastHelper;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.provider.chat.bean.ChatBean;
import com.hw.provider.chat.bean.ConstactsBean;
import com.hw.provider.chat.bean.MessageBody;
import com.hw.provider.chat.bean.MessageReal;
import com.hw.provider.chat.utils.GreenDaoUtil;
import com.hw.provider.chat.utils.MessageUtils;
import com.hw.provider.eventbus.EventBusUtils;
import com.hw.provider.eventbus.EventMsg;
import com.hw.provider.huawei.commonservice.common.LocContext;
import com.hw.provider.huawei.commonservice.localbroadcast.CustomBroadcastConstants;
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcast;
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcastReceiver;
import com.hw.provider.huawei.commonservice.util.LogUtil;
import com.hw.provider.router.provider.message.impl.MessageModuleRouteService;
import com.hw.provider.user.UserContants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * author：pc-20171125
 * data:2019/1/4 14:31
 */
public abstract class BaseMediaActivity extends BaseActivity implements LocBroadcastReceiver {
    private static final int CALL_CONNECTED = 100;
    private static final int CALL_UPGRADE = 101;
    private static final int HOLD_CALL_SUCCESS = 102;
    private static final int VIDEO_HOLD_CALL_SUCCESS = 103;
    private static final int MEDIA_CONNECTED = 104;

    private String[] mActions = new String[]{
            CustomBroadcastConstants.ACTION_CALL_CONNECTED,
            CustomBroadcastConstants.CALL_MEDIA_CONNECTED,
            CustomBroadcastConstants.CONF_CALL_CONNECTED,
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.CALL_UPGRADE_ACTION,
            CustomBroadcastConstants.HOLD_CALL_RESULT
    };

    protected String mCallNumber;
    protected String mDisplayName;
    protected boolean mIsVideoCall;
    protected int mCallID = -1;
    protected String mConfID;
    protected boolean mIsConfCall;
    protected boolean mIsCaller;

    private CallFunc mCallFunc;

    private boolean isJoin = false;

    private Gson gson = new Gson();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MEDIA_CONNECTED:
                    if (msg.obj instanceof CallInfo && !isJoin) {
                        CallInfo callInfo = (CallInfo) msg.obj;
                        Log.i(TAG, "69-->" + callInfo.toString());
                        if (!callInfo.isVideoCall()) {
                            Intent intent = new Intent(IntentConstant.AUDIO_ACTIVITY_ACTION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
//                            intent.putExtra(UIConstants.CALL_INFO, callInfo);

                            SPStaticUtils.put(UIConstants.CALL_INFO, gson.toJson(callInfo));

                            ActivityUtil.startActivity(BaseMediaActivity.this, intent);

                            isJoin = true;
                            finishActivityLine(73);
                        }
                    }
                    break;

                case CALL_CONNECTED:
                    if (msg.obj instanceof CallInfo) {
                        CallInfo callInfo = (CallInfo) msg.obj;
                        Log.i(TAG, "69-->" + callInfo.toString());
                        if (callInfo.isVideoCall()) {
                            boolean isConf = MeetingMgr.getInstance().judgeInviteFormMySelf(callInfo.getConfID());
                            try {
                                isConf = SPStaticUtils.getBoolean(UIConstants.IS_AUTO_ANSWER, false);
                            } catch (Exception e) {
                                isConf = false;
                            }

                            if (isConf) {
                                SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, false);
                                LogUtil.i(UIConstants.DEMO_TAG, "呼叫内容:" + callInfo.toString());
                                String confID = callInfo.getCallID() + "";
                                Intent intent = new Intent(IntentConstant.VIDEO_CONF_ACTIVITY_ACTION);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(UIConstants.CONF_ID, confID);
                                intent.putExtra(UIConstants.CALL_ID, callInfo.getCallID());
                                intent.putExtra(UIConstants.PEER_NUMBER, callInfo.getPeerNumber());

                                SPStaticUtils.put(UIConstants.CALL_INFO, gson.toJson(callInfo));

                                ActivityUtil.startActivity(LocContext.getContext(), intent);

                                finishActivityLine(191);
                            } else {
                                Intent intent = new Intent(IntentConstant.VIDEO_ACTIVITY_ACTION);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
//                              intent.putExtra(UIConstants.CALL_INFO, callInfo);

                                SPStaticUtils.put(UIConstants.CALL_INFO, gson.toJson(callInfo));

                                //TODO:判断是否是会议
                                intent.putExtra(UIConstants.IS_MEETING, false);
                                ActivityUtil.startActivity(BaseMediaActivity.this, intent);
                                finishActivityLine(90);
                            }
                        }
                    }
                    break;

                case CALL_UPGRADE:
                    break;

                case HOLD_CALL_SUCCESS: {
                    String textDisplayName = null == mDisplayName ? "" : mDisplayName;
                    String textCallNumber = null == mCallNumber ? "" : mCallNumber;
                }
                break;

                case VIDEO_HOLD_CALL_SUCCESS: {
                    String textDisplayName = null == mDisplayName ? "" : mDisplayName;
                    String textCallNumber = null == mCallNumber ? "" : mCallNumber;
                    textCallNumber = textCallNumber + "Holding";
                }
                break;

                default:
                    break;
            }
        }
    };

    private void finishActivityLine(int line) {
        finish();
    }

    private String TAG = BaseMediaActivity.class.getSimpleName();

    @Override
    protected void onResume() {
        super.onResume();

        LocBroadcast.getInstance().registerBroadcast(this, mActions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
    }

    @Override
    public void onReceive(String broadcastName, final Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.ACTION_CALL_CONNECTED:
                mHandler.sendMessage(mHandler.obtainMessage(CALL_CONNECTED, obj));
                break;

            case CustomBroadcastConstants.CALL_MEDIA_CONNECTED:
                mHandler.sendMessage(mHandler.obtainMessage(MEDIA_CONNECTED, obj));
                break;

            case CustomBroadcastConstants.CONF_CALL_CONNECTED:
//                PreferencesHelper.saveData(UIConstants.IS_AUTO_ANSWER, false);
                SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, false);

                CallInfo callInfo = (CallInfo) obj;
                LogUtil.i(UIConstants.DEMO_TAG, "呼叫内容:" + callInfo.toString());
                String confID = callInfo.getCallID() + "";
                Intent intent = new Intent(IntentConstant.VIDEO_CONF_ACTIVITY_ACTION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(UIConstants.CONF_ID, confID);
                intent.putExtra(UIConstants.CALL_ID, callInfo.getCallID());
                intent.putExtra(UIConstants.PEER_NUMBER, callInfo.getPeerNumber());

                SPStaticUtils.put(UIConstants.CALL_INFO, gson.toJson(callInfo));

                ActivityUtil.startActivity(LocContext.getContext(), intent);
                finishActivityLine(191);
                break;

            case CustomBroadcastConstants.ACTION_CALL_END:
                //486：挂断
                if (obj instanceof CallInfo) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CallInfo params = (CallInfo) obj;
                            if (403 == params.getReasonCode() || 603 == params.getReasonCode()) {
                                ToastHelper.INSTANCE.showShort("对方已拒接");
                                if (params.isCaller()) {
                                    //发送消息
                                    sendTextMsg("已拒绝", "对方已拒绝", params.getPeerNumber(), params.isVideoCall());
                                }
                                finishActivityLine(202);
                            } else if (404 == params.getReasonCode()) {
                                ToastHelper.INSTANCE.showShort("对方不在线");
                                finishActivityLine(208);
                            } else if (486 == params.getReasonCode()) {
                                ToastHelper.INSTANCE.showShort("对方正忙");
                                //主叫
                                if (params.isCaller()) {
                                    //发送消息
                                    sendTextMsg("忙线未接听", "对方正忙", params.getPeerNumber(), params.isVideoCall());
                                }

                                finishActivityLine(208);
                            } else if (0 == params.getReasonCode()) {
                                ToastHelper.INSTANCE.showShort("通话结束");

                                //如果是主叫
                                if (params.isCaller()) {
                                    //发送消息
                                    sendTextMsg("对方已取消", "已取消", params.getPeerNumber(), params.isVideoCall());
                                }
                                finishActivityLine(262);
                            } else {
                                finishActivityLine(210);
                            }

                            //删除notif
                            NotificationUtils.cancel(NotificationUtils.CALL_IN_ID);
                            NotificationUtils.cancel(NotificationUtils.AUDIO_ID);
                            NotificationUtils.cancel(NotificationUtils.VIDEO_ID);
                        }
                    });
                }
                break;

            case CustomBroadcastConstants.CALL_UPGRADE_ACTION:
                mHandler.sendEmptyMessage(CALL_UPGRADE);
                break;

            case CustomBroadcastConstants.HOLD_CALL_RESULT:
                if ("HoldSuccess".equals(obj)) {
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS);
                } else if ("UnHoldSuccess".equals(obj)) {
                    mHandler.sendEmptyMessage(HOLD_CALL_SUCCESS);
                } else if ("VideoHoldSuccess".equals(obj)) {
                    mHandler.sendEmptyMessage(VIDEO_HOLD_CALL_SUCCESS);
                }
                break;

            default:
                break;
        }
    }


    /**
     * 发送消息
     *
     * @param sendMsg     发出去的消息
     * @param saveMsg     保存在本地的消息
     * @param peerNumber  收件人名称
     * @param isVideoCall true：视频呼叫
     * @return
     */
    private boolean sendTextMsg(String sendMsg,
                                String saveMsg,
                                String peerNumber,
                                boolean isVideoCall) {

        MessageBody messageBody = getMessageBody(sendMsg, peerNumber, isVideoCall);

        //是否发送成功
        boolean sendSuccess = MessageModuleRouteService.INSTANCE.sendMessage(messageBody);

        //发送成功
        if (sendSuccess) {
            //将消息转换成chatbean
            ChatBean sendChatBean = MessageUtils.INSTANCE.sendMessageToChatBean(messageBody);
            //设置消息的内容
            sendChatBean.textContent = saveMsg;

            //插入到数据库
            GreenDaoUtil.insertChatBean(sendChatBean);

            //插入到最后消息数据
            GreenDaoUtil.insertLastChatBean(sendChatBean.toLastMesage());

            //刷新首页消息
            EventBusUtils.INSTANCE.sendMessage(EventMsg.Companion.getREFRESH_HOME_MESSAGE(), "");
            //修改消息内容
            messageBody.getReal().setMessage(sendChatBean.textContent);

            //聊天界面更新消息
            EventBusUtils.INSTANCE.sendMessage(EventMsg.Companion.getSEND_SINGLE_MESSAGE(), messageBody);
        }

        return false;
    }


    /**
     * 获取发送的消息
     *
     * @param textMsg
     * @param peerNumber
     * @param isVideoCall
     * @return
     */
    private MessageBody getMessageBody(String textMsg,
                                       String peerNumber,
                                       boolean isVideoCall) {
        String sendId = SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT);
        String sendName = SPStaticUtils.getString(UserContants.DISPLAY_NAME);

        ConstactsBean constactsBean = GreenDaoUtil.queryByHuaweiIdConstactsBean(peerNumber);
        String receiveName = peerNumber;

        //是否获取到用户名
        if (null != constactsBean) {
            receiveName = constactsBean.name;
        }

        MessageReal messageReal = new MessageReal(
                textMsg,
                isVideoCall ? MessageReal.Companion.getTYPE_VIDEO_CALL() : MessageReal.Companion.getTYPE_VOICE_CALL(),
                ""
        );

        MessageBody messageBody = new MessageBody(
                sendId,
                sendName,
                peerNumber,
                receiveName,
                MessageBody.Companion.getTYPE_PERSONAL(),
                messageReal
        );

        return messageBody;
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCallFunc = CallFunc.getInstance();

        Intent intent = getIntent();

        CallInfo callInfo = gson.fromJson(SPStaticUtils.getString(UIConstants.CALL_INFO), CallInfo.class);

        if (null != callInfo) {
            Log.i(TAG, "120-->" + callInfo.toString());
            mCallNumber = callInfo.getPeerNumber();
            mDisplayName = callInfo.getPeerDisplayName();
            mIsVideoCall = callInfo.isVideoCall();
            mCallID = callInfo.getCallID();
            mConfID = callInfo.getConfID();
            mIsConfCall = callInfo.isFocus();
            mIsCaller = callInfo.isCaller();
        }
    }


    @Override
    public void onError(@NotNull String text) {

    }
}

package com.hw.huaweivclib.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ecsdk.common.UIConstants;
import com.huawei.opensdk.ecsdk.logic.CallFunc;
import com.huawei.opensdk.ecsdk.utils.DateUtil;
import com.huawei.opensdk.ecsdk.utils.IntentConstant;
import com.hw.baselibrary.net.RetrofitManager;
import com.hw.baselibrary.net.Urls;
import com.hw.baselibrary.rx.scheduler.CustomCompose;
import com.hw.baselibrary.ui.activity.BaseActivity;
import com.hw.baselibrary.utils.DateUtils;
import com.hw.baselibrary.utils.NotificationUtils;
import com.hw.baselibrary.utils.ToastHelper;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.huaweivclib.R;
import com.hw.huaweivclib.net.ConfControlApi;
import com.hw.huaweivclib.net.respone.BaseData;
import com.hw.huaweivclib.net.respone.ConfBeanRespone;
import com.hw.provider.chat.bean.ChatBean;
import com.hw.provider.chat.bean.ConstactsBean;
import com.hw.provider.chat.bean.MessageBody;
import com.hw.provider.chat.bean.MessageReal;
import com.hw.provider.chat.utils.GreenDaoUtil;
import com.hw.provider.chat.utils.MessageUtils;
import com.hw.provider.eventbus.EventBusUtils;
import com.hw.provider.eventbus.EventMsg;
import com.hw.provider.huawei.commonservice.localbroadcast.CustomBroadcastConstants;
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcast;
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcastReceiver;
import com.hw.provider.router.provider.message.impl.MessageModuleRouteService;
import com.hw.provider.user.UserContants;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.reactivex.functions.Consumer;

import static com.hw.provider.huawei.commonservice.common.LocContext.getContext;

/**
 * 语音会议
 */
public class AudioActivity extends BaseActivity implements LocBroadcastReceiver, View.OnClickListener {
    private static final String TAG = "AudioActivity";
    /*会控按钮--*/
    private TextView tvHangUp;
    private TextView tvMic;
    private TextView tvMute;
    /*会控按钮--end*/

    private String[] mActions = new String[]{
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.ADD_LOCAL_VIEW,
            CustomBroadcastConstants.DEL_LOCAL_VIEW
    };

    private CallInfo mCallInfo;
    private int mCallID;
    private Object thisVideoActivity = this;

    private CallMgr mCallMgr;
    private CallFunc mCallFunc;

    private MeetingMgr instance;

    private String confId;

    private String peerNumber;

    private boolean isMic = true; //麦克风是否静音
    private boolean isMute = false;//外放是否静音

    private String smcConfId;//smc的会议id，添加会场使用

    //开始的时间
    private long startTimeLong;

    private Gson gson = new Gson();

    @Override
    public void initData(@Nullable Bundle bundle) {
        Intent intent = getIntent();

        mCallInfo = gson.fromJson(SPStaticUtils.getString(UIConstants.CALL_INFO), CallInfo.class);
        peerNumber = mCallInfo.getPeerNumber();

        this.mCallID = mCallInfo.getCallID();

        mCallMgr = CallMgr.getInstance();
        mCallFunc = CallFunc.getInstance();

        if ((CallConstant.TYPE_LOUD_SPEAKER != CallMgr.getInstance().getCurrentAudioRoute())) {
            CallMgr.getInstance().switchAudioRoute();
        }



        //发送notif
        sendNotif(mCallInfo);

        startTimeLong = System.currentTimeMillis();
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_audio;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState, @NotNull View contentView) {
        tvHangUp = (TextView) findViewById(R.id.tv_hang_up);
        tvMic = (TextView) findViewById(R.id.tv_mic);
        tvMute = (TextView) findViewById(R.id.tv_mute);

        //设置扬声器的状态
        setAudioRouteStatus();

        //是否静音
        setMuteStatus();
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void setListeners() {
        tvHangUp.setOnClickListener(this);
        tvMic.setOnClickListener(this);
        tvMute.setOnClickListener(this);
    }

    @Override
    public void onError(@NotNull String text) {

    }

    @Override
    public void onClick(View v) {
        if (R.id.tv_hang_up == v.getId()) {
            //结束会议
            mCallMgr.endCall(mCallID);
        } else if (R.id.tv_mute == v.getId()) {
            if (mCallInfo.isFocus()) {
                if (isMute) {
                    setSitesQuietRequest(false);
                } else {
                    setSitesQuietRequest(true);
                }
            } else {
                if (isMuteSpeakStatus()) {
                    huaweiOpenSpeaker();
                } else {
                    huaweiCloseSpeaker();
                }
            }
        } else if (R.id.tv_mic == v.getId()) {
            if (mCallInfo.isFocus()) {
                //处于静音
                if (isMic) {
                    setSiteMuteRequest(false);
                } else {
                    setSiteMuteRequest(true);
                }
            } else {
                muteMic();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mCallInfo.isFocus()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    queryConfInfo();
                }
            }, 3 * 1000);

        }
        LocBroadcast.getInstance().registerBroadcast(this, mActions);
    }

    private void queryConfInfo() {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .queryConfDetail(peerNumber)
                .compose(new CustomCompose())
                .subscribe(new Consumer<ConfBeanRespone>() {
                    @Override
                    public void accept(ConfBeanRespone baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            //smcconfid
                            smcConfId = baseData.data.smcConfId;

                            //设置本人的麦克风状态
                            for (ConfBeanRespone.DataBean.SiteStatusInfoListBean siteBean : baseData.data.siteStatusInfoList) {
                                if (siteBean.siteUri.equals(SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT))) {
                                    tvMic.setCompoundDrawablesWithIntrinsicBounds(0, 1 == siteBean.microphoneStatus ? R.mipmap.ic_mic_open : R.mipmap.ic_mic_close, 0, 0);
                                    tvMute.setCompoundDrawablesWithIntrinsicBounds(0, 1 == siteBean.loudspeakerStatus ? R.mipmap.icon_unmute : R.mipmap.icon_mute, 0, 0);
                                    break;
                                }
                            }

                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(throwable.getMessage());
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "217-->onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cancleNotif();

        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.ACTION_CALL_END:
                //如果是主叫则发送消息
                if (mCallInfo.isCaller()) {
                    long chatTimeLong = System.currentTimeMillis() - startTimeLong;
                    String chatTime = DateUtils.longToString(chatTimeLong, "mm:ss");


                    //发送消息
                    sendTextMsg("通话时长 " + chatTime, "通话时长 " + chatTime, mCallInfo.getPeerNumber(), false);

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
                break;

            case CustomBroadcastConstants.ADD_LOCAL_VIEW:
                break;

            case CustomBroadcastConstants.DEL_LOCAL_VIEW:
                break;

            default:
                break;
        }
    }

    public void setAudioRouteStatus() {
        int audioRoute = CallMgr.getInstance().getCurrentAudioRoute();
        boolean isLoudSpeaker = CallConstant.TYPE_LOUD_SPEAKER == audioRoute;
        tvMute.setCompoundDrawablesWithIntrinsicBounds(0, isLoudSpeaker ? R.mipmap.icon_unmute : R.mipmap.icon_mute, 0, 0);
    }

    private void setMuteStatus() {
        boolean currentMuteStatus = mCallFunc.isMuteStatus();
        //更新状态静音按钮状态
        tvMic.setCompoundDrawablesWithIntrinsicBounds(0, currentMuteStatus ? R.mipmap.ic_mic_close : R.mipmap.ic_mic_open, 0, 0);
    }

    /**
     * true代表静音
     *
     * @return
     */
    private boolean isMuteSpeakStatus() {
        if (0 != mCallID) {
            return CallMgr.getInstance().getMuteSpeakStatus(mCallID);
        } else {
            return false;
        }
    }

    /**
     * 静音扬声器
     */
    private void huaweiCloseSpeaker() {
        if (0 != mCallID) {
            boolean muteSpeak = CallMgr.getInstance().muteSpeak(mCallID, true);
        }
        setSpeakerStatus();
    }

    /**
     * 打开扬声器
     */
    private void huaweiOpenSpeaker() {
        if (0 != mCallID) {
            CallMgr.getInstance().muteSpeak(mCallID, false);
        }
        setSpeakerStatus();
    }

    /**
     * 设置扬声器的图片
     */
    private void setSpeakerStatus() {
        tvMute.setCompoundDrawablesWithIntrinsicBounds(0, isMuteSpeakStatus() ? R.mipmap.icon_mute : R.mipmap.icon_unmute, 0, 0);
    }

    /**
     * 麦克风静音
     */
    public void muteMic() {
        boolean currentMuteStatus = isMic;
        if (CallMgr.getInstance().muteMic(mCallID, !currentMuteStatus)) {
            mCallFunc.setMuteStatus(!currentMuteStatus);
//            Toast.makeText(this, "麦克风" + (currentMuteStatus ? "当前静音" : "非静音"), Toast.LENGTH_SHORT).show();
            setMuteStatus();
            isMic = !isMic;
        }
    }


    /**
     * 外放闭音
     */
    private void setSitesQuietRequest(final boolean isMuteParam) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .setSitesQuiet(smcConfId, SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT), String.valueOf(isMuteParam))
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            if (isMuteParam) {
                                //静音成功
                                tvMute.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_mute, 0, 0);
                            } else {
                                //取消静音成功
                                tvMute.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_unmute, 0, 0);
                            }
                            isMute = isMuteParam;
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort( throwable.getMessage());
                    }
                });
    }

    /**
     * 麦克风闭音
     */
    private void setSiteMuteRequest(final boolean isMicParam) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .setSiteMute(smcConfId, SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT), String.valueOf(isMicParam))
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            if (isMicParam) {
                                //静音成功
                                tvMic.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_mic_close, 0, 0);
                            } else {
                                //取消静音成功
                                tvMic.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_mic_open, 0, 0);
                            }
                            isMic = isMicParam;
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(throwable.getMessage());
                    }
                });
    }

    private static Intent intent = new Intent(IntentConstant.AUDIO_ACTIVITY_ACTION);

    /**
     * 发送notif
     */
    private void sendNotif(final CallInfo callInfo) {
        NotificationUtils.notify(NotificationUtils.VIDEO_ID, new NotificationUtils.Func1<Void, NotificationCompat.Builder>() {
            @Override
            public Void call(NotificationCompat.Builder param) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
//                intent.putExtra(UIConstants.CONF_ID, confID);
//                intent.putExtra(UIConstants.CALL_ID, callInfo.getCallID());
//                intent.putExtra(UIConstants.PEER_NUMBER, callInfo.getPeerNumber());

                //判断是否是会议
                param.setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText((callInfo.isVideoCall() ? "视频" : "语音") + "通话中,点击以继续")
                        .setContentIntent(PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                        //设置该通知的优先级
                        .setPriority(Notification.PRIORITY_HIGH)
                        //让通知右滑是否能能取消通知,默认是false
                        .setOngoing(true)
                        .setAutoCancel(false);
                return null;
            }
        });
    }

    private void cancleNotif() {
        NotificationUtils.cancel(NotificationUtils.AUDIO_ID);
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
}

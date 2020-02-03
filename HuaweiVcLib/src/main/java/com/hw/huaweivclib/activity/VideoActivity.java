package com.hw.huaweivclib.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.VideoMgr;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ecsdk.common.UIConstants;
import com.huawei.opensdk.ecsdk.logic.CallFunc;
import com.huawei.opensdk.ecsdk.utils.IntentConstant;
import com.huawei.videoengine.ViERenderer;
import com.hw.baselibrary.common.AppManager;
import com.hw.baselibrary.ui.activity.BaseActivity;
import com.hw.baselibrary.utils.DateUtils;
import com.hw.baselibrary.utils.DisplayUtil;
import com.hw.baselibrary.utils.NotificationUtils;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.huaweivclib.R;
import com.hw.huaweivclib.service.AudioStateWatchService;
import com.hw.huaweivclib.widget.DragFrameLayout;
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
import com.hw.provider.huawei.commonservice.util.LogUtil;
import com.hw.provider.router.provider.message.impl.MessageModuleRouteService;
import com.hw.provider.user.UserContants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.concurrent.locks.ReentrantLock;

import me.jessyan.autosize.internal.CancelAdapt;

import static com.hw.provider.huawei.commonservice.common.LocContext.getContext;


/**
 * 视频界面
 */
public class VideoActivity extends BaseActivity implements LocBroadcastReceiver, View.OnClickListener , CancelAdapt {
    private TextView tvTopTitle;
    private ImageView ivRightOperate;
    /*顶部按钮--end*/

    /*视频界面--*/
    private FrameLayout mRemoteView;
    private DragFrameLayout mLocalView;
    private FrameLayout mHideView;
    /*视频界面--end*/

    /*会控按钮--*/
    private TextView tvHangUp;
    private TextView tvMic;
    private TextView tvMute;
    /*会控按钮--end*/

    private Gson gson = new Gson();

    private static final int ADD_LOCAL_VIEW = 101;

    private String[] mActions = new String[]{
            CustomBroadcastConstants.ACTION_CALL_END,
            CustomBroadcastConstants.ADD_LOCAL_VIEW,
            CustomBroadcastConstants.DEL_LOCAL_VIEW
    };

    private CallInfo mCallInfo;
    private int mCallID;
    private Object thisVideoActivity = this;

    private int mCameraIndex = CallConstant.FRONT_CAMERA;

    private CallMgr mCallMgr;
    private CallFunc mCallFunc;
    private MeetingMgr instance;

    /*会控顶部*/
    private ImageView ivBg;
    private RelativeLayout llTopControl;
    private LinearLayout llBottomControl;
    /*会控顶部-end*/

    private ImageView ivBackOperate;

    private boolean showControl = true;//是否显示控制栏

    //开始的时间
    private long startTimeLong;
    private long last = 0;

    @Override
    protected void onResume() {
        super.onResume();

        showControl = llBottomControl.getVisibility() == View.VISIBLE && llTopControl.getVisibility() == View.VISIBLE;

        LocBroadcast.getInstance().registerBroadcast(this, mActions);

        addSurfaceView(false);

        //是否开启画面自动旋转
        setAutoRotation(this, true, "148");

        //如果不是扬声器则切换成扬声器
        AudioStateWatchService.suitCurrAudioDevice();

        setLocalView();

        //设置扬声器的状态
        setSpeakerStatus();
    }

    private void setLocalView() {
        int virtualBarHeigh = 0;
        if (Build.VERSION.SDK_INT < 28) {
            virtualBarHeigh = DisplayUtil.INSTANCE.getNavigationBarHeight(this) + DisplayUtil.INSTANCE.dp2px(20);
        } else {
            virtualBarHeigh = DisplayUtil.INSTANCE.dp2px(20);
        }
        //显示
        mLocalView.animate().translationX(0 - virtualBarHeigh).setDuration(100).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cancleNotif();

        LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
        mHandler.removeCallbacksAndMessages(null);
        setAutoRotation(this, false, "163");

        SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, false);

        reSetRenderer();
    }

    private void cancleNotif() {
        NotificationUtils.cancel(NotificationUtils.VIDEO_ID);
    }

    private void reSetRenderer() {
        Log.e("hme-video", "清空渲染器");
        //清空渲染器信息
        ViERenderer viERenderer = new ViERenderer();
        try {
            Field g_localRendererField = viERenderer.getClass().getDeclaredField("g_localRenderer");
            g_localRendererField.setAccessible(true);
            g_localRendererField.set(viERenderer, null);

            Field g_localRenderField = viERenderer.getClass().getDeclaredField("g_localRender");
            g_localRenderField.setAccessible(true);
            g_localRenderField.set(viERenderer, null);

            Field renderSysLockField = viERenderer.getClass().getDeclaredField("renderSysLock");
            renderSysLockField.setAccessible(true);
            renderSysLockField.set(viERenderer, new ReentrantLock());


            Field g_remoteRenderField = viERenderer.getClass().getDeclaredField("g_remoteRender");
            g_remoteRenderField.setAccessible(true);
            g_remoteRenderField.set(viERenderer, new SurfaceView[16]);

            Field listenThreadField = viERenderer.getClass().getDeclaredField("listenThread");
            listenThreadField.setAccessible(true);
            listenThreadField.set(viERenderer, null);

            g_localRendererField = null;
            g_localRenderField = null;
            renderSysLockField = null;
            g_remoteRenderField = null;
            listenThreadField = null;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            viERenderer = null;
        }
    }

    /**
     * 设置为扬声器
     */
    public void setLoudSpeaker() {
        //获取扬声器状态
        //如果不是扬声器则切换成扬声器
        if ((CallConstant.TYPE_LOUD_SPEAKER != CallMgr.getInstance().getCurrentAudioRoute())) {
            CallMgr.getInstance().switchAudioRoute();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ADD_LOCAL_VIEW:
                    addSurfaceView(true);
//                    setAutoRotation(thisVideoActivity, true, "184");
                    setAutoRotation(thisVideoActivity, true, "184");
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 更新状态
     */
    public void muteMic() {
        boolean currentMuteStatus = mCallFunc.isMuteStatus();
        if (CallMgr.getInstance().muteMic(mCallID, !currentMuteStatus)) {
            mCallFunc.setMuteStatus(!currentMuteStatus);
            setMicStatus();
        }
    }

    private void setMicStatus() {
        boolean currentMuteStatus = mCallFunc.isMuteStatus();
        //更新状态静音按钮状态
        tvMic.setCompoundDrawablesWithIntrinsicBounds(0, currentMuteStatus ? R.mipmap.ic_mic_close : R.mipmap.ic_mic_open, 0, 0);
    }

    /**
     * 设置扬声器的图片
     */
    private void setSpeakerStatus() {
        tvMute.setCompoundDrawablesWithIntrinsicBounds(0, isMuteSpeakStatus() ? R.mipmap.icon_mute : R.mipmap.icon_unmute, 0, 0);
    }

    public void videoToAudio() {
        CallMgr.getInstance().delVideo(mCallID);
    }

    public void holdVideo() {
        CallMgr.getInstance().holdVideoCall(mCallID);
    }

    public void videoDestroy() {
        if (null != CallMgr.getInstance().getVideoDevice()) {
            LogUtil.i(UIConstants.DEMO_TAG, "onCallClosed destroy.");
            CallMgr.getInstance().videoDestroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        holdCall(false);
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        mCameraIndex = VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA ? CallConstant.BACK_CAMERA : CallConstant.FRONT_CAMERA;
        CallMgr.getInstance().switchCamera(mCallID, mCameraIndex);
    }

    public void switchCameraStatus(boolean isCameraClose) {
        if (isCameraClose) {
            CallMgr.getInstance().closeCamera(mCallID);
        } else {
            CallMgr.getInstance().openCamera(mCallID);
        }
    }

    public SurfaceView getHideVideoView() {
        return VideoMgr.getInstance().getLocalHideView();
    }

    public SurfaceView getLocalVideoView() {
        return VideoMgr.getInstance().getLocalVideoView();
    }

    public SurfaceView getRemoteVideoView() {
        return VideoMgr.getInstance().getRemoteVideoView();
    }

    public void setAutoRotation(Object object, boolean isOpen, String line) {
        LogUtil.i(UIConstants.DEMO_TAG, "setAutoRotation-->" + line);
        VideoMgr.getInstance().setAutoRotation(object, isOpen, 1);
    }

    private void addSurfaceView(ViewGroup container, SurfaceView child) {
        if (child == null) {
            return;
        }
        if (child.getParent() != null) {
            ViewGroup vGroup = (ViewGroup) child.getParent();
            vGroup.removeAllViews();
        }
        container.addView(child);
    }

    private void addSurfaceView(boolean onlyLocal) {
        if (!onlyLocal) {
            addSurfaceView(mRemoteView, getRemoteVideoView());
        }
        addSurfaceView(mLocalView, getLocalVideoView());
        addSurfaceView(mHideView, getHideVideoView());
    }

    /**
     * On call closed.
     */
    private void callClosed() {
        LogUtil.i(UIConstants.DEMO_TAG, "onCallClosed enter.");
        videoDestroy();
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.ACTION_CALL_END:
                finishActivity();
                break;

            case CustomBroadcastConstants.ADD_LOCAL_VIEW:
                mHandler.sendEmptyMessage(ADD_LOCAL_VIEW);
                break;

            case CustomBroadcastConstants.DEL_LOCAL_VIEW:
                break;

            default:
                break;
        }
    }

    private void finishActivity() {
        //如果是主叫则发送消息
        if (mCallInfo.isCaller()) {
            Long chatTimeLong = System.currentTimeMillis() - startTimeLong;
            String chatTime = DateUtils.longToString(chatTimeLong, "mm:ss");

            //发送消息
            sendTextMsg("通话时长 " + chatTime, "通话时长 " + chatTime, mCallInfo.getPeerNumber(), true);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callClosed();
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (R.id.tv_hang_up == v.getId()) {
            //结束会议
            mCallMgr.endCall(mCallID);
        } else if (R.id.tv_mute == v.getId()) {
            //是否静音喇叭
            //true代表静音
            if (isMuteSpeakStatus()) {
                huaweiOpenSpeaker();
            } else {
                huaweiCloseSpeaker();
            }
        } else if (R.id.tv_mic == v.getId()) {
            //静音
            muteMic();
        } else if (R.id.iv_bg == v.getId()) {
            if (showControl) {
                hideControl();
            } else {
                showControl();
            }
        } else if (R.id.iv_back_operate == v.getId()) {
            switchCamera();
        } else if (R.id.conf_video_small_logo == v.getId()) {
            changeShowView();
        }
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

    private void huaweiCloseSpeaker() {
        if (0 != mCallID) {
            boolean muteSpeak = CallMgr.getInstance().muteSpeak(mCallID, true);
        }
        setSpeakerStatus();
    }

    private void huaweiOpenSpeaker() {
        if (0 != mCallID) {
            CallMgr.getInstance().muteSpeak(mCallID, false);
        }
        setSpeakerStatus();
    }

    private void showControl() {
        llTopControl.setVisibility(View.VISIBLE);
        getViewAlphaAnimator(llTopControl, 1).start();
        llBottomControl.setVisibility(View.VISIBLE);
        getViewAlphaAnimator(llBottomControl, 1).start();
    }

    private void hideControl() {
        getViewAlphaAnimator(llBottomControl, 0).start();
        getViewAlphaAnimator(llTopControl, 0).start();
    }

    private ViewPropertyAnimator getViewAlphaAnimator(final View view, final float alpha) {
        ViewPropertyAnimator viewPropertyAnimator = view.animate().alpha(alpha).setDuration(300);
        viewPropertyAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(alpha > 0 ? View.VISIBLE : View.GONE);
                showControl = alpha > 0 ? true : false;
            }
        });
        return viewPropertyAnimator;
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * 保持通话不断切换至后台
     */
    private void holdCall(boolean isBack) {
//        moveTaskToBack(true);
//        if (isBack) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        }
//        EventMsg eventMsg = new EventMsg();
//        eventMsg.setMsg(EventMsg.HOLDCALL);
//        eventMsg.setBody("video");
//        EventBus.getDefault().post(eventMsg);
    }

    private int changeNumber;

    private void changeShowView() {
        mRemoteView.removeAllViews();
        mLocalView.removeAllViews();
        changeNumber++;
        if (changeNumber % 2 == 0) {
            VideoMgr.getInstance().getRemoteVideoView().setZOrderMediaOverlay(false);
            VideoMgr.getInstance().getLocalVideoView().setZOrderMediaOverlay(true);

            addSurfaceView(mRemoteView, VideoMgr.getInstance().getRemoteVideoView());
            addSurfaceView(mLocalView, VideoMgr.getInstance().getLocalVideoView());
        } else {
            VideoMgr.getInstance().getRemoteVideoView().setZOrderMediaOverlay(true);
            VideoMgr.getInstance().getLocalVideoView().setZOrderMediaOverlay(false);

            addSurfaceView(mLocalView, VideoMgr.getInstance().getRemoteVideoView());
            addSurfaceView(mRemoteView, VideoMgr.getInstance().getLocalVideoView());
        }
    }

    private static Intent intent = new Intent(IntentConstant.VIDEO_ACTIVITY_ACTION);

    /**
     * 发送notif
     */
    private void sendNotif(final CallInfo callInfo) {
        NotificationUtils.notify(NotificationUtils.VIDEO_ID, new NotificationUtils.Func1<Void, NotificationCompat.Builder>() {
            @Override
            public Void call(NotificationCompat.Builder param) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                //判断是否是会议
                param.setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher))
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText((callInfo.isVideoCall() ? "视频" : "语音") + "通话中,点击以继续")
                        //状态栏的通知
                        .setContentIntent(pendingIntent)
                        //设置该通知的优先级
                        .setPriority(Notification.PRIORITY_HIGH)
                        //让通知右滑是否能能取消通知,默认是false
                        .setOngoing(true)
                        //浮动通知（弹窗式通知）
                        //.setFullScreenIntent(pendingIntent,true)
                        .setAutoCancel(false);
                return null;
            }
        });
    }

    /**
     * 发送消息
     *
     * @param textMsg
     * @param peerNumber
     */
    private boolean sendTextMsg(String textMsg,
                                String peerNumber) {
//        String account = LoginCenter.getInstance().getAccount();
//
//        String disPlayName = PreferenceUtil.getString(this, Constant.DISPLAY_NAME, "");
//
//        MessageReal messageReal = new MessageReal(textMsg,
//                MessageReal.TYPE_VIDEO_CALL,
//                "");
//
//        MessageBody msg = new MessageBody(
//                account,
//                disPlayName,
////                Constant.CurrDisPlayName,
//                peerNumber,
//                peerNumber,
//                messageReal,
//                MessageBody.TYPE_PERSONAL
//        );
//        return MsgIOServer.sendMsg(msg);
        return false;
    }

    /**
     * 保存消息到数据库
     *
     * @param textMsg
     * @param peerNumber
     */
    private void saveLocalMessage(String textMsg, String peerNumber) {
//        String disPlayName = PreferenceUtil.getString(this, Constant.DISPLAY_NAME, "");
//
//        ChatBean sendMsg = new ChatBean(
//                MultipleItem.SEND_VIDEO_CALL,
//                disPlayName,
////                Constant.CurrDisPlayName,
//                new Date(),
//                textMsg);
//        sendMsg.sendId = LoginCenter.getInstance().getAccount();
//        sendMsg.sendName = disPlayName;
//        sendMsg.receiveId = peerNumber;
//        sendMsg.receiveName = peerNumber;
//        sendMsg.isSend = true;
//        sendMsg.conversationId = peerNumber;//会话id
//        sendMsg.conversationUserName = peerNumber;//会话id
//
//        ChatItem chatItem = new ChatItem(sendMsg);
//
//        //保存消息列表
//        DBUtils.recordSaveIten(chatItem);
//        //保存到最近的消息列表
//        DBUtils.saveLMItem(chatItem);
//
//        //更新单个消息
//        EventMsg eventMsg = new EventMsg();
//        eventMsg.setMessageData(sendMsg);
//        eventMsg.setMsg(EventMsg.RECEIVE_SINGLE_MESSAGE);
//        EventBus.getDefault().post(eventMsg);
//
//        //更新首页消息
//        eventMsg = new EventMsg();
//        eventMsg.setMessageData(sendMsg);
//        eventMsg.setMsg(EventMsg.UPDATE_HOME);
//        EventBus.getDefault().post(eventMsg);
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

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_video;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState, @NotNull View contentView) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ivRightOperate = (ImageView) findViewById(R.id.iv_right_operate);
        mRemoteView = (FrameLayout) findViewById(R.id.conf_share_layout);
        mLocalView = (DragFrameLayout) findViewById(R.id.conf_video_small_logo);
        mHideView = (FrameLayout) findViewById(R.id.hide_video_view);
        tvHangUp = (TextView) findViewById(R.id.tv_hang_up);
        tvMic = (TextView) findViewById(R.id.tv_mic);
        tvMute = (TextView) findViewById(R.id.tv_mute);

        llTopControl = (RelativeLayout) findViewById(R.id.ll_top_control);
        llBottomControl = (LinearLayout) findViewById(R.id.ll_bottom_control);
        ivBg = (ImageView) findViewById(R.id.iv_bg);

        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
    }

    @Override
    public void doBusiness() {
        //开始时间
        startTimeLong = System.currentTimeMillis();

        //结束掉等待的对话框
        AppManager.Companion.getInstance().pushActivity(LoadingActivity.class);

        ivRightOperate.setVisibility(View.GONE);
        ivRightOperate.setImageResource(R.mipmap.icon_add);

        ivBackOperate.setVisibility(View.GONE);

        Intent intent = getIntent();

        //是否是会议
        try {
            String callInfoStringData = SPStaticUtils.getString(UIConstants.CALL_INFO);
            mCallInfo = gson.fromJson(callInfoStringData, CallInfo.class);
        } catch (Exception e) {
        }

        this.mCallID = mCallInfo.getCallID();

        mCallMgr = CallMgr.getInstance();
        mCallFunc = CallFunc.getInstance();
        instance = MeetingMgr.getInstance();

        //是否静音
        setMicStatus();

        //发送notif
        sendNotif(mCallInfo);
    }

    @Override
    public void setListeners() {
        tvHangUp.setOnClickListener(this);
        tvMic.setOnClickListener(this);
        tvMute.setOnClickListener(this);

        ivRightOperate.setOnClickListener(this);
        ivBg.setOnClickListener(this);
        ivBackOperate.setOnClickListener(this);

        mLocalView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        last = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_UP:
                        long s = System.currentTimeMillis() - last;
                        if (s < 100) {
                            changeShowView();
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onError(@NotNull String text) {

    }
}

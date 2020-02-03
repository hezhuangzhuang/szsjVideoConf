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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.huawei.opensdk.callmgr.CallConstant;
import com.huawei.opensdk.callmgr.CallInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.VideoMgr;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.ecsdk.common.UIConstants;
import com.huawei.opensdk.ecsdk.logic.CallFunc;
import com.huawei.opensdk.ecsdk.utils.IntentConstant;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.videoengine.ViERenderer;
import com.hw.baselibrary.common.AppManager;
import com.hw.baselibrary.net.RetrofitManager;
import com.hw.baselibrary.net.Urls;
import com.hw.baselibrary.rx.scheduler.CustomCompose;
import com.hw.baselibrary.ui.activity.BaseActivity;
import com.hw.baselibrary.utils.DisplayUtil;
import com.hw.baselibrary.utils.LogUtils;
import com.hw.baselibrary.utils.NotificationUtils;
import com.hw.baselibrary.utils.ToastHelper;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.huaweivclib.R;
import com.hw.huaweivclib.adapter.ConfAddSiteAdapter;
import com.hw.huaweivclib.adapter.ConfControlAdapter;
import com.hw.huaweivclib.net.ConfControlApi;
import com.hw.huaweivclib.net.respone.BaseData;
import com.hw.huaweivclib.net.respone.ConfBeanRespone;
import com.hw.huaweivclib.net.respone.ConfControlUserBean;
import com.hw.huaweivclib.service.AudioStateWatchService;
import com.hw.huaweivclib.widget.DragFrameLayout;
import com.hw.provider.huawei.commonservice.localbroadcast.CustomBroadcastConstants;
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcast;
import com.hw.provider.huawei.commonservice.localbroadcast.LocBroadcastReceiver;
import com.hw.provider.huawei.commonservice.util.LogUtil;
import com.hw.provider.net.respone.contacts.PeopleBean;
import com.hw.provider.router.RouterPath;
import com.hw.provider.router.provider.constacts.impl.ContactsModuleRouteService;
import com.hw.provider.user.UserContants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.jessyan.autosize.internal.CancelAdapt;

import static com.hw.provider.huawei.commonservice.common.LocContext.getContext;


/**
 * 视频会议界面
 */
public class VideoConfActivity extends BaseActivity implements LocBroadcastReceiver, View.OnClickListener, CancelAdapt {
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

    private ImageView ivSwitchCamera;
    private ImageView ivOneKeyCloseMic;
    private ImageView ivSwitchConfMode;

    private boolean showControl = true;//是否显示控制栏

    private String peerNumber;//会议接入号

    private String confID;//会议id
    private int callID;//会议id
    private String smcConfId;//smc的会议id，添加会场使用
    private String TAG = VideoConfActivity.class.getSimpleName();

    //会议是否是自己创建的
    private boolean isCreate = false;

    //麦克风是否静音
    private boolean isMic = true;
    //外放是否静音
    private boolean isMute = false;

    //是否是主席
    private boolean isChair = false;

    //sip账号
    private String siteUri;

    //会议模式：0表示主席模式,选看模式，1表示多画面模式
    //（1）主席模式,选看模式：所有参会会场都观看主席会场，在此模式下，任意会场可以选看任意会场
    //（2）多画面模式：所有参会会场都观看多画面，在此模式下，选看功能无效
    //多画面模式，显示广播按钮
    //选看模式，显示选看按钮
    private int confMode = -1;

    private Gson gson = new Gson();

    @Override
    protected void onResume() {
        super.onResume();

        showControl = llBottomControl.getVisibility() == View.VISIBLE && llTopControl.getVisibility() == View.VISIBLE;

        LocBroadcast.getInstance().registerBroadcast(this, mActions);
        addSurfaceView(false);

        if (!TextUtils.isEmpty(peerNumber)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //查询会议详情

                }
            }, 3 * 1000);
        }

        //是否开启画面自动旋转
        setAutoRotation(this, true, "148");

        //设置扬声器的状态
        AudioStateWatchService.suitCurrAudioDevice();

        setLocalView();

        //循环查询当前的状态
        createQueryConfInfoRetryRequest();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //取消查询当前会场的状态
        unQueryConfInfoSubscribe();
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
        try {
            unQueryConfInfoSubscribe();

            cancleNotif();

            SPStaticUtils.put(UIConstants.IS_CREATE, false);
            SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, false);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callClosed();
                }
            });

            LocBroadcast.getInstance().unRegisterBroadcast(this, mActions);
            mHandler.removeCallbacksAndMessages(null);
            setAutoRotation(this, false, "163");
            super.onDestroy();
//        MeetingMgr.getInstance().setCurrentConferenceCallID(0);
            reSetRenderer();
        } catch (Exception e) {
            LogUtils.i(getLine(245) + e.getMessage());
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
                    setAutoRotation(thisVideoActivity, true, "184");
                    break;

                default:
                    break;
            }
        }
    };


    public void videoDestroy() {
        if (null != CallMgr.getInstance().getVideoDevice()) {
            LogUtil.i(UIConstants.DEMO_TAG, "onCallClosed destroy.");
            CallMgr.getInstance().videoDestroy();

            //从会话列表中移除一路会话
            CallMgr.getInstance().removeCallSessionFromMap(callID);
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        try {
            int callID = getCallID();
            mCameraIndex = VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA ? CallConstant.BACK_CAMERA : CallConstant.FRONT_CAMERA;
            CallMgr.getInstance().switchCamera(callID, mCameraIndex);
        } catch (Exception e) {

        }
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
//        LogUtil.i(UIConstants.DEMO_TAG, "setAutoRotation-->" + line);
        VideoMgr.getInstance().setAutoRotation(object, isOpen, 1);
//        VideoMgr.getInstance().setAutoRotation(object, false, 1);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callClosed();
                        finishActivity("470");
                    }
                });
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


    /**
     * 离开会议
     */
    private void leaveConf(int line) {
        boolean isLeaveResult = false;
        int callID = getCallID();
        if (callID != 0) {
            isLeaveResult = CallMgr.getInstance().endCall(callID);
        }

        int result = MeetingMgr.getInstance().leaveConf();
        if (result != 0) {
            return;
        }
    }

    public void finishActivity(final String lineNumber) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                ToastHelper.INSTANCE.showShort(lineNumber);
                finish();
            }
        });
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

    @Override
    public void onClick(View v) {
        if (R.id.tv_hang_up == v.getId()) {
            //结束会议
            //是主席则弹出对话框
            if (isChair) {
                showExitConfDialog();
            } else {
                leaveConf(0);
            }
        } else if (R.id.tv_mute == v.getId()) {
            //是否静音喇叭
            //true代表静音
            if (TextUtils.isEmpty(smcConfId)) {

            } else {
                if (isMute) {
                    setSitesQuietRequest(false);
                } else {
                    setSitesQuietRequest(true);
                }
            }
        } else if (R.id.tv_mic == v.getId()) {
            //静音
            if (TextUtils.isEmpty(smcConfId)) {

            } else {
                //处于静音
                if (isMic) {
                    setOtherSiteMuteRequest(false);
                } else {
                    setOtherSiteMuteRequest(true);
                }
            }
        } else if (R.id.iv_bg == v.getId()) {
            if (showControl) {
                hideControl();
            } else {
                showControl();
            }
        } else if (R.id.iv_back_operate == v.getId()) {
            switchCamera();
        } else if (R.id.iv_right_operate == v.getId()) {
            if (null != allSiteList) {
                ToastHelper.INSTANCE.showShort("正在启动会议控制...");
                List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> onlineSites = getOnlineSites();
//                showConfControlDialog(onlineSites, true);
                showConfControlDialog(allSiteList, true);
            }
        } else if (R.id.conf_video_small_logo == v.getId()) {
            changeShowView();
        }//一键关闭除自己的麦克风
        else if (R.id.iv_one_key_close_mic == v.getId()) {
            StringBuilder builder = new StringBuilder();
            if (allSiteList.size() > 0) {
                for (ConfBeanRespone.DataBean.SiteStatusInfoListBean statusInfoListBean : allSiteList) {
                    if (!statusInfoListBean.siteUri.equals(siteUri)) {
                        builder.append(statusInfoListBean.siteUri).append(",");
                    }
                }
            }
            //一键关闭所有麦克风
//            oneKeyCloseMic(!isAllMicClose, builder.toString());
            oneKeyCloseMic(isAllMicClose, builder.toString());
        }//切换模式
        else if (R.id.iv_switch_conf_mode == v.getId()) {
            switchConfMode();
        }
    }

    private int changeNumber;

    /**
     * 切换显示模式
     */
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

    //是否处于一键静音状态
    private boolean isAllMicClose = true;

    /**
     * 麦克风闭音
     */
    private void oneKeyCloseMic(final boolean isMicParam, String siteUri) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .setSiteMute(smcConfId, siteUri, String.valueOf(isMicParam))
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            ToastHelper.INSTANCE.showShort(isMicParam ? "静音成功" : "取消静音成功");
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(557) + throwable.getMessage());
                    }
                });
    }

    /**
     * 离开会议的网络请求
     */
    private void leaveConfRequest() {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .leaveConf(smcConfId, siteUri)
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            SPStaticUtils.put(UIConstants.IS_CREATE, false);
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(582) + throwable.getMessage());
                    }
                });
    }

    /**
     * 麦克风闭音
     */
    private void setOtherSiteMuteRequest(final boolean isMicParam) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .setSiteMute(smcConfId, siteUri, String.valueOf(isMicParam))
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
                        ToastHelper.INSTANCE.showShort(getLine(614) + throwable.getMessage());
                    }
                });
    }

    /**
     * 外放闭音
     */
    private void setSitesQuietRequest(final boolean isMuteParam) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .setSitesQuiet(smcConfId, siteUri, String.valueOf(isMuteParam))
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
                        ToastHelper.INSTANCE.showShort(getLine(646) + throwable.getMessage());
                    }
                });
    }

    private int getLine(int i) {
        return i;
    }

    /**
     * 切换会议模式
     */
    private void switchConfMode() {
        final int mode = 0 == confMode ? 1 : 0;

        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .changeConfMode(smcConfId, String.valueOf(mode))
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            ToastHelper.INSTANCE.showShort(mode == 0 ? "设置选看模式成功" : "设置主席模式成功");
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(677) + throwable.getMessage());
                    }
                });
    }

    /**
     * true代表静音
     *
     * @return
     */
    private boolean isMuteSpeakStatus() {
        int currentConferenceCallID = getCallID();
        if (0 != currentConferenceCallID) {
            return CallMgr.getInstance().getMuteSpeakStatus(currentConferenceCallID);
        } else {
            return false;
        }
    }

    private int getCallID() {
        if (-1 != callID) {
            return callID;
        } else {
            return MeetingMgr.getInstance().getCurrentConferenceCallID();
        }
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
                view.setVisibility(alpha > 0 ? View.VISIBLE : View.INVISIBLE);
                showControl = alpha > 0 ? true : false;
            }
        });
        return viewPropertyAnimator;
    }


    private List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> allSiteList = new ArrayList<>();

    /**
     * 获取在线的会场
     *
     * @param siteStatusInfoList
     */
    private List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> getOnlineSites() {
        List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> siteList = new ArrayList<>();
        for (ConfBeanRespone.DataBean.SiteStatusInfoListBean siteBean : allSiteList) {
            if (2 == siteBean.siteStatus) {
                siteList.add(siteBean);
            }
        }
        return siteList;
    }

    /**
     * 获取在线的会场并剔除自己
     *
     * @param siteStatusInfoList
     */
    private List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> getOnlineSitesRemoveSelf() {
        List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> siteList = new ArrayList<>();
        for (ConfBeanRespone.DataBean.SiteStatusInfoListBean siteBean : allSiteList) {
            if (2 == siteBean.siteStatus && !siteBean.siteUri.equals(getCurrentSiteUri())) {
                siteList.add(siteBean);
            }
        }
        return siteList;
    }

    /************************添加会场-start*****************************/
    private BottomSheetDialog addAddtendDialog;
    private TextView tvAddCancle;
    private TextView tvAddConfirm;

    private RecyclerView rvAddAttendees;
    private ConfAddSiteAdapter addSiteAdapter;
    /*添加会场--end*/

    /**
     * 显示添加对话框
     */
    private void showAddAddtendDialog(List<ConfControlUserBean> data) {
        final List<ConfControlUserBean> selectUser = new ArrayList<>();

        WindowManager wm = this.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();

        //构造函数的第二个参数可以设置BottomSheetDialog的主题样式
        //mBottomSheetDialog = new BottomSheetDialog(this,R.style.MyBottomDialog);
        addAddtendDialog = new BottomSheetDialog(this);
        //导入底部reycler布局
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_site, null, false);

        rvAddAttendees = view.findViewById(R.id.rv_add_attendees);

        addSiteAdapter = new ConfAddSiteAdapter(R.layout.item_conf_add_site, data);
        addSiteAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ConfControlUserBean userBean = addSiteAdapter.getItem(position);
                //判断是否被选中
                userBean.isCheck = !userBean.isCheck;
                if (userBean.isCheck) {
                    selectUser.add(userBean);
                } else {
                    selectUser.remove(userBean);
                }
                addSiteAdapter.notifyItemChanged(position);
            }
        });

        rvAddAttendees.setLayoutManager(new LinearLayoutManager(this));
        rvAddAttendees.setAdapter(addSiteAdapter);

        tvAddCancle = view.findViewById(R.id.tv_add_cancle);
        tvAddConfirm = view.findViewById(R.id.tv_add_confirm);

        View speaceHolder = view.findViewById(R.id.view_speaceHolder);

        //配置点击外部区域消失
        speaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddtendDialog.dismiss();
            }
        });

        tvAddCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddtendDialog.dismiss();
            }
        });

        tvAddConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectUser.size() <= 0) {
                    ToastHelper.INSTANCE.showShort("请选择需要添加的会场");
                    return;
                }

                StringBuilder siteUri = new StringBuilder();

                for (int i = 0; i < selectUser.size(); i++) {
                    if (i == selectUser.size() - 1) {
                        siteUri.append(selectUser.get(i).sip);
                    } else {
                        siteUri.append(selectUser.get(i).sip + ",");
                    }
                }
                //添加会场
                addSiteToConf(siteUri.toString());
                addAddtendDialog.dismiss();
            }
        });

        addAddtendDialog.setContentView(view);
        try {
            // hack bg color of the BottomSheetDialog
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.setBackgroundColor(ContextCompat.getColor(this, R.color.tran));
        } catch (Exception e) {
            e.printStackTrace();
        }

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
        //设置默认弹出高度为屏幕的0.4倍
        //mBehavior.setPeekHeight((int) (0.4 * height));
        mBehavior.setPeekHeight((int) (height));

        //设置点击dialog外部不消失
        addAddtendDialog.setCanceledOnTouchOutside(false);
        addAddtendDialog.setCancelable(false);

        if (!addAddtendDialog.isShowing()) {
            addAddtendDialog.show();
        } else {
            addAddtendDialog.dismiss();
        }
    }

    /**
     * 添加会场
     */
    private void addSiteToConf(String siteUris) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .addSiteToConf(smcConfId, siteUris)
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            ToastHelper.INSTANCE.showShort("添加用户成功");
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(883) + throwable.getMessage());
                    }
                });
    }
    /************************添加会场-end*****************************/

    /************************会控对话框-start*****************************/
    /*会控界面-start*/
    private BottomSheetDialog confControlDialog;
    private TextView tvControlCancel;
    private TextView tvControlConfirm;

    private RecyclerView rvControl;
    private ConfControlAdapter confControlAdapter;

    //最后控制的会场
    private int controlPosition = -1;

    //正在查看的会场
    private String watchUri = "";
    /*会控界面--end*/
    /************************会控对话框-end*****************************/

    /**
     * 显示会控的对话框
     */
    private void showConfControlDialog(List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> siteList, boolean isDisPlay) {

        if (null != confControlDialog) {
            //刷新数据
            confControlAdapter.replaceData(siteList);
        } else {
            initConfControlDialog(siteList);
        }

        //设置添添加人员按钮是否显示
        tvControlConfirm.setVisibility(isChair ? View.VISIBLE : View.INVISIBLE);
        confControlAdapter.setConfMode(confMode);
        confControlAdapter.setChair(isChair);

        confControlDialog.show();
    }

    private void initConfControlDialog(List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> siteList) {
        //初始化适配器
        initConfControlAdapter(siteList);

        //构造函数的第二个参数可以设置BottomSheetDialog的主题样式
        confControlDialog = new BottomSheetDialog(this);
        //导入底部reycler布局
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_site, null, false);

        rvControl = view.findViewById(R.id.rv_add_attendees);
        rvControl.setLayoutManager(new LinearLayoutManager(this));
        rvControl.setAdapter(confControlAdapter);

        tvControlCancel = view.findViewById(R.id.tv_add_cancle);
        tvControlConfirm = view.findViewById(R.id.tv_add_confirm);
        TextView tvLable = view.findViewById(R.id.tv_lable);
        tvLable.setText("与会列表");

        View speaceHolder = view.findViewById(R.id.view_speaceHolder);

        //配置点击外部区域消失
        speaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confControlDialog.dismiss();
            }
        });

        tvControlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confControlDialog.dismiss();
            }
        });

        tvControlConfirm.setText("添加用户");

        tvControlConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取所有会场
                getAllPeople();
            }
        });

        confControlDialog.setContentView(view);
        try {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.setBackgroundColor(ContextCompat.getColor(this, R.color.tran));
        } catch (Exception e) {
            e.printStackTrace();
        }

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
        //设置默认弹出高度为屏幕的0.4倍
        //mBehavior.setPeekHeight((int) (0.4 * height));
        mBehavior.setPeekHeight((int) (DisplayUtil.INSTANCE.getScreenHeight()));

        //设置点击dialog外部不消失
        confControlDialog.setCanceledOnTouchOutside(false);
        confControlDialog.setCancelable(false);
    }

    /**
     * 获取所有的会场
     */
    private void getAllPeople() {
        ContactsModuleRouteService.INSTANCE.getAllPeople()
                .subscribe(new Consumer<com.hw.baselibrary.common.BaseData<PeopleBean>>() {
                    @Override
                    public void accept(com.hw.baselibrary.common.BaseData<PeopleBean> baseData) throws Exception {
                        //关闭会控界面
                        confControlDialog.dismiss();

                        //获取会场
                        List<PeopleBean> list = baseData.getData();
                        List<ConfControlUserBean> data = new ArrayList<>();
                        for (PeopleBean temp : list) {
                            data.add(new ConfControlUserBean(temp.getId(), temp.getName(), temp.getSip(), 0, false));
                        }
                        showAddAddtendDialog(data);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(1009) + throwable.getMessage());
                    }
                });
    }

    /**
     * 初始化会控适配器
     *
     * @param siteList
     */
    private void initConfControlAdapter(List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> siteList) {
        confControlAdapter = new ConfControlAdapter(R.layout.item_conf_control, siteList);
        confControlAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ConfBeanRespone.DataBean.SiteStatusInfoListBean site = confControlAdapter.getItem(position);

                int id = view.getId();
                if (id == R.id.iv_watch_site) {
                    controlPosition = position;
                    setWatchSite(site.siteUri, true);
                } else if (id == R.id.iv_broadcast) {
                    controlPosition = position;
                    setSiteBroadcastRequest(!(site.broadcastStatus == 1), site.siteUri);
                } else if (id == R.id.iv_mic) {
                    controlPosition = position;
                    if (site.siteStatus == 2) {
                        setOtherSiteMuteRequest(true, site.siteUri);
                    }
                } else if (id == R.id.iv_louder) {
                    controlPosition = position;
                    if (site.siteStatus == 2) {
                        setOtherSitesQuietRequest(site.loudspeakerStatus == 1, site.siteUri);
                    }
                } else if (id == R.id.iv_hangup) {
                    controlPosition = position;
                    if (site.siteStatus == 2) {
                        setSiteDisconnectRequest(site.siteUri);
                    } else {
                        setSiteCallRequest(site.siteUri);
                    }
                }
            }
        });
    }

    /**
     * 静音其他会场麦克风
     *
     * @param isMicParam   true：静音
     * @param otherSiteUri 静音的会场号码
     */
    private void setOtherSiteMuteRequest(final boolean isMicParam, final String otherSiteUri) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .setSiteMute(smcConfId, otherSiteUri, String.valueOf(isMicParam))
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            //如果静音是的自己
                            if (otherSiteUri.equals(siteUri)) {
                                if (isMicParam) {
                                    //静音成功
                                    tvMic.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_mic_close, 0, 0);
                                } else {
                                    //取消静音成功
                                    tvMic.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_mic_open, 0, 0);
                                }
                                isMic = isMicParam;
                            }
                            //刷新列表
                            confControlAdapter.getItem(controlPosition).microphoneStatus = isMicParam ? 0 : 1;
                            confControlAdapter.notifyDataSetChanged();
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(1093) + throwable.getMessage());
                    }
                });
    }

    /**
     * 外放闭音其他会场
     */
    private void setOtherSitesQuietRequest(final boolean isMuteParam, final String otherSiteUri) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .setSitesQuiet(smcConfId, otherSiteUri, String.valueOf(isMuteParam))
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            if (otherSiteUri.equals(siteUri)) {
                                if (isMuteParam) {
                                    //静音成功
                                    tvMute.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_mute, 0, 0);
                                } else {
                                    //取消静音成功
                                    tvMute.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.icon_unmute, 0, 0);
                                }
                                isMute = isMuteParam;
                            }
                            //刷新列表
                            confControlAdapter.getItem(controlPosition).loudspeakerStatus = isMuteParam ? 0 : 1;
                            confControlAdapter.notifyDataSetChanged();
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(1130) + throwable.getMessage());
                    }
                });
    }

    /**
     * 广播会场
     *
     * @param isBroadcast  true：广播
     * @param otherSiteUri 广播的会场号码
     *                     confAction_setBroadcastSite.action
     */
    private void setSiteBroadcastRequest(final boolean isBroadcast, final String otherSiteUri) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .setBroadcastSite(smcConfId, otherSiteUri, String.valueOf(isBroadcast))
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            for (int i = 0; i < confControlAdapter.getItemCount(); i++) {
                                confControlAdapter.getItem(i).broadcastStatus = 0;
                            }
                            //刷新列表
                            confControlAdapter.getItem(controlPosition).broadcastStatus = isBroadcast ? 0 : 1;
                            confControlAdapter.notifyDataSetChanged();
                            ToastHelper.INSTANCE.showShort(isBroadcast ? "广播会场成功" : "取消广播会场成功");
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(1165) + throwable.getMessage());
                    }
                });
    }

    /**
     * 呼叫会场
     *
     * @param otherSiteUri 呼叫的会场号码
     *                     connectSite
     */
    private void setSiteCallRequest(String otherSiteUri) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .connectSite(smcConfId, otherSiteUri)
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            ToastHelper.INSTANCE.showShort("呼叫会场成功");
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(1193) + throwable.getMessage());
                    }
                });
    }

    /**
     * 选看会场
     *
     * @param otherSiteUri 选看会场
     *                     connectSite
     */
    private void setWatchSite(String otherSiteUri, boolean showToast) {
        if (0 != confMode) {
            return;
        }
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .setVideoSource(smcConfId, siteUri, otherSiteUri)
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            //临时解决方案
                            //清空所有标记
                            List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> data = allSiteList;
                            for (ConfBeanRespone.DataBean.SiteStatusInfoListBean temp : data) {
                                if (temp.siteUri.equals(siteUri)) {
                                    temp.isWatch = true;
                                } else {
                                    temp.isWatch = false;
                                }
                            }
                            watchUri = siteUri;
                            if (null != confControlDialog && confControlDialog.isShowing() && null != confControlAdapter) {
                                confControlAdapter.notifyDataSetChanged();
                            }
                        } else {
//                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(1235) + throwable.getMessage());
                    }
                });
    }

    /**
     * 挂断
     *
     * @param otherSiteUri 挂断的会场号码
     *                     connectSite
     */
    private void setSiteDisconnectRequest(String otherSiteUri) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .leaveConf(smcConfId, otherSiteUri)
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            ToastHelper.INSTANCE.showShort("挂断会场成功");
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(1263) + throwable.getMessage());
                    }
                });
    }

    /**
     * 结束会议
     */
    private void endConfReuqest() {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .stopConf(smcConfId)
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            ToastHelper.INSTANCE.showShort("结束会议成功");
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(getLine(1288) + throwable.getMessage());
                    }
                });
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * 获取当前账号的号码
     *
     * @return
     */
    private String getCurrentSiteUri() {
        return LoginCenter.getInstance().getSipAccountInfo().getTerminal();
    }

    private MaterialDialog exitConfDialog;

    /**
     * 显示结束会议的对话框
     */
    private void showExitConfDialog() {
        if (null == exitConfDialog) {
            initEndConfDialog();
        }
        exitConfDialog.show();
    }

    /**
     * 初始化结束会议对话框
     */
    private void initEndConfDialog() {
        View view = View.inflate(this, R.layout.dialog_exit_conf, null);
        RelativeLayout rlContent = (RelativeLayout) view.findViewById(R.id.rl_content);
        ImageView ivClose = (ImageView) view.findViewById(R.id.ic_close);
        TextView tvEndConf = (TextView) view.findViewById(R.id.tv_end_conf);
        TextView tvLeaveConf = (TextView) view.findViewById(R.id.tv_leave_conf);
        TextView tvAppointChair = (TextView) view.findViewById(R.id.tv_appoint_chair);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitConfDialog.dismiss();
            }
        });

        tvEndConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endConfReuqest();
                exitConfDialog.dismiss();
            }
        });

        tvLeaveConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveConfRequest();
                exitConfDialog.dismiss();
            }
        });

        tvAppointChair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAppointChairActivity();
                exitConfDialog.dismiss();
            }
        });

        exitConfDialog = new MaterialDialog.Builder(this)
                .customView(view, false)
                .build();

        Window window = exitConfDialog.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏的flag
        window.setBackgroundDrawableResource(android.R.color.transparent); //设置window背景透明
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.8f;
        lp.dimAmount = 0.1f; //dimAmount在0.0f和1.0f之间，0.0f完全不暗，1.0f全暗
        window.setAttributes(lp);
    }

    private int REQUEST_CODE = 0;

    /**
     * 指定主席
     */
    private void startAppointChairActivity() {
        if (getOnlineSitesRemoveSelf().isEmpty()) {
            ToastHelper.INSTANCE.showShort("当前没有在线会场");
            return;
        }
        //跳转到指定主席界面
        ARouter.getInstance().build(RouterPath.Huawei.CHAIR_SELECT)
                .withString(RouterPath.Huawei.FILED_SMC_CONF_ID, smcConfId)
                .withSerializable(RouterPath.Huawei.FILED_ONLINE_LIST, (Serializable) getOnlineSitesRemoveSelf())
                .navigation();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private Disposable queryConfInfoSubscribe;

    private void createQueryConfInfoRetryRequest() {
        unQueryConfInfoSubscribe();

        queryConfInfoSubscribe = Observable.interval(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        intervalQueryConfInfo();
                    }
                });
    }

    private void unQueryConfInfoSubscribe() {
        if (null != queryConfInfoSubscribe) {
            if (!queryConfInfoSubscribe.isDisposed()) {
                queryConfInfoSubscribe.dispose();
                queryConfInfoSubscribe = null;
            }
        }
    }

    /**
     * 循环查询状态
     */
    private void intervalQueryConfInfo() {
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
                            //是否是会议主席
                            boolean isConfChair = !TextUtils.isEmpty(baseData.data.chairUri) && baseData.data.chairUri.equals(siteUri);

                            //会议模式
                            confMode = baseData.data.confMode;

                            //会议中已存在的人员
                            allSiteList.clear();
                            allSiteList.addAll(baseData.data.siteStatusInfoList);

                            //如果是主席模式
                            if (confMode == 0) {
                                if (TextUtils.isEmpty(baseData.data.chairUri)) {
                                    setWatchSite(baseData.data.creatorUri, false);
                                } else {
                                    setWatchSite(baseData.data.chairUri, false);
                                }
                            }

                            //判断是否是主席
                            if (isConfChair || isCreate) {
                                //等于主席
                                isChair = true;
                                ivSwitchConfMode.setVisibility(View.VISIBLE);
                                //0表示主席模式，1表示多画面模式
                                ivSwitchConfMode.setImageResource(0 == confMode ? R.mipmap.ic_discuss_close : R.mipmap.ic_discuss_open);
                            } else {
                                isChair = false;
                                ivSwitchConfMode.setVisibility(View.GONE);
                            }

                            //如果是主席显示一键关闭麦克风按钮
                            //ivOneKeyCloseMic.setVisibility(isChair ? View.VISIBLE : View.GONE);

                            //判断是否是本人
                            for (ConfBeanRespone.DataBean.SiteStatusInfoListBean siteBean : baseData.data.siteStatusInfoList) {
                                if (siteBean.siteUri.equals(siteUri)) {
                                    tvMic.setCompoundDrawablesWithIntrinsicBounds(0, 1 == siteBean.microphoneStatus ? R.mipmap.ic_mic_open : R.mipmap.ic_mic_close, 0, 0);
                                    tvMute.setCompoundDrawablesWithIntrinsicBounds(0, 1 == siteBean.loudspeakerStatus ? R.mipmap.icon_unmute : R.mipmap.icon_mute, 0, 0);
                                    //得到正在观看的画面
                                    watchUri = siteBean.videoSourceUri;
                                    break;
                                }
                            }

                            for (ConfBeanRespone.DataBean.SiteStatusInfoListBean temp : baseData.data.siteStatusInfoList) {
                                if (temp.microphoneStatus == 1 && !temp.siteUri.equals(siteUri)) {
                                    ivOneKeyCloseMic.setImageResource(R.mipmap.ic_open_all_mic);
                                    return;
                                }
                            }
                            ivOneKeyCloseMic.setImageResource(R.mipmap.ic_close_all_mic);

                            //如果会控对话框显示则刷新列表
                            if (null != confControlDialog && confControlDialog.isShowing()) {
                                //刷新列表
                                confControlAdapter.replaceData(allSiteList);
                            }
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        ToastHelper.INSTANCE.showShort(getLine(1495) + throwable.getMessage());
                    }
                });
    }

    private static Intent intent = new Intent(IntentConstant.VIDEO_CONF_ACTIVITY_ACTION);

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
        NotificationUtils.cancel(NotificationUtils.VIDEO_ID);
    }


    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_video_conf;
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

        ivSwitchCamera = (ImageView) findViewById(R.id.iv_back_operate);

        ivOneKeyCloseMic = (ImageView) findViewById(R.id.iv_one_key_close_mic);
        ivSwitchConfMode = (ImageView) findViewById(R.id.iv_switch_conf_mode);
    }

    @Override
    public void doBusiness() {
        //结束掉等待的对话框
        AppManager.Companion.getInstance().pushActivity(LoadingActivity.class);

        ivRightOperate.setVisibility(View.VISIBLE);
        //可能需要删除的添加图标
        ivRightOperate.setImageResource(R.mipmap.icon_add);

        ivSwitchCamera.setVisibility(View.GONE);

        Intent intent = getIntent();

        mCallInfo = gson.fromJson(SPStaticUtils.getString(UIConstants.CALL_INFO), CallInfo.class);
        LogUtils.i(mCallInfo.toString());
        isCreate = SPStaticUtils.getBoolean(UIConstants.IS_CREATE, false);

        siteUri = SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT);

        confID = intent.getStringExtra(UIConstants.CONF_ID);
        callID = intent.getIntExtra(UIConstants.CALL_ID, -1);
        peerNumber = intent.getStringExtra(UIConstants.PEER_NUMBER);

        this.mCallID = mCallInfo.getCallID();

        mCallMgr = CallMgr.getInstance();
        mCallFunc = CallFunc.getInstance();
        instance = MeetingMgr.getInstance();

        //请求主席
        if (isCreate) {
            ivOneKeyCloseMic.setVisibility(View.GONE);

            isChair = true;
        }

        sendNotif(mCallInfo);
    }

    //最后点击的时间
    private long last = 0;

    @Override
    public void setListeners() {
        tvHangUp.setOnClickListener(this);
        tvMic.setOnClickListener(this);
        tvMute.setOnClickListener(this);

        ivRightOperate.setOnClickListener(this);
        ivBg.setOnClickListener(this);
        ivSwitchCamera.setOnClickListener(this);
        ivOneKeyCloseMic.setOnClickListener(this);
        ivRightOperate.setOnClickListener(this);
        ivSwitchConfMode.setOnClickListener(this);

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


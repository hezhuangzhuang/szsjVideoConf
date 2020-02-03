package com.hw.huaweivclib.inter;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.ecsdk.common.UIConstants;
import com.huawei.opensdk.sdkwrapper.login.LoginCenter;
import com.huawei.opensdk.sdkwrapper.login.LoginStatus;
import com.huawei.utils.DeviceManager;
import com.hw.baselibrary.common.AppManager;
import com.hw.baselibrary.common.BaseApp;
import com.hw.baselibrary.net.RetrofitManager;
import com.hw.baselibrary.net.Urls;
import com.hw.baselibrary.rx.scheduler.CustomCompose;
import com.hw.baselibrary.utils.LogUtils;
import com.hw.baselibrary.utils.ToastHelper;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.huaweivclib.activity.LoadingActivity;
import com.hw.huaweivclib.arouter.HuaweiModuleServiceImp;
import com.hw.huaweivclib.net.ConfControlApi;
import com.hw.huaweivclib.net.respone.BaseData;
import com.hw.huaweivclib.net.respone.CreateConfResponeBean;
import com.hw.provider.huawei.commonservice.util.LogUtil;
import com.hw.provider.router.provider.huawei.impl.HuaweiModuleService;
import com.hw.provider.user.UserContants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.RequestBody;

/**
 * author：pc-20171125
 * data:2020/1/15 19:37
 * 0：未注册
 * <p>
 * 1：注册中
 * <p>
 * 2：注销中
 * <p>
 * 3：已注册
 * <p>
 * 4：无效状态
 */
public class HuaweiCallImp {
    /**
     * 获取登录状态
     *
     * @return
     */
    public static boolean getLoginStatus() {
        if (null != LoginCenter.getInstance() && null != LoginCenter.getInstance().getLoginStatus()) {
            return 3 == LoginCenter.getInstance().getLoginStatus().getCallResult().getRegState();
        } else {
            return false;
        }
    }

    /**
     * 判断是否需要登录
     */
    public static void hasLogin() {
        if (!getLoginStatus()) {
            HuaweiLoginImp.login(
                    SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT),
                    SPStaticUtils.getString(UserContants.HUAWEI_PWD),
                    SPStaticUtils.getString(UserContants.HUAWEI_SMC_IP),
                    SPStaticUtils.getString(UserContants.HUAWEI_SMC_PORT)
            );
        }
    }

    /**
     * 点对点呼叫
     *
     * @param siteNumber  呼叫的号码
     * @param isVideoCall true:视频,false：音频
     * @return
     */
    public static int callSite(String siteNumber, boolean isVideoCall) {
        if (!DeviceManager.isNetworkAvailable(BaseApp.context)) {
            ToastHelper.INSTANCE.showShort("请检查您的网络");
            return -1;
        }

        //是否需要登录
        hasLogin();

        return CallMgr.getInstance().startCall(siteNumber, isVideoCall);
    }

    public static int joinConf(String accessCode) {
        if (!DeviceManager.isNetworkAvailable(BaseApp.context)) {
            ToastHelper.INSTANCE.showShort("请检查您的网络");
            return -1;
        }

        //是否需要登录
        hasLogin();

        //是否需要自动接听
        SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, true);

        //是否是加入会议
        SPStaticUtils.put(UIConstants.JOIN_CONF, true);

        //显示等待界面
//        LoadingActivity.startActivty(BaseApp.context, accessCode);
        return CallMgr.getInstance().startCall(accessCode, true);
    }

    /**
     * 加入会议
     *
     * @param accessCode
     * @return
     */
    public static boolean joinConfNetWork(@NotNull String smcConfId, @NotNull String siteUri) {
        if (!DeviceManager.isNetworkAvailable(BaseApp.context)) {
            ToastHelper.INSTANCE.showShort("请检查您的网络");
            return false;
        }

        //是否需要登录
        hasLogin();

        //是否需要自动接听
        SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, true);

        //是否是加入会议
        SPStaticUtils.put(UIConstants.JOIN_CONF, true);

        final boolean[] isSuccess = {false};

        Disposable subscribe = RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.BASE_URL)
                .joinConf(smcConfId, siteUri)
                .compose(new CustomCompose<BaseData>())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        LoadingActivity.startActivty(BaseApp.context, "");
                        isSuccess[0] = true;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isSuccess[0] = false;

                        //是否需要自动接听
                        SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, false);

                        //是否是加入会议
                        SPStaticUtils.put(UIConstants.JOIN_CONF, false);
                    }
                });

        return isSuccess[0];
    }


    /**
     * 创建即时会议
     *
     * @param confName      会议名称
     * @param duration      会议时长，单位(分钟)
     * @param memberSipList 参会人员的sip号码，多个以逗号分隔
     * @param groupId
     * @param accessCode    会议接入码
     * @param type          0：语音会议，1：视频会议
     */
    public static void createConfNetWork(String confName,
                                         String duration,
                                         String accessCode,
                                         String memberSipList,
                                         String groupId,
                                         int type) {
        if (!DeviceManager.isNetworkAvailable(BaseApp.context)) {
            ToastHelper.INSTANCE.showShort("请检查您的网络");
            return;
        }

        //是否需要登录
        hasLogin();

        String createUri = SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT);

        //是否自己创建的会议
        SPStaticUtils.put(UIConstants.IS_CREATE, true);

        //是否需要自动接听
        SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, true);

        //显示等待界面
        LoadingActivity.startActivty(BaseApp.context, confName);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("confName", confName);
            jsonObject.put("duration", duration);
            jsonObject.put("accessCode", accessCode);
            jsonObject.put("sites", memberSipList);
            jsonObject.put("creatorUri", createUri);
            jsonObject.put("groupId", groupId);
            jsonObject.put("confMediaType", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(Urls.INSTANCE.getMEDIA_TYPE(), jsonObject.toString());

        RetrofitManager.INSTANCE.create(ConfControlApi.class,
                Urls.INSTANCE.getFILE_URL()
        )
                .createConf(body)
                .compose(new CustomCompose())
                .subscribe(new Consumer<CreateConfResponeBean>() {
                    @Override
                    public void accept(CreateConfResponeBean baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            ToastHelper.INSTANCE.showShort("会议召集成功");
                        } else {
                            AppManager.Companion.getInstance().pushActivity(LoadingActivity.class);
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        AppManager.Companion.getInstance().pushActivity(LoadingActivity.class);
                        ToastHelper.INSTANCE.showShort(throwable.getMessage());
                    }
                });
    }


    /**
     * 预约会议
     *
     * @param confName      会议名称
     * @param duration      会议时长，单位(分钟)
     * @param memberSipList 参会人员的sip号码，多个以逗号分隔
     * @param groupId
     * @param accessCode    会议接入码
     * @param type          0：语音会议，1：视频会议
     * @param confType      0：即时会议，1：预约会议
     * @param startTime     会议开始时间
     */
    public static boolean reservedConfNetWork(String confName,
                                              String duration,
                                              String accessCode,
                                              String memberSipList,
                                              String groupId,
                                              int type,
                                              String confType,
                                              String startTime) {
        if (!DeviceManager.isNetworkAvailable(BaseApp.context)) {
            ToastHelper.INSTANCE.showShort("请检查您的网络");
            return false;
        }

        //是否需要登录
        hasLogin();

        String createUri = SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT);

//        //是否自己创建的会议
//        SPStaticUtils.put(UIConstants.IS_CREATE, true);
//
//        //是否需要自动接听
//        SPStaticUtils.put(UIConstants.IS_AUTO_ANSWER, true);

        //显示等待界面
//        LoadingActivity.startActivty(BaseApp.context, confName);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("confName", confName);
            jsonObject.put("duration", duration);
            jsonObject.put("accessCode", accessCode);
            jsonObject.put("sites", memberSipList);
            jsonObject.put("creatorUri", createUri);
            jsonObject.put("groupId", groupId);
            jsonObject.put("confMediaType", type);
            jsonObject.put("confType", confType);
            jsonObject.put("startTime", startTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(Urls.INSTANCE.getMEDIA_TYPE(), jsonObject.toString());

        //是否预约成功
        final boolean[] isReservedSuccess = {false};

        RetrofitManager.INSTANCE.create(ConfControlApi.class,
                Urls.INSTANCE.getFILE_URL()
        )
                .createConf(body)
                .compose(new CustomCompose())
                .subscribe(new Consumer<CreateConfResponeBean>() {
                    @Override
                    public void accept(CreateConfResponeBean baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            ToastHelper.INSTANCE.showShort("会议预约成功");
//                            AppManager.Companion.getInstance().pushActivity(LoadingActivity.class);
                            isReservedSuccess[0] = true;
                        } else {
                            ToastHelper.INSTANCE.showShort("会议预约失败");
//                            AppManager.Companion.getInstance().pushActivity(LoadingActivity.class);
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                            isReservedSuccess[0] = false;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort("会议预约失败");
//                        AppManager.Companion.getInstance().pushActivity(LoadingActivity.class);
                        ToastHelper.INSTANCE.showShort(throwable.getMessage());
                        isReservedSuccess[0] = false;
                    }
                });
        return isReservedSuccess[0];
    }
}

package com.huawei.opensdk.esdk.login;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.huawei.ecterminalsdk.base.TsdkContactsInfo;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.esdk.common.IntentConstant;
import com.huawei.opensdk.esdk.common.UIConstants;
import com.huawei.opensdk.esdk.utils.ActivityUtil;
import com.huawei.opensdk.loginmgr.ILoginEventNotifyUI;
import com.huawei.opensdk.loginmgr.LoginConstant;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.List;


public class LoginFunc implements ILoginEventNotifyUI, LocBroadcastReceiver {
    private static final int VOIP_LOGIN_SUCCESS = 100;
    private static final int IM_LOGIN_SUCCESS = 101;
    private static final int LOGIN_FAILED = 102;
    private static final int LOGOUT = 103;
    private static final int FIREWALL_DETECT_FAILED = 104;
    private static final int BUILD_STG_FAILED = 105;

    private static LoginFunc INSTANCE = new LoginFunc();

    private String[] broadcastNames = new String[]{CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_RESULT};

    private LoginFunc() {
        LocBroadcast.getInstance().registerBroadcast(this, broadcastNames);
    }

    public static ILoginEventNotifyUI getInstance() {
        return INSTANCE;
    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            LogUtil.i(UIConstants.DEMO_TAG, "what:" + msg.what);
            parallelHandleMessage(msg);
        }
    };

    private void sendHandlerMessage(int what, Object object) {
        if (mMainHandler == null) {
            LogUtil.i(UIConstants.DEMO_TAG, "mMainHandler == null");
            return;
        }
        Message msg = mMainHandler.obtainMessage(what, object);
        mMainHandler.sendMessage(msg);
    }

    @Override
    public void onLoginEventNotify(LoginConstant.LoginUIEvent evt, int reason, String description) {
        switch (evt) {
            case VOIP_LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "voip login success");
                sendHandlerMessage(VOIP_LOGIN_SUCCESS, description);
                break;
            case IM_LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "im login success");
                sendHandlerMessage(IM_LOGIN_SUCCESS, description);
                break;
            case LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login fail");
                sendHandlerMessage(LOGIN_FAILED, description);
                break;
            case FIREWALL_DETECT_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "firewall detect fail");
                sendHandlerMessage(FIREWALL_DETECT_FAILED, description);
                break;
            case BUILD_STG_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "build stg fail");
                sendHandlerMessage(BUILD_STG_FAILED, description);
                break;

            case LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout");
                sendHandlerMessage(LOGOUT, description);
                break;
            default:
                break;
        }
    }

    /**
     * handle msg
     *
     * @param msg
     */
    private void parallelHandleMessage(Message msg) {
        switch (msg.what) {
            case VOIP_LOGIN_SUCCESS:
                LogUtil.i(UIConstants.DEMO_TAG, "voip login success,notify UI!");
//                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
//                ActivityUtil.startActivity(LocContext.getContext(), IntentConstant.MAIN_ACTIVITY_ACTION);
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_SUCCESS, null);
                //CallMgr.getInstance().addDefaultAudioRoute();
//                Executors.newSingleThreadExecutor().execute(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        EnterpriseAddressBookMgr.getInstance().searchSelfInfo(LoginMgr.getInstance().getAccount());
//                    }
//                });
                break;

            case LOGIN_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "login failed,notify UI!");
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGIN_FAILED, msg.obj);
//                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;

            case LOGOUT:
                LogUtil.i(UIConstants.DEMO_TAG, "logout success,notify UI!");
//                ActivityStack.getIns().popupAbove(LoginActivity.class);
                LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.LOGOUT, null);
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;

            case FIREWALL_DETECT_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "firewall detect failed,notify UI!");
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;

            case BUILD_STG_FAILED:
                LogUtil.i(UIConstants.DEMO_TAG, "build stg failed,notify UI!");
                Toast.makeText(LocContext.getContext(), ((String) msg.obj), Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    @Override
    public void onReceive(String broadcastName, Object obj) {
        switch (broadcastName) {
            case CustomBroadcastConstants.ACTION_ENTERPRISE_GET_SELF_RESULT:
                List<TsdkContactsInfo> selfInfo = (List<TsdkContactsInfo>) obj;
                TsdkContactsInfo contactInfo = selfInfo.get(0);

                LoginMgr.getInstance().setSelfInfo(contactInfo);

                if (null != contactInfo.getTerminal() && !contactInfo.getTerminal().equals("")) {
                    LoginMgr.getInstance().setTerminal(contactInfo.getTerminal());
                } else {
                    LoginMgr.getInstance().setTerminal(contactInfo.getTerminal2());
                }
                break;
            default:
                break;
        }
    }
}

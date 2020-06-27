package com.zxwl.vclibrary.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;

import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.esdk.utils.ActivityUtil;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.zxwl.vclibrary.activity.VideoConfActivity;
import com.zxwl.vclibrary.activity.VoiceConfActivity;

public class AppForeBackUtil {
    private AppForeBackExchangeListener appForeBackExchangeListener;

    public AppForeBackUtil() {
    }

    /**
     * 注册状态监听
     *
     * @param application
     * @param listener
     */
    public void register(Application application, AppForeBackExchangeListener listener) {
        appForeBackExchangeListener = listener;
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    public void unRegister(Application application) {
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        //打开的Activity数量统计
        private int activityStartCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityStartCount++;
//            LogUtil.i("AppForeBackUtil", "onActivityStarted:" + activityStartCount);
            //数值从0变到1说明是从后台切到前台
            if (activityStartCount >= 1) {
                //从后台切到前台
                if (appForeBackExchangeListener != null) {
                    appForeBackExchangeListener.toFront();
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityStartCount--;

//            LogUtil.i("AppForeBackUtil", "onActivityStopped:" + activityStartCount);

            //数值从1到0说明是从前台切到后台
            if (activityStartCount == 0) {
                //从前台切到后台
                if (appForeBackExchangeListener != null) {
                    appForeBackExchangeListener.toBack();
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    public interface AppForeBackExchangeListener {
        void toFront();

        void toBack();
    }

}

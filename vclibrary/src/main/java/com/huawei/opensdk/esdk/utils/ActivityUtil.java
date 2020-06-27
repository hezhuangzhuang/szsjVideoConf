package com.huawei.opensdk.esdk.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.huawei.opensdk.esdk.common.IntentConstant;
import com.huawei.opensdk.esdk.common.UIConstants;


public final class ActivityUtil {
    private ActivityUtil() {
    }

    public static void startActivity(Context context, String action, String[] categorys) {
        try {
            Intent intent = new Intent(action);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            for (int i = 0; i < categorys.length; i++) {
                intent.addCategory(categorys[i]);
            }
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivity(Context context, String action) {
        try {
            Intent intent = new Intent(action);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivity(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        try {
            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    public static void startActivityForResult(Activity activity, String action, int requestCode) {
        try {
            Intent intent = new Intent(action);
            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Log.e(UIConstants.DEMO_TAG, e.getMessage());
        }
    }

    /**
     * 跳转到来电界面
     */
    public static void startCallIdActivity() {
        //是否来电中
//        boolean isCallerIDIng = CallMgr.getInstance().isCallerIDIng();
//        boolean phoneIsInUse = DeviceUtil.phoneIsInUse();
//        //正在呼叫并切不在通话状态
//        if (isCallerIDIng && !phoneIsInUse) {
//            Intent intent = new Intent(IntentConstant.CALL_IN_ACTIVITY_ACTION);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
//
//            ActivityUtil.startActivity(LocContext.getContext(), intent);
//        }
    }

    /**
     * 跳转到会议界面
     */
    public static void startConfActivity() {
        //是否在会议中
//        boolean isCallerIDIng = CallMgr.getInstance().isExistCall();
//
//        if (!isCallerIDIng) {
//            return;
//        }
//
//        Activity activity = ActivityStack.getIns().findActivity(VideoConfActivity.class);
//
//        if (null != activity) {
//            Intent intent = new Intent(IntentConstant.VIDEO_CONF_MANAGER_ACTIVITY_ACTION);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
//
//            ActivityUtil.startActivity(LocContext.getContext(), intent);
//            return;
//        }
//
//        activity = ActivityStack.getIns().findActivity(VoiceConfActivity.class);
//        if (null != activity) {
//            Intent intent = new Intent(IntentConstant.VOICE_CONF_MANAGER_ACTIVITY_ACTION);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addCategory(IntentConstant.DEFAULT_CATEGORY);
//
//            ActivityUtil.startActivity(LocContext.getContext(), intent);
//
//        }
    }
}

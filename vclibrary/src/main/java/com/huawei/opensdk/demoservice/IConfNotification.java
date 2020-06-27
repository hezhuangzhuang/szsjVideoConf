package com.huawei.opensdk.demoservice;

/**
 * Callback msg interface thrown to the UI layer.
 */
public interface IConfNotification
{
    void onConfEventNotify(ConfConstant.CONF_EVENT confEvent, Object params);
}

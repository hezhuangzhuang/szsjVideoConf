package com.example.szsjhost;

public class HuaweiContants {
    //活动类型，TYPE_CREATE_CONF:创建会议，JOIN_CONF:加入会议
    public static final String FILED_TYPE = "type";

    //创建会议
    public static final int TYPE_CREATE_CONF = 0;
    //会议名称
    public static final String FILED_CONF_NAME = "confName";
    //会议时长
    public static final String FILED_DURATION = "duration";
    //参会列表
    public static final String FILED_SITES = "sites";

    //加入会议
    public static final int TYPE_JOIN_CONF = 1;
    //加入会议的smc会议id
    public static final String FILED_SMC_CONF_ID = "smcConfId";


    //用户账号，同时也是uuid
    public static final String FILED_USER_NAME = "userName";

    //用户显示名称
    public static final String FILED_DISPLAY_NAME = "displayName";
    //密码
    public static final String FILED_PASS_WORD = "password";

    //
    public static final String FILED_USER_TICKET = "UserTicket";

    //smc路径
    public static final String FILED_HUAWEI_SMC_URL = "huaweiServerUrl";

    //smc端口
    public static final String FILED_HUAWEI_SMC_PORT = "huaweiServerPort";


    //鉴权信息
    public static final String FILED_APP_PACKAGE_NAME = "appPackageName";
    public static final String FILED_SECRET_KEY = "secretKey";
    public static final String FILED_BASE_URL = "backServerUrl";
}

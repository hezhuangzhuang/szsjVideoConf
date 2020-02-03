package com.hw.provider.router

/**
 *author：Thinkpad
 *data:2019/12/7 20:17
 */
object RouterPath {
    //用户模块
    class UserCenter {
        companion object {
            const val PATH_LOGIN = "/userCenter/login"
            const val PATH_ABOUT = "/userCenter/about"
            const val PATH_SET_URL = "/userCenter/setUrl"
            const val USER_MODULE_SERVICE = "/userCenter/service"
        }
    }

    //主模块
    class Main {
        companion object {
            const val PATH_MAIN = "/main/main"

            const val FILED_TAB_INDEX = "FILED_TAB_INDEX"
        }
    }

    //通讯录模块
    class Contacts {
        companion object {
            //联系人详情
            const val CONTACT_DETAILS = "/contacts/contactsDetails"

            //群聊详情
            const val GROUP_CHAT_DETAILS = "/contacts/groupChatDetails"

            //删除群组成员
            const val DELETE_GROUP_PEOPLE = "/contacts/deleteGroupPeople"

            const val FILED_RECEIVE_ID = "FILED_RECEIVE_ID"
            const val FILED_RECEIVE_NAME = "FILED_RECEIVE_NAME"

            const val FILED_ALL_PEOPLE = "FILED_ALL_PEOPLE"
            const val FILED_GROUP_ID = "FILED_GROUP_ID"

            //提供给其他模块调用的服务
            const val CONTACTS_MODULE_SERVICE = "/contacts/service"
        }
    }

    //会议模块
    class Conf {
        companion object {
            const val CREATE_CONF = "/conf/createConf"

            const val MY_CONF_LIST = "/conf/myConfList"

            const val SELECTED_PEOPLE = "/conf/selectedPeople"

            const val SELECTED_PEOPLE_REQUEST = 0X111

            //已选择的成员
            const val FILED_SELECTED_PEOPLE = "FILED_SELECTED_PEOPLE"

            //是否是历史会议
            const val FILED_IS_HISTORY = "FILED_IS_HISTORY"

            //是否是创建群组
            const val FILED_IS_CREATE_GROUP = "FILED_IS_CREATE_GROUP"

            //创建会议界面的操作类型，0：创建会议，1：创建群组，2：添加人员
            const val FILED_CONTROL_TYPE = "FILED_CONTROL_TYPE"

            //群组id
            const val FILED_GROUP_ID = "FILED_GROUP_ID"

            //群组中已经存在的人员
            const val FILED_EXIST_PEOPLS = "FILED_EXIST_PEOPLS"

            //是否是视频会议
            const val FILED_VIDEO_CONF = "FILED_VIDEO_CONF"

            //是否是删除群组成员
            const val FILED_DELETE_GROUP_PEOPLE = "FILED_DELETE_GROUP_PEOPLE"
        }
    }

    //聊天模块
    class Chat {
        companion object {
            const val CHAT = "/chat/chat"

            //提供给其他模块调用的服务
            const val CHAT_MODULE_SERVICE = "/chat/db"

            const val FILED_RECEIVE_ID = "FILED_RECEIVE_ID"
            const val FILED_RECEIVE_NAME = "FILED_RECEIVE_NAME"
            const val FILED_IS_GROUP = "FILED_IS_GROUP"

        }
    }

    //华为会议模块
    class Huawei {
        companion object {
            const val HUAWEI_MODULE_SERVICE = "/huawei/service"

            const val CHAIR_SELECT = "/huawei/chairSelect"

            //在线的会场
            const val FILED_ONLINE_LIST = "FILED_ONLINE_LIST"

            //活动类型，TYPE_CREATE_CONF:创建会议，JOIN_CONF:加入会议
            const val FILED_TYPE = "type"

            //创建会议
            const val TYPE_CREATE_CONF = 0
            //会议名称
            const val FILED_CONF_NAME = "confName"
            //会议时长
            const val FILED_DURATION = "duration"
            //参会列表
            const val FILED_SITES = "sites"

            //加入会议
            const val TYPE_JOIN_CONF = 1
            //加入会议的smc会议id
            const val FILED_SMC_CONF_ID = "smcConfId"


            //用户账号，同时也是uuid
            const val FILED_USER_NAME = "userName"

            //用户显示名称
            const val FILED_DISPLAY_NAME = "displayName"

            //密码
            const val FILED_PASS_WORD = "password"

            //
            const val FILED_USER_TICKET = "UserTicket"

            //smc路径
            const val FILED_HUAWEI_SMC_URL = "huaweiServerUrl"

            //smc端口
            const val FILED_HUAWEI_SMC_PORT = "huaweiServerPort"


            //鉴权信息
            const val FILED_APP_PACKAGE_NAME = "appPackageName"
            const val FILED_SECRET_KEY = "secretKey"
            const val FILED_BASE_URL = "backServerUrl"

        }
    }

}
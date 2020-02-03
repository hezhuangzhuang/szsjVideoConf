package com.hw.provider.chat.utils;

import com.hw.baselibrary.common.BaseApp;
import com.hw.baselibrary.utils.LogUtils;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.provider.chat.bean.ChatBean;
import com.hw.provider.chat.bean.ChatBeanLastMessage;
import com.hw.provider.chat.bean.ConstactsBean;
import com.hw.provider.chat.bean.LocalFileBean;
import com.hw.provider.db.ChatBeanDao;
import com.hw.provider.db.ChatBeanLastMessageDao;
import com.hw.provider.db.ConstactsBeanDao;
import com.hw.provider.db.DaoMaster;
import com.hw.provider.db.DaoSession;
import com.hw.provider.db.LocalFileBeanDao;
import com.hw.provider.user.UserContants;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * author：pc-20171125
 * data:2020/1/14 20:32
 * 数据库的操作类
 */
public class GreenDaoUtil {
    private static DaoMaster.DevOpenHelper mSQLiteOpenHelper;
    private static DaoMaster mDaoMaster;

    private static DaoSession mDaoSession;

    //初始化数据库及相关类
    public static void initDataBase() {
        setDebugMode(true);//默认开启Log打印
        //创建数据库
        mSQLiteOpenHelper = new DaoMaster.DevOpenHelper(BaseApp.context,
                SPStaticUtils.getString(UserContants.DISPLAY_NAME) + "-" + SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT) + ".db", null);
        mDaoMaster = new DaoMaster(mSQLiteOpenHelper.getWritableDatabase());
//        mDaoSession = mDaoMaster.newSession();
//        LogUtils.i("创建数据库-->"+SPStaticUtils.getString(UserContants.DISPLAY_NAME) + "-" + SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT));
    }

    public static void close(){
        mDaoSession.clear();
        mDaoSession.getDatabase().close();
        mSQLiteOpenHelper.close();

        mDaoSession = null;

//        sInstance.daoSession.clear();
//        sInstance.mDatabase.close();
//        sInstance.mHelper.close();
//        sInstance = null;
    }

    public synchronized static DaoSession getDaoSession() {
        if (null == mDaoSession) {
            if (null == mDaoMaster) {
                initDataBase();
            }
            mDaoSession = mDaoMaster.newSession();
        }
        return mDaoSession;
    }

    //是否开启Log
    public static void setDebugMode(boolean flag) {
//        mSQLiteOpenHelper. = true;//如果查看数据库更新的Log，请设置为true
        QueryBuilder.LOG_SQL = flag;
        QueryBuilder.LOG_VALUES = flag;
    }

    /**
     * 保存聊天记录
     *
     * @param chatBean
     */
    public static long insertChatBean(ChatBean chatBean) {
        return getDaoSession().getChatBeanDao().insert(chatBean);
    }

    /**
     * 通过id查询聊天记录
     *
     * @param id
     * @return
     */
    public static List<ChatBean> queryByIdChatBeans(String id) {
        return getDaoSession().getChatBeanDao()
                .queryBuilder()
                .where(ChatBeanDao.Properties.ConversationId.eq(id))
                .list();
    }

    /**
     * 更新最后一条数据
     *
     * @param chatBeanLastMessage
     */
    public static void insertLastChatBean(ChatBeanLastMessage chatBeanLastMessage) {
        getDaoSession()
                .getChatBeanLastMessageDao()
                .insertOrReplace(chatBeanLastMessage);
    }

    /**
     * 获取最后一条数据的消息列表
     */
    public static List<ChatBeanLastMessage> queryLastChatBeans() {
        return getDaoSession()
                .getChatBeanLastMessageDao()
                .queryBuilder()
                .orderDesc(ChatBeanLastMessageDao.Properties.Time)
                .list();
    }

    /**
     * 通过id获取最后一条数据
     */
    public static ChatBeanLastMessage queryLastChatBeanById(String conversationId) {
        return getDaoSession()
                .getChatBeanLastMessageDao()
                .queryBuilder()
                .where(ChatBeanLastMessageDao.Properties.ConversationId.eq(conversationId))
                .unique();
    }

    /**
     * 保存联系人
     *
     * @param constactsBean
     * @return
     */
    public static long insertConstactsBean(ConstactsBean constactsBean) {
        return getDaoSession()
                .getConstactsBeanDao()
                .insertOrReplace(constactsBean);
    }

    /**
     * 通过华为id查询联系人
     */
    public static ConstactsBean queryByHuaweiIdConstactsBean(String accountId) {
        List<ConstactsBean> all = getDaoSession().getConstactsBeanDao()
                .queryBuilder().list();
        return getDaoSession().getConstactsBeanDao()
                .queryBuilder()
                .where(ConstactsBeanDao.Properties.Sip.eq(accountId))
                .unique();
    }

    /**
     * 解散群组时，删除群组聊天记录
     */
    public static void deleteMessageById(String conversationId) {
        getDaoSession().getChatBeanLastMessageDao()
                .queryBuilder()
                .where(ChatBeanLastMessageDao.Properties.ConversationId.eq(conversationId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }

    /**
     * 保存语音消息到本地
     */

    public static void insertLocalFileBean(LocalFileBean localFileBean) {
        getDaoSession()
                .getLocalFileBeanDao()
                .insertOrReplace(localFileBean);
    }

    /**
     * 通过远端路径获取本地路径
     */
    public static LocalFileBean queryLocalFileByRemotePath(String remotePath) {
        return getDaoSession().getLocalFileBeanDao()
                .queryBuilder()
                .where(LocalFileBeanDao.Properties.RemotePath.eq(remotePath))
                .unique();
    }

}

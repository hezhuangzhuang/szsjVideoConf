package com.hw.provider.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 本地文件的bean
 */
@Entity
public class LocalFileBean {
    //服务端文件地址
    @Unique
    String remotePath;

    //本地文件地址
    String localPath;

    @Generated(hash = 1856414671)
    public LocalFileBean(String remotePath, String localPath) {
        this.remotePath = remotePath;
        this.localPath = localPath;
    }

    @Generated(hash = 329032171)
    public LocalFileBean() {
    }

    public String getRemotePath() {
        return this.remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getLocalPath() {
        return this.localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}

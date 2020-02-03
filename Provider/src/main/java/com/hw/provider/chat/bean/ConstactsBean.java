package com.hw.provider.chat.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * author：pc-20171125
 * data:2020/1/18 15:27
 * 保存到数据库的联系人的bean
 */
@Entity
public class ConstactsBean {
    @Id
    public Long id;

    @Unique
    public String sip;
    public String name;

    public ConstactsBean(String sip, String name) {
        this.sip = sip;
        this.name = name;
    }

    @Generated(hash = 948691040)
    public ConstactsBean(Long id, String sip, String name) {
        this.id = id;
        this.sip = sip;
        this.name = name;
    }
    @Generated(hash = 1817553141)
    public ConstactsBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSip() {
        return this.sip;
    }
    public void setSip(String sip) {
        this.sip = sip;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

package com.hw.huaweivclib.net.respone;

import java.util.Objects;

/**
 * author：pc-20171125
 * data:2020/1/16 15:23
 * 会控中使用的bean对象
 */
public class ConfControlUserBean {
    /**
     * id : 0271010
     * name : 颜鹏
     * online : 0
     */
    public String id;
    public String name;
    public int online;//0代表在线
    public String sip;//呼叫的号码
    public boolean isCheck = false;//是否被选中

    public ConfControlUserBean(String id, String name,String sip, int online, boolean isCheck) {
        this.id = id;
        this.name = name;
        this.online = online;
        this.isCheck = isCheck;
        this.sip = sip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfControlUserBean dataBean = (ConfControlUserBean) o;
        return online == dataBean.online &&
                Objects.equals(id, dataBean.id) &&
                Objects.equals(name, dataBean.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, online);
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", online=" + online +
                ", isCheck=" + isCheck +
                '}';
    }
}

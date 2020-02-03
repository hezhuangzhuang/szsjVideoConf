package com.hw.huaweivclib.net.respone;

import java.util.List;

/**
 * author：pc-20171125
 * data:2020/1/15 20:10
 */
public class BaseData<T> {
    public int code;
    public String msg;

    public static int SUCEESS_CODE = 0;

    public List<T> data;

    @Override
    public String toString() {
        return "BaseData{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}

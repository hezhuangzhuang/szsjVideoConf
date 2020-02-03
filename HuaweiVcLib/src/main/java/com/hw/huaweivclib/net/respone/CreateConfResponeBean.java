package com.hw.huaweivclib.net.respone;

import java.util.List;

/**
 * author：pc-20171125
 * data:2020/1/18 19:11
 */
public class CreateConfResponeBean {

    /**
     * code : 0
     * msg : success
     * data : {"smcConfId":"1247","confName":"1234","accessCode":"900002710619","confPassword":"","duration":"120","confMediaType":1,"creatorUri":"0000010007","createTime":"2020-01-18 19:03:04","siteInfoList":[{"siteUri":"0000010000","siteName":"何伟"},{"siteUri":"0000010004","siteName":"黄露露"},{"siteUri":"0000010016","siteName":"黄恒"},{"siteUri":"0000010007","siteName":"杨章建"}],"sites":"0000010000,0000010004,0000010016,0000010007","ifSuccess":1}
     */

    public int code;
    public String msg;
    public DataBean data;


    public static class DataBean {
        /**
         * smcConfId : 1247
         * confName : 1234
         * accessCode : 900002710619
         * confPassword : 
         * duration : 120
         * confMediaType : 1
         * creatorUri : 0000010007
         * createTime : 2020-01-18 19:03:04
         * siteInfoList : [{"siteUri":"0000010000","siteName":"何伟"},{"siteUri":"0000010004","siteName":"黄露露"},{"siteUri":"0000010016","siteName":"黄恒"},{"siteUri":"0000010007","siteName":"杨章建"}]
         * sites : 0000010000,0000010004,0000010016,0000010007
         * ifSuccess : 1
         */

        public String smcConfId;
        public String confName;
        public String accessCode;
        public String confPassword;
        public String duration;
        public int confMediaType;
        public String creatorUri;
        public String createTime;
        public String sites;
        public int ifSuccess;
        public List<SiteInfoListBean> siteInfoList;

        public static class SiteInfoListBean {
            /**
             * siteUri : 0000010000
             * siteName : 何伟
             */

            public String siteUri;
            public String siteName;
        }
    }
}

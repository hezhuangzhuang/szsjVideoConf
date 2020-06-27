package com.huawei.opensdk.callmgr;

import java.io.Serializable;

/**
 * This class is about transfer between call information modules
 * 呼叫信息模块间传递类
 */
public class CallInfo implements Serializable
{
    /**
     * call ID
     * 呼叫id
     */
    private int callID;

    /**
     * conference ID
     * 会议id
     */
    private String confID;

    /**
     * Peer number
     * 来电号码
     */
    private String peerNumber;

    /**
     * Peer display name
     * 来电人
     */
    private String peerDisplayName;

    /**
     * Video call
     * 是否是视频来电
     */
    private boolean isVideoCall;

    /**
     *  Conference identify
     * 是否是会议
     */
    private boolean isFocus;

    /**
     * Caller
     * 主叫
     */
    private boolean isCaller;

    /**
     * Reason code
     * 错误码
     */
    private int reasonCode;

    /**
     * Maybe video call
     * 可能是视频呼叫
     */
    private Boolean maybeVideoCall;

    @Override
    public String toString() {
        return "CallInfo{" +
                "callID=" + callID +
                ", confID='" + confID + '\'' +
                ", peerNumber='" + peerNumber + '\'' +
                ", peerDisplayName='" + peerDisplayName + '\'' +
                ", isVideoCall=" + isVideoCall +
                ", isFocus=" + isFocus +
                ", isCaller=" + isCaller +
                ", reasonCode=" + reasonCode +
                ", maybeVideoCall=" + maybeVideoCall +
                '}';
    }


    public int getCallID() {
        return callID;
    }

    public void setCallID(int callID) {
        this.callID = callID;
    }

    public String getConfID() {
        return confID;
    }

    public void setConfID(String confID) {
        this.confID = confID;
    }

    public String getPeerNumber() {
        return peerNumber;
    }

    public void setPeerNumber(String peerNumber) {
        this.peerNumber = peerNumber;
    }

    public String getPeerDisplayName() {
        return peerDisplayName;
    }

    public void setPeerDisplayName(String peerDisplayName) {
        this.peerDisplayName = peerDisplayName;
    }

    public boolean isVideoCall() {
        return isVideoCall;
    }

    public void setVideoCall(boolean videoCall) {
        isVideoCall = videoCall;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }

    public boolean isCaller() {
        return isCaller;
    }

    public void setCaller(boolean caller) {
        isCaller = caller;
    }


    public int getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(int reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Boolean getMaybeVideoCall() {
        return maybeVideoCall;
    }

    public void setMaybeVideoCall(Boolean maybeVideoCall) {
        this.maybeVideoCall = maybeVideoCall;
    }

    public static class Builder
    {
        //呼叫ID
        private int callID;
        //会议ID
        private String confID;
        //对端号码
        private String peerNumber;
        //对端名称
        private String peerDisplayName;

        //是否是视频呼叫
        private boolean isVideoCall;

        //是否是会议
        private boolean isFocus;

        private boolean isCaller;
        private Boolean maybeVideoCall;

        private int reasonCode;

        public Builder setCallID(int callID) {
            this.callID = callID;
            return this;
        }

        public Builder setConfID(String confID) {
            this.confID = confID;
            return this;
        }

        public Builder setPeerNumber(String peerNumber) {
            this.peerNumber = peerNumber;
            return this;
        }

        public Builder setPeerDisplayName(String peerDisplayName) {
            this.peerDisplayName = peerDisplayName;
            return this;
        }

        public Builder setVideoCall(boolean videoCall) {
            isVideoCall = videoCall;
            return this;
        }

        public Builder setFocus(boolean focus) {
            isFocus = focus;
            return this;
        }

        public Builder setCaller(boolean caller) {
            isCaller = caller;
            return this;
        }

        public Builder setReasonCode(int reasonCode) {
            this.reasonCode = reasonCode;
            return this;
        }

        public void setMaybeVideoCall(Boolean maybeVideoCall) {
            this.maybeVideoCall = maybeVideoCall;
        }

        private void apply(CallInfo params)
        {
            params.callID = this.callID;
            params.confID = this.confID;
            params.peerNumber = this.peerNumber;
            params.peerDisplayName = this.peerDisplayName;

            params.isVideoCall = this.isVideoCall;
            params.isFocus = this.isFocus;
            params.isCaller = this.isCaller;
            params.reasonCode = this.reasonCode;
            params.maybeVideoCall = this.maybeVideoCall;
        }

        public CallInfo build()
        {
            CallInfo callInfo = new CallInfo();
            apply(callInfo);
            return callInfo;
        }
    }
}

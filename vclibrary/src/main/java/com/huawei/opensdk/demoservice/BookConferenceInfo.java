package com.huawei.opensdk.demoservice;

import com.huawei.ecterminalsdk.base.TsdkConfMediaType;
import com.huawei.ecterminalsdk.base.TsdkConfRecordMode;

import java.io.Serializable;
import java.util.List;


/**
 * This class is about book conference information
 * 预约会议信息类
 */
public class BookConferenceInfo implements Serializable {

    /**
     * subject
     * 会议主题
     */
    private String subject;

    /**
     * media type
     * 媒体类型
     */
    TsdkConfMediaType mediaType;

    /**
     * record type
     * 录制类型
     */
    TsdkConfRecordMode recordType;

    /**
     * is auto
     * 是否自动录制

     */
    private Boolean is_auto;

    /**
     * Conference start time
     * 会议开始时间
     */
    private String startTime;

    /**
     * duration
     * 会议时长
     */
    private int duration;

    /**
     * size
     * 会议人数
     */
    private int size;

    /**
     * Member list
     * 与会者列表
     */
    private List<Member> memberList;

    /**
     * Start now
     * 是否是即时会议
     */
    private boolean isInstantConference = true; //默认为立即会议

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public TsdkConfMediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(TsdkConfMediaType mediaType) {
        this.mediaType = mediaType;
    }

    public TsdkConfRecordMode getRecordType() {
        return recordType;
    }

    public void setRecordType(TsdkConfRecordMode recordType) {
        this.recordType = recordType;
    }

    public Boolean getIs_auto() {
        return is_auto;
    }

    public void setIs_auto(Boolean is_auto) {
        this.is_auto = is_auto;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    public boolean isInstantConference() {
        return isInstantConference;
    }

    public void setInstantConference(boolean instantConference) {
        isInstantConference = instantConference;
    }

}

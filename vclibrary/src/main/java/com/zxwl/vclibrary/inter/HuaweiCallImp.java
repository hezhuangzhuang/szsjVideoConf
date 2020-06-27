package com.zxwl.vclibrary.inter;

import com.huawei.ecterminalsdk.base.TsdkConfJoinParam;
import com.huawei.ecterminalsdk.base.TsdkConfMediaType;
import com.huawei.ecterminalsdk.base.TsdkConfRecordMode;
import com.huawei.ecterminalsdk.base.TsdkConfRole;
import com.huawei.ecterminalsdk.base.TsdkContactsInfo;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants;
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast;
import com.huawei.opensdk.commonservice.util.DeviceManager;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.BookConferenceInfo;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.demoservice.Member;
import com.huawei.opensdk.esdk.common.UIConstants;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.zxwl.vclibrary.activity.LoadingActivity;
import com.zxwl.vclibrary.util.Constants;
import com.zxwl.vclibrary.util.DateUtil;
import com.zxwl.vclibrary.util.permission.PermissionUtils;
import com.zxwl.vclibrary.util.sharedpreferences.SPStaticUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author：pc-20171125
 * data:2019/03/18 10:12
 * 呼叫对外暴露的接口
 */
public class HuaweiCallImp {
    private static HuaweiCallImp callImp = new HuaweiCallImp();

    public static HuaweiCallImp getInstance() {
        return callImp;
    }


    /**
     * 点对点呼叫
     *
     * @param siteNumber
     * @param isVideoCall
     * @return
     */
    public int callSite(String siteNumber, boolean isVideoCall) {
        //判断是否有网络
        if (!DeviceManager.isNetworkAvailable(LocContext.getContext())) {
            return -1;
        }
        return CallMgr.getInstance().startCall(siteNumber, isVideoCall);
    }


    /**
     * 创建即时会议
     *
     * @param confName      会议名称
     * @param memberNumbers 参会列表号码
     * @return
     */
    public int createConf(
            final String confName,
            final List<String> memberNumbers,
            final boolean isVideoConf) {
        //判断是否有网络
        if (!DeviceManager.isNetworkAvailable(LocContext.getContext())) {
            return -1;
        }

        List<Member> members = memberNumberToMembers(memberNumbers);

        return createConfVc(confName, members, isVideoConf);
    }

    /**
     * 呼叫号码转成member对象
     * @param memberNumbers
     * @return
     */
    private List<Member> memberNumberToMembers(List<String> memberNumbers) {
        List<Member> numbers = new ArrayList<>();

        Member member = null;
        for (String number : memberNumbers) {
            member = new Member();
            member.setNumber(number);
            member.setAccountId(number);
            member.setRole(TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE);
            numbers.add(member);
        }

        return numbers;
    }

    /**
     * 加入会议
     *
     * @param confId  会议id
     * @param confPwd 会议密码
     * @return
     */
    public int joinConf(final String confId,
                        final String confPwd) {
        //判断是否有网络
        if (!DeviceManager.isNetworkAvailable(LocContext.getContext())) {
            return -1;
        }

        TsdkConfJoinParam confJoinParam = new TsdkConfJoinParam();
        confJoinParam.setConfId(confId);
        confJoinParam.setConfPassword(confId);
        //会议接入码固定为:+991117
//        confJoinParam.setAccessNumber("+991117");
        confJoinParam.setAccessNumber(confId);

        return MeetingMgr.getInstance().joinConf(
                confJoinParam,
                true,
                LoginMgr.getInstance().getTerminal()
        );

//        return callSite(confId,true);
    }

    /**
     * vc召集会议
     *
     * @param confName
     * @param memberNumbers
     * @param isVideoConf
     * @return
     */
    private Integer createConfVc(String confName, List<Member> memberNumbers, boolean isVideoConf) {
        int result;
        BookConferenceInfo bookConferenceInfo = new BookConferenceInfo();

        //设置会议类型
        bookConferenceInfo.setMediaType(isVideoConf ? TsdkConfMediaType.TSDK_E_CONF_MEDIA_VIDEO_DATA : TsdkConfMediaType.TSDK_E_CONF_MEDIA_VOICE_DATA);

        //设置会议时间
        String formatStr = DateUtil.longToString(System.currentTimeMillis(), DateUtil.FORMAT_DATE_TIME);
        bookConferenceInfo.setStartTime(formatStr);
        //即时会议
        bookConferenceInfo.setInstantConference(true);

        //自动录播
        bookConferenceInfo.setIs_auto(false);

        //录播方式
        bookConferenceInfo.setRecordType(TsdkConfRecordMode.TSDK_E_CONF_RECORD_DISABLE);

        //会议时长
        bookConferenceInfo.setDuration(120);

        //会议名称
        bookConferenceInfo.setSubject(confName);

        Member chairman = new Member();
        TsdkContactsInfo contactSelf = LoginMgr.getInstance().getSelfInfo();
        if (contactSelf != null) {
            chairman.setDisplayName(contactSelf.getPersonName());
        }

        chairman.setNumber(LoginMgr.getInstance().getTerminal());
        chairman.setAccountId(LoginMgr.getInstance().getAccount());
        chairman.setRole(TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN);

        memberNumbers.add(chairman);

        //参会人员
        bookConferenceInfo.setMemberList(memberNumbers);

        result = MeetingMgr.getInstance().bookConference(bookConferenceInfo);
        if (result != 0) {
            LogUtil.e(UIConstants.DEMO_TAG, "bookReservedConf fail result ->" + result);
            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.GET_CONF_DETAIL_RESULT, null);
            return result;
        } else {
            //是否自己创建的会议
            SPStaticUtils.put(Constants.IS_CREATE, true);
            //是否需要自动接听
            SPStaticUtils.put(Constants.IS_AUTO_ANSWER, true);

            //显示等待界面
            LoadingActivity.Companion.startActivty(LocContext.getContext());
//            LoadingActivity.startActivty(LocContext.getContext());
        }
        return 0;
    }

    /**
     * vc预约会议
     *
     * @param confName
     * @param memberNumbers
     * @param isVideoConf
     * @return
     */
    public Integer reservedConfVc(String confName, String startTime, List<String> memberNumbers, boolean isVideoConf) {
        int result;
        BookConferenceInfo bookConferenceInfo = new BookConferenceInfo();

        //设置会议类型
        bookConferenceInfo.setMediaType(isVideoConf ? TsdkConfMediaType.TSDK_E_CONF_MEDIA_VIDEO_DATA : TsdkConfMediaType.TSDK_E_CONF_MEDIA_VOICE_DATA);

        //设置会议时间
        bookConferenceInfo.setStartTime(startTime);

        //即时会议
        bookConferenceInfo.setInstantConference(false);

        //自动录播
        bookConferenceInfo.setIs_auto(false);

        //录播方式
        bookConferenceInfo.setRecordType(TsdkConfRecordMode.TSDK_E_CONF_RECORD_DISABLE);

        //会议时长
        bookConferenceInfo.setDuration(180);

        //会议名称
        bookConferenceInfo.setSubject(confName);

        List<Member> memberList = memberNumberToMembers(memberNumbers);

        Member chairman = new Member();
        TsdkContactsInfo contactSelf = LoginMgr.getInstance().getSelfInfo();
        if (contactSelf != null) {
            chairman.setDisplayName(contactSelf.getPersonName());
        }

        chairman.setNumber(LoginMgr.getInstance().getTerminal());
        chairman.setAccountId(LoginMgr.getInstance().getAccount());
        chairman.setRole(TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN);
        memberList.add(chairman);

        //参会人员
        bookConferenceInfo.setMemberList(memberList);

        result = MeetingMgr.getInstance().bookConference(bookConferenceInfo);
        if (result != 0) {
            LogUtil.e(UIConstants.DEMO_TAG, "bookReservedConf fail result ->" + result);
            LocBroadcast.getInstance().sendBroadcast(CustomBroadcastConstants.GET_CONF_DETAIL_RESULT, null);
            return result;
        } else {
        }
        return 0;
    }

    /**
     * 获取成员
     *
     * @param members
     * @return
     */
    private List<Member> getMembers(List<Member> members) {
        //是否存在自己
        int selfIndex = -1;

        TsdkContactsInfo contactSelf = LoginMgr.getInstance().getSelfInfo();
        for (int i = 0, len = members.size(); i < len; i++) {
            members.get(i).setRole(TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE);
            //判断是自己
            if (members.get(i).getNumber().equals(LoginMgr.getInstance().getTerminal())) {
                selfIndex = i;
            }
        }

        //如果下标不为空则删除自己
        if (selfIndex != -1) {
            members.remove(selfIndex);
        }

        //Join the meeting as chairman
        //不存在自己则添加
        Member chairman = new Member();
        if (contactSelf != null) {
            chairman.setDisplayName(contactSelf.getPersonName());
        }
        chairman.setNumber(LoginMgr.getInstance().getTerminal());
        chairman.setAccountId(LoginMgr.getInstance().getAccount());
        chairman.setRole(TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN);
        members.add(0, chairman);
        return members;
    }

    /**
     * 查询会议详情
     *
     * @param confId 会议id
     */
    public int queryConfDetail(String confId) {
        return MeetingMgr.getInstance().queryConfDetail(confId);
    }
}


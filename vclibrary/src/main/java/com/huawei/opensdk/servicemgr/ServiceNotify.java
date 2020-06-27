package com.huawei.opensdk.servicemgr;

import com.huawei.ecterminalsdk.base.TsdkBatchChatMsgInfo;
import com.huawei.ecterminalsdk.base.TsdkBeAddedFriendInfo;
import com.huawei.ecterminalsdk.base.TsdkBeAddedToChatGroupInfo;
import com.huawei.ecterminalsdk.base.TsdkChatGroupInfoUpdateType;
import com.huawei.ecterminalsdk.base.TsdkChatGroupUpdateInfo;
import com.huawei.ecterminalsdk.base.TsdkChatMsgInfo;
import com.huawei.ecterminalsdk.base.TsdkChatMsgUndeliverInfo;
import com.huawei.ecterminalsdk.base.TsdkChatMsgWithdrawInfo;
import com.huawei.ecterminalsdk.base.TsdkChatMsgWithdrawResult;
import com.huawei.ecterminalsdk.base.TsdkConfAppShareType;
import com.huawei.ecterminalsdk.base.TsdkConfAsStateInfo;
import com.huawei.ecterminalsdk.base.TsdkConfBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkConfChatMsgInfo;
import com.huawei.ecterminalsdk.base.TsdkConfDetailInfo;
import com.huawei.ecterminalsdk.base.TsdkConfListInfo;
import com.huawei.ecterminalsdk.base.TsdkConfOperationResult;
import com.huawei.ecterminalsdk.base.TsdkConfSpeakerInfo;
import com.huawei.ecterminalsdk.base.TsdkConfTokenMsg;
import com.huawei.ecterminalsdk.base.TsdkCtdCallStatus;
import com.huawei.ecterminalsdk.base.TsdkDelChatGroupMemberResult;
import com.huawei.ecterminalsdk.base.TsdkDocBaseInfo;
import com.huawei.ecterminalsdk.base.TsdkDocShareDelDocInfo;
import com.huawei.ecterminalsdk.base.TsdkForceLogoutInfo;
import com.huawei.ecterminalsdk.base.TsdkGetIconResult;
import com.huawei.ecterminalsdk.base.TsdkImLoginParam;
import com.huawei.ecterminalsdk.base.TsdkImUserInfo;
import com.huawei.ecterminalsdk.base.TsdkImUserStatusUpdateInfo;
import com.huawei.ecterminalsdk.base.TsdkInputtingStatusInfo;
import com.huawei.ecterminalsdk.base.TsdkIptServiceInfoSet;
import com.huawei.ecterminalsdk.base.TsdkJoinConfIndInfo;
import com.huawei.ecterminalsdk.base.TsdkLeaveChatGroupResult;
import com.huawei.ecterminalsdk.base.TsdkLoginFailedInfo;
import com.huawei.ecterminalsdk.base.TsdkLoginSuccessInfo;
import com.huawei.ecterminalsdk.base.TsdkMsgReadIndInfo;
import com.huawei.ecterminalsdk.base.TsdkOnEvtAsScreenFirstKeyframe;
import com.huawei.ecterminalsdk.base.TsdkOnEvtAsScreenKeyframe;
import com.huawei.ecterminalsdk.base.TsdkOnEvtConfBaseInfoInd;
import com.huawei.ecterminalsdk.base.TsdkOnEvtDataComponentLoadInd;
import com.huawei.ecterminalsdk.base.TsdkOnEvtRecvCustomDataInd;
import com.huawei.ecterminalsdk.base.TsdkOnEvtStatisticLocalQos;
import com.huawei.ecterminalsdk.base.TsdkReqJoinChatGroupMsg;
import com.huawei.ecterminalsdk.base.TsdkRspJoinChatGroupMsg;
import com.huawei.ecterminalsdk.base.TsdkSearchContactsResult;
import com.huawei.ecterminalsdk.base.TsdkSearchDepartmentResult;
import com.huawei.ecterminalsdk.base.TsdkSecurityTunnelInfo;
import com.huawei.ecterminalsdk.base.TsdkSendChatMsgResult;
import com.huawei.ecterminalsdk.base.TsdkServiceAccountType;
import com.huawei.ecterminalsdk.base.TsdkSessionCodec;
import com.huawei.ecterminalsdk.base.TsdkSessionModified;
import com.huawei.ecterminalsdk.base.TsdkSetIptServiceResult;
import com.huawei.ecterminalsdk.base.TsdkSmsInfo;
import com.huawei.ecterminalsdk.base.TsdkVideoOrientation;
import com.huawei.ecterminalsdk.base.TsdkVideoViewRefresh;
import com.huawei.ecterminalsdk.base.TsdkVoipAccountInfo;
import com.huawei.ecterminalsdk.base.TsdkWbDelDocInfo;
import com.huawei.ecterminalsdk.models.TsdkCommonResult;
import com.huawei.ecterminalsdk.models.TsdkNotify;
import com.huawei.ecterminalsdk.models.call.TsdkCall;
import com.huawei.ecterminalsdk.models.conference.TsdkConference;
import com.huawei.ecterminalsdk.models.im.TsdkChatGroup;
import com.huawei.opensdk.callmgr.CallMgr;
import com.huawei.opensdk.callmgr.ctdservice.CtdMgr;
import com.huawei.opensdk.callmgr.iptService.IptMgr;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.demoservice.MeetingMgr;
import com.huawei.opensdk.eaddr.EnterpriseAddressBookMgr;
import com.huawei.opensdk.loginmgr.LoginMgr;

import java.util.List;


public class ServiceNotify implements TsdkNotify{

    private static final String TAG = ServiceNotify.class.getSimpleName();

    private static ServiceNotify instance;

    public static ServiceNotify getInstance() {
        if (null == instance) {
            instance = new ServiceNotify();
        }
        return instance;
    }

    @Override
    public void onEvtAuthSuccess(int userId, TsdkImLoginParam imAccountLoginParam) {
        LogUtil.i(TAG, "onEvtAuthSuccess notify.");
        LoginMgr.getInstance().handleAuthSuccess(userId, imAccountLoginParam);
    }

    @Override
    public void onEvtAuthFailed(int userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtAuthFailed notify.");
        LoginMgr.getInstance().handleAuthFailed(userId, result);
    }

    @Override
    public void onEvtAuthRefreshFailed(int userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtAuthRefreshFailed notify.");
        LoginMgr.getInstance().handleAuthRefreshFailed(userId, result);

    }

    @Override
    public void onEvtLoginSuccess(int userId, TsdkServiceAccountType serviceAccountType, TsdkLoginSuccessInfo loginSuccessInfo) {
        LogUtil.i(TAG, "onEvtLoginSuccess notify.");
        LoginMgr.getInstance().handleLoginSuccess(userId, serviceAccountType, loginSuccessInfo);
    }

    @Override
    public void onEvtLoginFailed(int userId, TsdkServiceAccountType serviceAccountType, TsdkLoginFailedInfo loginFailedInfo) {
        LogUtil.i(TAG, "onEvtLoginFailed notify.");
        LoginMgr.getInstance().handleLoginFailed(userId, serviceAccountType, loginFailedInfo);
    }

    @Override
    public void onEvtLogoutSuccess(int userId, TsdkServiceAccountType serviceAccountType) {
        LogUtil.i(TAG, "onEvtLogoutSuccess notify.");
        LoginMgr.getInstance().handleLogoutSuccess(userId, serviceAccountType);
    }


    @Override
    public void onEvtLogoutFailed(int userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtLogoutFailed notify.");
        LoginMgr.getInstance().handleLogoutFailed(userId, result);

    }

    @Override
    public void onEvtForceLogout(int userId, TsdkServiceAccountType serviceAccountType, TsdkForceLogoutInfo forceLogoutInfo) {
        LogUtil.i(TAG, "onEvtForceLogout notify.");
        LoginMgr.getInstance().handleForceLogout(userId);
    }

    @Override
    public void onEvtVoipAccountStatus(int userId, TsdkVoipAccountInfo voipAccountInfo) {
        LogUtil.i(TAG, "onEvtVoipAccountStatus notify.");
        LoginMgr.getInstance().handleVoipAccountStatus(userId, voipAccountInfo);
    }

    @Override
    public void onEvtImAccountStatus(int userId) {
        LogUtil.i(TAG, "onEvtImAccountStatus notify.");
    }

    @Override
    public void onEvtFirewallDetectFailed(int userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtFirewallDetectFailed notify.");
        LoginMgr.getInstance().handleFirewallDetectFailed(userId, result);
    }

    @Override
    public void onEvtBuildStgTunnelFailed(int userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtBuildStgTunnelFailed notify.");
        LoginMgr.getInstance().handleBuildStgTunnelFailed(userId, result);
    }

    @Override
    public void onEvtSecurityTunnelInfoInd(int userId, int firewallMode, TsdkSecurityTunnelInfo securityTunnelInfo) {
        LogUtil.i(TAG, "onEvtSecurityTunnelInfoInd notify.");
        LoginMgr.getInstance().handleSecurityTunnelInfoInd(userId, firewallMode, securityTunnelInfo);
    }

    @Override
    public void onEvtCallStartResult(TsdkCall call, TsdkCommonResult result) {
        LogUtil.e(TAG, "onEvtCallStartResult notify.");

    }

    @Override
    public void onEvtCallIncoming(TsdkCall call, Boolean maybeVideoCall) {
        LogUtil.i(TAG, "onEvtCallIncoming notify.");
        CallMgr.getInstance().handleCallComing(call, maybeVideoCall);

    }

    @Override
    public void onEvtCallOutgoing(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallOutgoing notify.");
        CallMgr.getInstance().handleCallGoing(call);

    }

    @Override
    public void onEvtCallRingback(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallRingback notify.");
        CallMgr.getInstance().handleCallRingback(call);

    }

    @Override
    public void onEvtCallRtpCreated(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallRtpCreated notify.");
        CallMgr.getInstance().handleCallRtpCreated(call);

    }

    @Override
    public void onEvtCallConnected(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallConnected notify.");
        CallMgr.getInstance().handleCallConnected(call);

    }

    @Override
    public void onEvtCallEnded(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallEnded notify.");
        CallMgr.getInstance().handleCallEnded(call);

    }

    @Override
    public void onEvtCallDestroy(TsdkCall call) {
        LogUtil.i(TAG, "onEvtCallDestroy notify.");
        CallMgr.getInstance().handleCallDestroy(call);

    }

    @Override
    public void onEvtOpenVideoReq(TsdkCall call, TsdkVideoOrientation orientType) {
        LogUtil.i(TAG, "onEvtOpenVideoReq notify.");
        CallMgr.getInstance().handleOpenVideoReq(call,orientType);

    }

    @Override
    public void onEvtRefuseOpenVideoInd(TsdkCall call) {
        LogUtil.i(TAG, "onEvtRefuseOpenVideoInd notify.");
        CallMgr.getInstance().handleRefuseOpenVideoInd(call);
    }

    @Override
    public void onEvtCloseVideoInd(TsdkCall call) {
        CallMgr.getInstance().handleCloseVideoInd(call);
        LogUtil.i(TAG, "onEvtCloseVideoInd notify.");

    }

    @Override
    public void onEvtOpenVideoInd(TsdkCall call) {
        LogUtil.i(TAG, "onEvtOpenVideoInd notify.");
        CallMgr.getInstance().handleOpenVideoInd(call);

    }

    @Override
    public void onEvtRefreshViewInd(TsdkCall call, TsdkVideoViewRefresh refreshInfo) {
        LogUtil.i(TAG, "onEvtRefreshViewInd notify.");
        CallMgr.getInstance().handleRefreshViewInd(call, refreshInfo);

    }

    @Override
    public void onEvtCallRouteChange(TsdkCall call, int route) {
        LogUtil.i(TAG, "onEvtCallRouteChange notify.");

    }

    @Override
    public void onEvtPlayMediaEnd(int handle) {
        LogUtil.i(TAG, "onEvtPlayMediaEnd notify.");

    }

    @Override
    public void onEvtSessionModified(TsdkCall call, TsdkSessionModified sessionInfo) {
        LogUtil.i(TAG, "onEvtSessionModified notify.");
    }

    @Override
    public void onEvtSessionCodec(TsdkCall call, TsdkSessionCodec codecInfo) {
        LogUtil.i(TAG, "onEvtSessionCodec notify.");
    }

    @Override
    public void onEvtHoldSuccess(TsdkCall call) {
        LogUtil.i(TAG, "onEvtHoldSuccess notify.");
        CallMgr.getInstance().handleHoldSuccess(call);
    }

    @Override
    public void onEvtHoldFailed(TsdkCall call) {
        LogUtil.i(TAG, "onEvtHoldFailed notify.");
        CallMgr.getInstance().handleHoldFailed(call);
    }

    @Override
    public void onEvtUnholdSuccess(TsdkCall call) {
        LogUtil.i(TAG, "onEvtUnholdSuccess notify.");
        CallMgr.getInstance().handleUnholdSuccess(call);
    }

    @Override
    public void onEvtUnholdFailed(TsdkCall call) {
        LogUtil.i(TAG, "onEvtUnholdFailed notify.");
        CallMgr.getInstance().handleUnholdFailed(call);
    }

    @Override
    public void onEvtEndcallFailed(TsdkCall call, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtEndcallFailed notify.");

    }

    @Override
    public void onEvtDivertFailed(TsdkCall call) {
        LogUtil.i(TAG, "onEvtDivertFailed notify.");
        CallMgr.getInstance().handleDivertFailed(call);
    }

    @Override
    public void onEvtBldTransferSuccess(TsdkCall call) {
        LogUtil.i(TAG, "onEvtBldTransferSuccess notify.");
        CallMgr.getInstance().handleBldTransferSuccess(call);
    }

    @Override
    public void onEvtBldTransferFailed(TsdkCall call) {
        LogUtil.i(TAG, "onEvtBldTransferFailed notify.");
        CallMgr.getInstance().handleBldTransferFailed(call);
    }

    @Override
    public void onEvtSetIptServiceResult(int type, TsdkSetIptServiceResult result) {
        LogUtil.i(TAG, "onEvtSetIptServiceResult notify.");
        IptMgr.getInstance().handleSetIptServiceResult(type, result);
    }

    @Override
    public void onEvtIptServiceInfo(TsdkIptServiceInfoSet serviceInfo) {
        LogUtil.i(TAG, "onEvtIptServiceInfo notify.");
        IptMgr.getInstance().handleIptServiceInfo(serviceInfo);
    }

    @Override
    public void onEvtGetTempUserResult(int userId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtGetTempUserResult notify.");
        MeetingMgr.getInstance().handleGetTempUserResult(userId, result);
    }

    @Override
    public void onEvtBookConfResult(TsdkCommonResult result, TsdkConfBaseInfo confBaseInfo) {
        LogUtil.i(TAG, "onEvtBookConfResult notify.");
        MeetingMgr.getInstance().handleBookConfResult(result, confBaseInfo);
    }

    @Override
    public void onEvtQueryConfListResult(TsdkCommonResult result, TsdkConfListInfo confList) {
        LogUtil.i(TAG, "onEvtQueryConfListResult notify.");
        MeetingMgr.getInstance().handleQueryConfListResult(result, confList);
    }

    @Override
    public void onEvtQueryConfDetailResult(TsdkCommonResult result, TsdkConfDetailInfo confDetailInfo) {
        LogUtil.i(TAG, "onEvtQueryConfDetailResult notify.");
        MeetingMgr.getInstance().handleQueryConfDetailResult(result, confDetailInfo);
    }

    @Override
    public void onEvtJoinConfResult(TsdkConference conference, TsdkCommonResult result, TsdkJoinConfIndInfo info) {
        LogUtil.i(TAG, "onEvtJoinConfResult notify.");
        MeetingMgr.getInstance().handleJoinConfResult(conference, result, info);
    }

    @Override
    public void onEvtGetDataconfParamResult(TsdkConference conference, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtGetDataconfParamResult notify.");
        MeetingMgr.getInstance().handleGetDataConfParamsResult(conference, result);
    }

    @Override
    public void onEvtConfctrlOperationResult(TsdkConference conference, TsdkConfOperationResult result) {
        LogUtil.i(TAG, "onEvtConfctrlOperationResult notify.");
        MeetingMgr.getInstance().handleConfctrlOperationResult(conference, result);
    }


    @Override
    public void onEvtInfoAndStatusUpdate(TsdkConference conference) {
        LogUtil.i(TAG, "onEvtInfoAndStatusUpdate notify.");
        MeetingMgr.getInstance().handleInfoAndStatusUpdate(conference);
    }

    @Override
    public void onEvtSpeakerInd(TsdkConference conference, TsdkConfSpeakerInfo speakerList) {
        LogUtil.i(TAG, "onEvtSpeakerInd notify.");
        MeetingMgr.getInstance().handleSpeakerInd(speakerList);
    }

    @Override
    public void onEvtRequestConfRightFailed(TsdkConference conference, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtRequestConfRightFailed notify.");
    }

    @Override
    public void onEvtConfIncomingInd(TsdkConference conference) {
        LogUtil.i(TAG, "onEvtConfIncomingInd notify.");
        MeetingMgr.getInstance().handleConfIncomingInd(conference);

    }

    @Override
    public void onEvtConfEndInd(TsdkConference conference) {
        LogUtil.i(TAG, "onEvtConfEndInd notify.");
        MeetingMgr.getInstance().handleConfEndInd(conference);
    }

    @Override
    public void onEvtJoinDataConfResult(TsdkConference conference, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtJoinDataConfResult notify.");
        MeetingMgr.getInstance().handleJoinDataConfResult(conference, result);
    }

    @Override
    public void onEvtAsStateChange(TsdkConference conference, TsdkConfAppShareType shareType, TsdkConfAsStateInfo asStateInfo) {
        LogUtil.i(TAG, "onEvtAsStateChange notify.");
        MeetingMgr.getInstance().handleAsStateChange(asStateInfo);
    }

    @Override
    public void onEvtDsDocNew(TsdkConference conference, TsdkDocBaseInfo docBaseInfo) {
        LogUtil.i(TAG, "onEvtDsDocNew notify.");
        MeetingMgr.getInstance().handleDsDocNew(docBaseInfo);
    }

    @Override
    public void onEvtDsDocDel(TsdkConference conference, TsdkDocShareDelDocInfo docShareDelDocInfo) {
        LogUtil.i(TAG, "onEvtDsDocDel notify.");
        MeetingMgr.getInstance().handleDsDocDel(docShareDelDocInfo);
    }

    @Override
    public void onEvtWbDocNew(TsdkConference conference, TsdkDocBaseInfo docBaseInfo) {
        LogUtil.i(TAG, "onEvtWbDocNew notify.");
        MeetingMgr.getInstance().handleWbDocNew(docBaseInfo);
    }

    @Override
    public void onEvtWbDocDel(TsdkConference conference, TsdkWbDelDocInfo wbDelDocInfo) {
        LogUtil.i(TAG, "onEvtWbDocDel notify.");
        MeetingMgr.getInstance().handleWbDocDel(wbDelDocInfo);
    }

    @Override
    public void onEvtRecvChatMsg(TsdkConference tsdkConference, TsdkConfChatMsgInfo tsdkConfChatMsgInfo) {
        LogUtil.i(TAG, "onEvtRecvChatMsg notify.");
        MeetingMgr.getInstance().handleRecvChatMsg(tsdkConfChatMsgInfo);
    }

    @Override
    public void onEvtCtdStartCallResult(int callId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtCtdStartCallResult notify.");
        CtdMgr.getInstance().handleStartCallResult(callId, result);
    }

    @Override
    public void onEvtCtdEndCallResult(int callId, TsdkCommonResult result) {
        LogUtil.i(TAG, "onEvtCtdEndCallResult notify.");
    }

    @Override
    public void onEvtCtdCallStatusNotify(int callId, TsdkCtdCallStatus status) {
        LogUtil.i(TAG, "onEvtCtdCallStatusNotify notify.");
    }

    @Override
    public void onEvtSearchContactsResult(int querySeqNo, TsdkCommonResult result, TsdkSearchContactsResult searchContactResult) {
        LogUtil.i(TAG, "onEvtSearchContactsResult notify.");
        EnterpriseAddressBookMgr.getInstance().handleSearchContactResult(querySeqNo, result, searchContactResult);
    }

    @Override
    public void onEvtSearchDeptResult(int querySeqNo, TsdkCommonResult result, TsdkSearchDepartmentResult searchDeptResult) {
        LogUtil.i(TAG, "onEvtSearchDeptResult notify.");
        EnterpriseAddressBookMgr.getInstance().handleSearchDepartmentResult(querySeqNo, result, searchDeptResult);
    }

    @Override
    public void onEvtGetIconResult(int querySeqNo, TsdkCommonResult result, TsdkGetIconResult getIconResult) {
        LogUtil.i(TAG, "onEvtGetIconResult notify.");
        EnterpriseAddressBookMgr.getInstance().handleGetIconResult(querySeqNo, result, getIconResult);
    }

    @Override
    public void onEvtAddFriendInd(TsdkBeAddedFriendInfo beAddedFriendInfo) {

    }

    @Override
    public void onEvtUserStatusUpdate(List<TsdkImUserStatusUpdateInfo> userStatusInfoList) {

    }

    @Override
    public void onEvtUserInfoUpdate(List<TsdkImUserInfo> userInfoList) {
        LogUtil.i(TAG, "onEvtUserInfoUpdate notify.");
        //ImMgr.getInstance().handleUserInfoUpdate(userInfoList);
    }

    @Override
    public void onEvtJoinChatGroupReq(TsdkChatGroup chatGroup, TsdkReqJoinChatGroupMsg reqJoinChatGroupMsg) {
        LogUtil.i(TAG, "onEvtJoinChatGroupReq notify.");
    }

    @Override
    public void onEvtJoinChatGroupRsp(TsdkChatGroup chatGroup, TsdkRspJoinChatGroupMsg rspJoinChatGroupMsg) {
        LogUtil.i(TAG, "onEvtJoinChatGroupRsp notify.");
        //ImMgr.getInstance().handleJoinChatGroupRsp(chatGroup, rspJoinChatGroupMsg);
    }

    @Override
    public void onEvtJoinChatGroupInd(TsdkChatGroup chatGroup, TsdkBeAddedToChatGroupInfo beAddedToChatGroupInfo) {
        LogUtil.i(TAG, "onEvtJoinChatGroupInd notify.");
        //ImMgr.getInstance().handleJoinChatGroupInd(chatGroup, beAddedToChatGroupInfo);
    }

    @Override
    public void onEvtDelChatGroupMemberResult(TsdkChatGroup chatGroup, TsdkDelChatGroupMemberResult delChatGroupMemberResult) {
        LogUtil.i(TAG, "onEvtDelChatGroupMemberResult notify.");
    }

    @Override
    public void onEvtLeaveChatGroupResult(TsdkChatGroup chatGroup, TsdkLeaveChatGroupResult leaveChatGroupResult) {
        LogUtil.i(TAG, "onEvtLeaveChatGroupResult notify.");
        //ImMgr.getInstance().handleLeaveChatGroupResult(leaveChatGroupResult);
    }

    @Override
    public void onEvtChatGroupInfoUpdate(TsdkChatGroup chatGroup, TsdkChatGroupUpdateInfo chatGroupUpdateInfo, TsdkChatGroupInfoUpdateType updateType) {
        LogUtil.i(TAG, "onEvtChatGroupInfoUpdate notify.");
        //ImMgr.getInstance().handleChatGroupInfoUpdate(chatGroup, chatGroupUpdateInfo, updateType);
    }


    @Override
    public void onEvtInputtingStatusInd(TsdkInputtingStatusInfo inputtingStatusInfo) {

    }

    @Override
    public void onEvtChatMsg(TsdkChatMsgInfo chatMsgInfo) {

    }

    @Override
    public void onEvtBatchChatMsg(TsdkBatchChatMsgInfo batchChatMsgInfo) {

    }

    @Override
    public void onEvtSystemBulletin(TsdkChatMsgInfo chatMsgInfo) {

    }

    @Override
    public void onEvtSms(TsdkSmsInfo smsInfo) {

    }

    @Override
    public void onEvtUndeliverInd(TsdkChatMsgUndeliverInfo chatMsgUndeliverInfo) {

    }

    @Override
    public void onEvtMsgReadInd(TsdkMsgReadIndInfo msgReadIndInfo) {

    }

    @Override
    public void onEvtMsgSendResult(TsdkSendChatMsgResult sendChatMsgResult) {

    }

    @Override
    public void onEvtMsgWithdrawResult(TsdkChatMsgWithdrawResult chatMsgWithdrawResult) {

    }

    @Override
    public void onEvtMsgWithdrawInd(TsdkChatMsgWithdrawInfo chatMsgWithdrawInfo) {

    }

    @Override
    public void onEvtStatisticLocalQos(TsdkOnEvtStatisticLocalQos notify) {

    }

    @Override
    public void onEvtRecvCustomDataInd(TsdkOnEvtRecvCustomDataInd notify) {

    }

    @Override
    public void onEvtAsScreenKeyframe(TsdkOnEvtAsScreenKeyframe notify) {

    }

    @Override
    public void onEvtAsScreenFirstKeyframe(TsdkOnEvtAsScreenFirstKeyframe notify) {

    }

    @Override
    public void onEvtDataComponentLoadInd(TsdkOnEvtDataComponentLoadInd notify) {

    }

    @Override
    public void onEvtConfBaseInfoInd(TsdkOnEvtConfBaseInfoInd notify) {

    }

    @Override
    public void onEvtConfTokenMsgInd(TsdkConfTokenMsg tsdkConfTokenMsg) {
        LogUtil.i(TAG, "onEvtConfTokenMsgInd notify.");
        LogUtil.i(TAG,"msgType: "+tsdkConfTokenMsg.getMsgType()+", tokenType: "+tsdkConfTokenMsg.getTokenType());
    }

    @Override
    public void onEvtTransToConfResult(TsdkCall call, TsdkCommonResult result) {

    }

}

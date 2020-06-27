package com.zxwl.vclibrary.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.huawei.ecterminalsdk.base.TsdkConfRole
import com.huawei.opensdk.callmgr.CallConstant
import com.huawei.opensdk.callmgr.CallInfo
import com.huawei.opensdk.callmgr.CallMgr
import com.huawei.opensdk.callmgr.VideoMgr
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver
import com.huawei.opensdk.commonservice.util.LogUtil
import com.huawei.opensdk.demoservice.ConfBaseInfo
import com.huawei.opensdk.demoservice.ConfConstant
import com.huawei.opensdk.demoservice.MeetingMgr
import com.huawei.opensdk.demoservice.Member
import com.huawei.opensdk.esdk.common.IntentConstant
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.zxwl.vclibrary.R
import com.zxwl.vclibrary.adapter.ConfControlAdapter
import com.zxwl.vclibrary.bean.PoliceBean
import com.zxwl.vclibrary.util.*
import com.zxwl.vclibrary.util.sharedpreferences.SPStaticUtils
import com.zxwl.vclibrary.widget.dialog.CustomDialog
import com.zxwl.vclibrary.widget.dialog.OnDialogClickListener
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_voice_conf.*
import kotlinx.android.synthetic.main.include_bottom_videoconf_control.*
import kotlinx.android.synthetic.main.include_conf_member_list_control.*
import kotlinx.android.synthetic.main.include_top_memberlist_control.*
import kotlinx.android.synthetic.main.include_top_videoconf_control.*
import kotlinx.android.synthetic.main.include_top_videoconf_control.tvConfName
import kotlinx.android.synthetic.main.item_conf_control.*
import kotlinx.android.synthetic.main.popup_more_control.*
import me.jessyan.autosize.internal.CancelAdapt
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *  语音会议界面
 */
class VoiceConfActivity : BaseVcActivity(), View.OnClickListener, CancelAdapt {

    protected var broadcastNames = arrayOf(
        CustomBroadcastConstants.CONF_STATE_UPDATE,
        CustomBroadcastConstants.GET_DATA_CONF_PARAM_RESULT,
        CustomBroadcastConstants.DATA_CONFERENCE_JOIN_RESULT,
        CustomBroadcastConstants.ADD_LOCAL_VIEW,
        CustomBroadcastConstants.DEL_LOCAL_VIEW,
        CustomBroadcastConstants.DATE_CONFERENCE_START_SHARE_STATUS,
        CustomBroadcastConstants.DATE_CONFERENCE_END_SHARE_STATUS,
        CustomBroadcastConstants.UPGRADE_CONF_RESULT,
        CustomBroadcastConstants.UN_MUTE_CONF_RESULT,
        CustomBroadcastConstants.MUTE_CONF_RESULT,
        CustomBroadcastConstants.LOCK_CONF_RESULT,
        CustomBroadcastConstants.UN_LOCK_CONF_RESULT,
        CustomBroadcastConstants.ADD_ATTENDEE_RESULT,
        CustomBroadcastConstants.DEL_ATTENDEE_RESULT,
        CustomBroadcastConstants.MUTE_ATTENDEE_RESULT,
        CustomBroadcastConstants.UN_MUTE_ATTENDEE_RESULT,
        CustomBroadcastConstants.HAND_UP_RESULT,
        CustomBroadcastConstants.CANCEL_HAND_UP_RESULT,
        CustomBroadcastConstants.SET_CONF_MODE_RESULT,
        CustomBroadcastConstants.WATCH_ATTENDEE_CONF_RESULT,
        CustomBroadcastConstants.BROADCAST_ATTENDEE_CONF_RESULT,
        CustomBroadcastConstants.CANCEL_BROADCAST_CONF_RESULT,
        CustomBroadcastConstants.REQUEST_CHAIRMAN_RESULT,
        CustomBroadcastConstants.RELEASE_CHAIRMAN_RESULT,
        CustomBroadcastConstants.SPEAKER_LIST_IND,
        CustomBroadcastConstants.GET_CONF_END,
        CustomBroadcastConstants.ACTION_CALL_END,
        CustomBroadcastConstants.ADD_ATTENDEE_TO_CONF
    )

    private var mCallID = 0

    //通话接听的时间
    private var answerTime: Long = 0

    //会控的适配器
    private lateinit var confControlAdapter: ConfControlAdapter

    //自己是否是主席
    private var selfChiar = false;

    private val confReceiver =
        LocBroadcastReceiver { broadcastName, obj ->
            val result: Int
            LogUtil.i(TAG, "收到广播:$broadcastName，obj是：$obj")
            when (broadcastName) {
                CustomBroadcastConstants.CONF_STATE_UPDATE -> {
                    confStatusUpdate(obj)
                }

                CustomBroadcastConstants.ADD_LOCAL_VIEW -> {
                    mHandler.sendEmptyMessage(ADD_LOCAL_VIEW)
                }

                CustomBroadcastConstants.GET_DATA_CONF_PARAM_RESULT -> {
                    result = obj as Int
                    if (result != 0) {
                        showCustomToast("获取数据会议失败")
                        return@LocBroadcastReceiver
                    }
                    MeetingMgr.getInstance().joinDataConf()
                }

                CustomBroadcastConstants.GET_CONF_END -> {
                    finishActivity()
                }

                CustomBroadcastConstants.ACTION_CALL_END -> {
                    finishActivity()
                }

                //申请主席
                CustomBroadcastConstants.REQUEST_CHAIRMAN_RESULT -> {
                    mHandler.sendMessage(mHandler.obtainMessage(REQUEST_CHAIR_RESULT, obj as Int))
                }

                //释放主席
                CustomBroadcastConstants.RELEASE_CHAIRMAN_RESULT -> {
                    mHandler.sendMessage(mHandler.obtainMessage(RELEASE_CHAIR_RESULT, obj as Int))
                }

                //添加人员到会议中
                CustomBroadcastConstants.ADD_ATTENDEE_TO_CONF -> {
                    mHandler.sendMessage(
                        mHandler.obtainMessage(
                            ADD_ATTENDEE_TO_CONF,
                            obj as List<PoliceBean>
                        )
                    )
                }
            }
        }
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var memberList: List<Member>? = null
            when (msg.what) {
                //刷新与会者列表
                REFRESH_MEMBER_LIST -> {
                    memberList = msg.obj as MutableList<Member>
                    refreshMemberList(memberList)

                    tvMemberListConfName.text = getInConfNumberString(memberList)
                }

                //刷新自己的状态
                REFRESH_SELF_MEMBER -> {
                    memberList = msg.obj as List<Member>
                    for (member in memberList) {
                        if (member.isSelf) {
                            refreshSelfMemberStatus(member)
                            return
                        }
                    }
                }

                //设置会议名称
                SET_CONF_NAME -> {
                    var confName = getConfSubject()
                    tvConfName.setText(msg.obj as String)
                }

                //显示toast
                SHOW_TOAST -> ToastHelper.showShort(msg.obj as String)

                //申请主席
                REQUEST_CHAIR_RESULT -> {
                    requestChairManResult(msg)
                }

                //释放主席
                RELEASE_CHAIR_RESULT -> {
                    releseChairManResult(msg)
                }

                //刷新会议状态
                UPDATE_CONF_INFO -> {
                    val confInfo = getCurrentConfBaseInfo()

                    ivCloseAllMic.setImageResource(
                        if (confInfo?.isMuteAll
                                ?: false
                        ) R.mipmap.ic_close_mute_all else R.mipmap.ic_open_mute_all
                    )
                }

                //添加人员到会议中
                ADD_ATTENDEE_TO_CONF -> {
                    var addAttends = msg.obj as List<PoliceBean>
                    addMembersToConf(addAttends)
                }

                else -> {

                }
            }
        }
    }
    //true：控制栏显示，false：控制栏隐藏
    private var showControlView = true

    /**
     * 申请主席的对话框
     */
    private lateinit var requestChairDialog: CustomDialog
    /**
     * 结束会议的对话框
     */
    private lateinit var endConfDialog: CustomDialog
    /**
     * 离开会议的对话框
     */
    private lateinit var leaveConfDialog: CustomDialog
    /**
     * 释放主席的对话框
     */
    private lateinit var releaseChairDialog: CustomDialog
    private var subscribe: Disposable? = null

    /**
     * 会议状态更新
     */
    private fun confStatusUpdate(obj: Any?) {
        val conferenceID = obj as String

        //判断会议状态，如果会议结束，则关闭会议界面
        val confBaseInfo = getCurrentConfBaseInfo()
            ?: return

        LogUtil.i(TAG, "会议信息：$confBaseInfo")

        if (ConfConstant.ConfConveneStatus.DESTROYED == confBaseInfo.confState) {
            finishActivity()
            return
        }

        if (TextUtils.isEmpty(tvConfName.getText().toString())) {
            //显示会议名称
            mHandler.sendMessage(
                mHandler.obtainMessage(
                    SET_CONF_NAME,
                    confBaseInfo.subject
                )
            )
        }

        val memberList = MeetingMgr.getInstance().currentConferenceMemberList
            ?: return

        //刷新与会者列表
        mHandler.sendMessage(mHandler.obtainMessage(REFRESH_MEMBER_LIST, memberList))

        mHandler.sendMessageDelayed(
            mHandler.obtainMessage(
                REFRESH_SELF_MEMBER,
                memberList
            ), 1000
        )

        mHandler.sendEmptyMessage(UPDATE_CONF_INFO)
    }

    /**
     * 添加人员到会议中
     */
    private fun addMembersToConf(addAttends: List<PoliceBean>) {
        val fromIterable = Observable.fromIterable(addAttends)

        val interval = Observable.interval(600, TimeUnit.MILLISECONDS)

        Observable.zip(
            fromIterable,
            interval,
            object : BiFunction<PoliceBean, Long, Member> {
                override fun apply(policeBean: PoliceBean, t2: Long): Member {
                    var member: Member? = null
                    member = Member()
                    member.displayName = policeBean.userName
                    member.number = policeBean.uri
                    member.accountId = policeBean.uri
                    member.role = TsdkConfRole.TSDK_E_CONF_ROLE_ATTENDEE
                    return member
                }
            })
            .compose(CustomCompose())
            .subscribe({
                LogUtils.i(
                    "呼叫的是：${it.toString()},当前时间是:${DateUtil.getCurrentTime(
                        DateUtil.FORMAT_DATE_TIME_SECOND
                    )}"
                )
                addAttendee(it!!)
            })
    }

    /**
     * 获取在线人数
     */
    private fun getInConfNumberString(memberList: MutableList<Member>): String {
        val filter = memberList.filter {
            it.isInConf
        }
        return "在线人数:(${filter.size}/${memberList.size})"
    }

    override fun initData() {
        StatusBarUtils.setTranslucentForImageView(this)

        ImmersionBar
            .with(this)
            .hideBar(BarHide.FLAG_HIDE_BAR)
            .init()


        //是否需要自动接听
        SPStaticUtils.put(Constants.IS_AUTO_ANSWER, false)

        //保持屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val intent = intent

        val callInfo = CallInfo()

        callInfo.isVideoCall = true

        NotificationUtils.sendNotif(
            callInfo,
            NotificationUtils.VOICE_ID,
            IntentConstant.VOICE_CONF_MANAGER_ACTIVITY_ACTION,
            false
        )

        LocBroadcast.getInstance().registerBroadcast(confReceiver, broadcastNames)

        hideVideoButton()

        initControlAdapter()

        initTimeSubscribe()


    }

    private fun hideVideoButton() {
        tvCloseCamera.isVisible = false
        tvSwitchCamera.isVisible = false
        tvOpenSamll.isVisible = false

        ivAddMember.setImageResource(R.mipmap.ic_more)
        ivRequestChair.isVisible = false
        ivDelayConf.isVisible = false
    }

    private fun initControlAdapter() {
        confControlAdapter = ConfControlAdapter(mutableListOf())
        confControlAdapter.setOnItemChildClickListener { adapter, view, position ->
            itemControlClick(view, position)
        }
        confControlAdapter.selfIsChair = isChairMan()
        confControlAdapter.isVideoConf = false
        rvList.adapter = confControlAdapter
        rvList.layoutManager = LinearLayoutManager(this)
    }

    private fun itemControlClick(view: View, position: Int) {
        val item = confControlAdapter.getItem(position)
        when (view.tag) {
            //呼叫
            ivItemRemove.tag -> {
                if (!isChairMan()) {
                    return
                }
                if (item!!.isInConf) {
                    removeAttendee(item)
                } else {
                    addAttendee(item)
                }
            }

            //麦克静音
            ivItemCloseMic.tag -> {
                if (!isChairMan()) {
                    return
                }
                muteAttendee(item!!, !item.isMute)
            }
        }
    }

    /**
     * 移除与会者
     */
    private fun removeAttendee(member: Member) {
        val result = MeetingMgr.getInstance().removeAttendee(member)
        if (0 != result) {
            ToastHelper.showShort("挂断${member.displayName}失败")
            result
        }
        ToastHelper.showShort("挂断${member.displayName}成功")
    }

    /**
     * 移除与会者
     */
    private fun addAttendee(member: Member) {
        val result = MeetingMgr.getInstance().addAttendee(member)
        if (0 != result) {
            ToastHelper.showShort("呼叫${member.displayName}失败")
            result
        }
        ToastHelper.showShort("呼叫${member.displayName}成功")
    }

    /**
     * 静音与会者
     * @param mute  true:静音，false:取消
     */
    private fun muteAttendee(member: Member, mute: Boolean) {
        val result = MeetingMgr.getInstance().muteAttendee(member, mute)
        if (0 != result) {
            ToastHelper.showShort("${if (mute) "静音" else "取消静音"}${member.displayName}失败")
            result
        }
        ToastHelper.showShort("${if (mute) "静音" else "取消静音"}${member.displayName}成功")
    }

    /**
     * 延长会议
     */
    private fun postponeConf(time: Int) {
        val result = MeetingMgr.getInstance().postpone(time)
        if (0 != result) {
            ToastHelper.showShort("会议延长失败")
            result
        }
        ToastHelper.showShort("会议延长${time}分钟")
    }

    /**
     * 静音会议
     */
    private fun muteConf(muteConf: Boolean?) {
        val result = MeetingMgr.getInstance().muteConf(muteConf ?: false)
        if (0 != result) {
            ToastHelper.showShort("静音会议失败")
            result
        }
        ToastHelper.showShort("静音会议成功")
    }

    override fun setListener() {
        ivBg.setOnClickListener(this)
        tvCloseMic.setOnClickListener(this)
        tvCloseLouder.setOnClickListener(this)
        tvHangUp.setOnClickListener(this)
        tvOpenControl.setOnClickListener(this)

        ivMemberListBack.setOnClickListener(this)
        ivAddMember.setOnClickListener(this)
        ivRequestChair.setOnClickListener(this)
        ivDelayConf.setOnClickListener(this)
        ivCloseAllMic.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        voiceLayoutClick(v)
        memberListClick(v)
    }

    /**
     * 与会者列表界面点击事件
     */
    private fun memberListClick(v: View?) {
        when (v) {
            //返回
            ivMemberListBack -> {
                switchMemberlistShow(false)
            }

            //添加与会者
            ivAddMember -> {
                showControlPopupWindow()
            }

            //申请主席
            ivRequestChair -> {
                dismissMorePopupWindow()
                if (isChairMan()) {
                    showReleaseChairDialog()
                    return
                }
                showRequestChairDialg("会议中已存在主席")
            }

            //延迟会议
            ivDelayConf -> {
                dismissMorePopupWindow()

                if (isChairMan()) {
                    postponeConf(60)
                    return
                }

                showRequestChairDialg("会议中已存在主席,请联系主席控制")
            }

            //静音会议
            ivCloseAllMic -> {
                if (isChairMan()) {
                    val muteAll = getCurrentConfBaseInfo()?.isMuteAll ?: false
                    muteConf(
                        !muteAll
                    )
                    return
                }

                showRequestChairDialg("会议中已存在主席,请联系主席控制")
            }
        }
    }


    /**
     * 会议主界面按钮点击事件
     */
    private fun voiceLayoutClick(v: View?) {
        when (v) {
            //显示隐藏控制栏
            ivBg -> {
                switchShowControl()
            }

            //切换麦克风
            tvCloseMic -> {
                muteSelf()
            }

            //免提切换
            tvCloseLouder -> {
                updateLoudSpeakerStatus(switchLoudSpeaker())
            }

            //显示会控界面
            tvOpenControl -> {
                switchMemberlistShow(true)
            }

            //挂断
            tvHangUp -> {
                hangUpClick()
            }
        }
    }

    /**
     * 切换与会者列表的显示
     */
    private fun switchMemberlistShow(show: Boolean) {
        clMemberlist.isVisible = show

        ImmersionBar.with(this)
            // 默认状态栏字体颜色为黑色
            // 解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
            .keyboardEnable(true)

        ImmersionBar.setTitleBar(this, if (show) clMemberlistTopControl else clVideoTopLayout)
    }

    override fun getLayoutId(): Int = R.layout.activity_voice_conf

    private fun finishActivity() {
        finishAndRemoveTask()
    }

    override fun onResume() {
        super.onResume()

        // 刷新当前扬声器状态
        updateLoudSpeakerStatus(CallMgr.getInstance().currentAudioRoute)
    }

    override fun onDestroy() {
        super.onDestroy()

        NotificationUtils.cancel(NotificationUtils.VOICE_ID)

        LocBroadcast.getInstance().unRegisterBroadcast(confReceiver, broadcastNames)

        disposeTimeSubscribe()
    }

    override fun onBackPressed() {
        //super.onBackPressed();
    }

    /**
     * 挂断点击
     */
    fun hangUpClick() {
        if (isChairMan()) {
            showEndConfDialog()
            return
        }
        showLeaveConfDialog()
    }

    /**
     * 释放主席结果
     */
    private fun releseChairManResult(msg: Message) {
        var result = msg.obj as Int
        if (result != 0) {
            ToastHelper.showShort("释放主席失败")
            return
        }
        ToastHelper.showShort("释放主席成功")
    }

    /**
     * 申请主席结果
     */
    private fun requestChairManResult(msg: Message) {
        var result = msg.obj as Int
        if (result != 0) {
            ToastHelper.showShort("申请主席失败")
            return
        }
        ToastHelper.showShort("申请主席成功")
    }

    /**
     * 刷新与会者列表
     *
     * @param memberList
     */
    private fun refreshMemberList(newMemberList: MutableList<Member>?) {
        if (null != newMemberList) {
            confControlAdapter.selfIsChair = isChairMan()
            confControlAdapter.replaceData(newMemberList)
        }
    }

    /**
     * 刷新自己的状态
     *
     * @param selfMember
     */
    private fun refreshSelfMemberStatus(selfMember: Member) {
        //是否是主席
        val isChairMan = selfMember.isChair

        //更新扬声器状态
        updateLoudSpeakerStatus(selfMember.isMute)
    }

    /**
     * 更新麦克风的状态
     *
     * @param isCloseMic true：麦克风关闭，false：麦克风打开
     */
    private fun updateLoudSpeakerStatus(isCloseMic: Boolean) {
        //更新状态静音按钮状态
        tvCloseMic.setCompoundDrawablesWithIntrinsicBounds(
            0,
            if (isCloseMic) R.mipmap.ic_close_mic else R.mipmap.ic_open_mic,
            0,
            0
        )
    }

    /**
     * 切换状态栏显示
     */
    private fun switchShowControl() {
        if (showControlView) {
            hideControl()
        } else {
            showControl()
        }
    }

    private fun showControl() {
        flControl.setVisibility(View.VISIBLE)
        getViewAlphaAnimator(flControl, 1f).start()
    }

    private fun hideControl() {
        getViewAlphaAnimator(flControl, 0f).start()
    }

    private fun getViewAlphaAnimator(view: View, alpha: Float): ViewPropertyAnimator {
        val viewPropertyAnimator = view.animate().alpha(alpha).setDuration(300)
        viewPropertyAnimator.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.visibility = if (alpha > 0) View.VISIBLE else View.GONE
                showControlView = if (alpha > 0) true else false
            }
        })
        return viewPropertyAnimator
    }

    /**
     * 静音自己
     *
     * @return
     */
    fun muteSelf() {
        val self = getSelf() ?: return
        muteAttendee(self, !self.isMute)
    }

    /**
     * 切换扬声器
     *
     * @return
     */
    fun switchLoudSpeaker(): Int {
        return CallMgr.getInstance().switchAudioRoute()
    }

    /**
     * 更新扬声器的状态
     *
     * @param type
     */
    private fun updateLoudSpeakerStatus(type: Int) {
        tvCloseLouder.setCompoundDrawablesWithIntrinsicBounds(
            0,
            if (type == CallConstant.TYPE_LOUD_SPEAKER) R.mipmap.ic_open_louder else R.mipmap.ic_close_louder,
            0,
            0
        )
    }

    fun getConfSubject(): String? {
        val currentConfBaseInfo = getCurrentConfBaseInfo()
        return if (currentConfBaseInfo == null) {
            null
        } else {
            currentConfBaseInfo.subject ?: currentConfBaseInfo.confID
        }
    }

    /**
     *  获取当前的会议
     */
    fun getCurrentConfBaseInfo(): ConfBaseInfo? {
        return MeetingMgr.getInstance().currentConferenceBaseInfo
    }

    /**
     * 离开会议
     */
    fun leaveConf() {
        val result = MeetingMgr.getInstance().leaveConf()
        if (result != 0) {
            showCustomToast("离开会议失败")
            return
        }
        LocBroadcast.getInstance().unRegisterBroadcast(confReceiver, broadcastNames)
        finishActivity()
    }

    /**
     * 结束会议
     */
    fun endConf() {
        val result = MeetingMgr.getInstance().endConf()
        if (result != 0) {
            showCustomToast("结束会议失败")
            return
        }

        LocBroadcast.getInstance().unRegisterBroadcast(confReceiver, broadcastNames)
        finishActivity()
    }

    /**
     * 显示申请主席的对话框
     */
    private fun showRequestChairDialg(hintContent: String) {
        if (hasChairMan()) {
            ToastHelper.showShort(hintContent)
        } else {
            showRequestChairDialog()
        }
    }

    /**
     * 申请主席
     */
    private fun showRequestChairDialog() {
        requestChairDialog = CustomDialog(this)
        requestChairDialog.dialogClickListener = object : OnDialogClickListener {
            override fun onConfirmClickListener(content: String) {
                requestChairman(content)
            }

            override fun onConfirmClickListener() {

            }

            override fun onCancleClickListener() {

            }
        }
        requestChairDialog.show()
    }

    /**
     * 申请主席
     *
     * @param content
     */
    private fun requestChairman(chairmanPassword: String) {
        val result = MeetingMgr.getInstance().requestChairman(chairmanPassword)
        if (result != 0) {
            ToastHelper.showShort("申请主席失败")
            return
        }
    }

    /**
     * 结束会议
     */
    private fun showEndConfDialog() {
        endConfDialog = CustomDialog(this, 2)
        endConfDialog.dialogClickListener = object : OnDialogClickListener {
            override fun onConfirmClickListener(content: String) {}

            override fun onConfirmClickListener() {
                endConf()
            }

            override fun onCancleClickListener() {
                leaveConf()
            }
        }
        endConfDialog.show()
    }

    /**
     * 离开会议
     */
    private fun showLeaveConfDialog() {
        leaveConfDialog = CustomDialog(this, 3)
        leaveConfDialog.dialogClickListener = object : OnDialogClickListener {
            override fun onConfirmClickListener(content: String) {}

            override fun onConfirmClickListener() {
                leaveConf()
            }

            override fun onCancleClickListener() {

            }
        }
        leaveConfDialog.show()
    }

    /**
     * 释放主席
     */
    private fun showReleaseChairDialog() {
        releaseChairDialog = CustomDialog(this, 1)
        releaseChairDialog.dialogClickListener = object : OnDialogClickListener {
            override fun onConfirmClickListener(content: String) {

            }

            override fun onConfirmClickListener() {
                releaseChairman()
            }

            override fun onCancleClickListener() {

            }
        }
        releaseChairDialog.show()
    }

    /**
     * 申请主席
     */
    private fun requestChairMan() {
        if (isChairMan()) {
            showReleaseChairDialog()
        } else {
            if (hasChairMan()) {
                ToastHelper.showShort("会议中已存在主席")
                return
            }
            showRequestChairDialog()
        }
    }

    /**
     * 释放主席
     */
    private fun releaseChairman() {
        val result = MeetingMgr.getInstance().releaseChairman()
        if (result != 0) {
            ToastHelper.showShort("释放主席失败")
            return
        }
    }

    /**
     * 是否是主席
     *
     * @return true:主席
     */
    fun isChairMan(): Boolean {
        val self = getSelf() ?: return false
        return self.role == TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN
    }

    /**
     * 是否存在主席
     * @return  true：存在
     */
    private fun hasChairMan(): Boolean {
        val getMemberList = getMemberList()
        var hasChairMan = false
        for (member in getMemberList) {
            if (member.role == TsdkConfRole.TSDK_E_CONF_ROLE_CHAIRMAN) {
                hasChairMan = true
                return hasChairMan
            }
        }
        return hasChairMan
    }

    /**
     * 获取自身的数据
     *
     * @return
     */
    private fun getSelf(): Member? {
        return MeetingMgr.getInstance().currentConferenceSelf
    }

    /**
     * 获取与会者列表
     *
     * @return
     */
    private fun getMemberList(): List<Member> {
        var memberList = MeetingMgr.getInstance().currentConferenceMemberList
        if (null == memberList) {
            memberList = ArrayList()
        }
        return memberList
    }

    private fun showCustomToast(content: String) {
        mHandler.sendMessage(mHandler.obtainMessage(SHOW_TOAST, content))
    }

    private fun disposeTimeSubscribe() {
        if (null != subscribe) {
            if (!subscribe!!.isDisposed) {
                subscribe!!.dispose()
                subscribe = null
            }
        }
    }

    /**
     * 会议持续时间
     */
    private fun initTimeSubscribe() {
        answerTime = CallMgr.getInstance().voiceAnswerTime
        if (0L == answerTime) {
            answerTime = System.currentTimeMillis()
        }
        subscribe = Flowable
            .interval(1, TimeUnit.SECONDS)
            //.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val confDuration = System.currentTimeMillis() - answerTime

                tvConfTime.setText(DateUtil.formatTimeFString(confDuration / 1000))
            }
    }


    private var moreControlPopupWindow: PopupWindow? = null
    private lateinit var tvRequestChair: TextView
    private lateinit var tvAddMember: TextView
    private lateinit var tvDelayConf: TextView

    /**
     * 显示更多会控的popup
     */
    private fun showControlPopupWindow() {
        if (null == moreControlPopupWindow) {
            initMoreControlPopup()
        }

        tvRequestChair.text = if (isChairMan()) "释放主席" else "申请主席"

        tvRequestChair.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (isChairMan()) R.mipmap.ic_release_chairman else R.mipmap.ic_request_chairman,
            0,
            0,
            0
        )

        moreControlPopupWindow?.showAsDropDown(ivAddMember)
    }

    private fun dismissMorePopupWindow() {
        if (null != moreControlPopupWindow && moreControlPopupWindow!!.isShowing) {
            moreControlPopupWindow?.dismiss()
        }
    }

    /**
     * 初始化更多popupwindow
     */
    private fun initMoreControlPopup() {
        val moreControlPopupView = View.inflate(this, R.layout.popup_more_control, null)
        tvRequestChair = moreControlPopupView.findViewById<View>(R.id.tvRequestChair) as TextView
        tvAddMember = moreControlPopupView.findViewById<View>(R.id.tvAddMember) as TextView
        tvDelayConf = moreControlPopupView.findViewById<View>(R.id.tvDelayConf) as TextView

        tvRequestChair.setOnClickListener {
            dismissMorePopupWindow()
            if (isChairMan()) {
                showReleaseChairDialog()
                return@setOnClickListener
            }
            showRequestChairDialg("会议中已存在主席")
        }

        tvAddMember.setOnClickListener {
            dismissMorePopupWindow()

            if (isChairMan()) {
                val noSelectPeoples = confControlAdapter.data
                SelectPeopleActivity.startActivity(this, noSelectPeoples)
                return@setOnClickListener
            }

            showRequestChairDialg("会议中已存在主席,请联系主席控制")
        }

        tvDelayConf.setOnClickListener {
            dismissMorePopupWindow()

            if (isChairMan()) {
                postponeConf(60)
                return@setOnClickListener
            }

            showRequestChairDialg("会议中已存在主席,请联系主席控制")
        }

        moreControlPopupWindow = PopupWindow(
            moreControlPopupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        moreControlPopupWindow?.isOutsideTouchable = true
        moreControlPopupWindow?.isFocusable = true
    }

    companion object {
        val TAG = VoiceConfActivity::class.java.simpleName

        //添加view
        private val ADD_LOCAL_VIEW = 101

        //刷新与会者列表
        private val REFRESH_MEMBER_LIST = 102

        //刷新自己的状态
        private val REFRESH_SELF_MEMBER = 103

        //设置会议名称
        private val SET_CONF_NAME = 104

        //显示toast
        private val SHOW_TOAST = 105

        //申请主席结果
        private val REQUEST_CHAIR_RESULT = 106

        //释放主席结果
        private val RELEASE_CHAIR_RESULT = 107

        //刷新会议状态
        private val UPDATE_CONF_INFO = 108

        //添加人员到会议中
        private val ADD_ATTENDEE_TO_CONF = 109
    }
}
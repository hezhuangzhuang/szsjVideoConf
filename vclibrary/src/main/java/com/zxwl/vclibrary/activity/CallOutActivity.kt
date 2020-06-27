package com.zxwl.vclibrary.activity

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.huawei.opensdk.callmgr.CallMgr
import com.zxwl.vclibrary.R
import kotlinx.android.synthetic.main.activity_call_out.*
import me.jessyan.autosize.internal.CancelAdapt

/**
 * 外呼界面
 */
class CallOutActivity : BaseMediaActivity(), CancelAdapt {

    override fun getLayoutId(): Int = R.layout.activity_call_out

    override fun setListener() {
        super.setListener()

        tvNumber.text = mCallNumber.toString() ?: ""

        if ("+991117".equals(mCallNumber)) {
            tvCallContent.text = "正在加入会议..."
        } else {
            tvCallContent.text = if (TextUtils.isEmpty(mCallNumber)) "" else mCallNumber
        }

        tvHangUp.setOnClickListener {
            CallMgr.getInstance().endCall(mCallID)
            finish()
        }
    }
}
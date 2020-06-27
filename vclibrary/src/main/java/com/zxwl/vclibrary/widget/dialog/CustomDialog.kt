package com.zxwl.vclibrary.widget.dialog

import android.content.Context
import android.graphics.Color
import android.text.Selection
import android.text.Spannable
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.NonNull
import com.zxwl.vclibrary.R
import com.zxwl.vclibrary.util.ToastHelper

class CustomDialog : BaseDialog {
    private var title: TextView? = null
    private var tvHint: TextView? = null

    private var etAccessCode: EditText? = null
    private var ll_pwd: LinearLayout? = null
    private var ivShowPwd: ImageView? = null
    private var btCancle: Button? = null
    private var btConfirm: Button? = null
    private var view_line: View? = null

    //释放主席
    private val releaseChair = true

    //0:申请主席，1:释放主席，2:结束会议，3:离开会议,4：加入会议,5：悬浮框权限，6:移除与会者，7:强制分享
    private var controlType: Int = 0

    private lateinit var name: String

    constructor(@NonNull context: Context) : super(context)

    constructor(@NonNull context: Context, controlType: Int) : this(context) {
        this.controlType = controlType
    }

    constructor(
        @NonNull context: Context,
        controlType: Int,
        name: String
    ) : this(context, controlType) {
        this.controlType = controlType
        this.name = name
    }

    override fun bindLayout(): Int {
        return R.layout.dialog_request_chair
    }

    override fun initView(dialog: BaseDialog, contentView: View) {
        title = contentView.findViewById(R.id.title) as TextView
        tvHint = contentView.findViewById(R.id.tv_hint) as TextView
        ll_pwd = contentView.findViewById(R.id.ll_pwd) as LinearLayout
        etAccessCode = contentView.findViewById(R.id.et_accessCode) as EditText
        ivShowPwd = contentView.findViewById(R.id.iv_show_pwd) as ImageView
        btCancle = contentView.findViewById(R.id.bt_cancle) as Button
        btConfirm = contentView.findViewById(R.id.btConfirm) as Button
        view_line = contentView.findViewById(R.id.view_line) as View

        etAccessCode!!.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) {
                view_line!!.setBackgroundColor(Color.parseColor("#007aff"))
            } else {
                view_line!!.setBackgroundColor(Color.parseColor("#cccccc"))
            }
        }

        when (controlType) {
            //申请呼吸
            0 -> {
                title!!.text = "视频会议"
                tvHint!!.text = "请输入主持人密码"
                tvHint!!.visibility = View.VISIBLE
                etAccessCode!!.hint = "主持人密码"
            }

            //释放主席
            1 -> {
                title!!.text = "视频会议"
                tvHint!!.text = "您确定要释放主持人吗?"
                tvHint!!.visibility = View.VISIBLE
                ll_pwd!!.visibility = View.GONE
                view_line!!.visibility = View.GONE
            }

            //结束会议
            2 -> {
                title!!.text = "视频会议"
                tvHint!!.text = "结束会议或退出会议?"
                tvHint!!.visibility = View.VISIBLE
                ll_pwd!!.visibility = View.GONE
                view_line!!.visibility = View.GONE
                btCancle!!.text = "退出会议"
                btConfirm!!.text = "结束会议"
            }

            //离开会议
            3 -> {
                title!!.text = "视频会议"
                tvHint!!.text = "是否离开会议?"
                tvHint!!.visibility = View.VISIBLE
                ll_pwd!!.visibility = View.GONE
                view_line!!.visibility = View.GONE
                btConfirm!!.text = "离开会议"
            }

            //加入会议
            4 -> {
                title!!.text = "视频会议"
                tvHint!!.text = "会议密码"
                etAccessCode!!.hint = "密码"
            }

            //请求悬浮框权限
            5 -> {
                title!!.text = "视频会议"
                tvHint!!.text = "是否跳转至悬浮框权限设置界面？"
                tvHint!!.visibility = View.VISIBLE
                ll_pwd!!.visibility = View.GONE
                view_line!!.visibility = View.GONE
            }

            //移除与会者
            6 -> {
                title!!.text = "视频会议"
                tvHint!!.text = "是否删除$name?"
                tvHint!!.visibility = View.VISIBLE
                ll_pwd!!.visibility = View.GONE
                view_line!!.visibility = View.GONE
            }

            //强制分享
            7 -> {
                title!!.text = "视频会议"
                tvHint!!.text = "此操作将停止另一方共享，是否确实要继续?"
                tvHint!!.visibility = View.VISIBLE
                ll_pwd!!.visibility = View.GONE
                view_line!!.visibility = View.GONE
            }
        }

        btConfirm!!.setOnClickListener(View.OnClickListener {
            if (null != dialogClickListener) {
                if (0 == controlType || 4 == controlType) {
                    val pwd = etAccessCode!!.text.toString().trim { it <= ' ' }
                    if (TextUtils.isEmpty(pwd)) {
                        ToastHelper.showShort("密码不能为空")
                        return@OnClickListener
                    }
                    dialogClickListener!!.onConfirmClickListener(pwd)
                    etAccessCode!!.setText("")
                } else {
                    dialogClickListener!!.onConfirmClickListener()
                }
                dismiss()
            }
        })

        btCancle!!.setOnClickListener {
            if (null != dialogClickListener) {
                dialogClickListener!!.onCancleClickListener()
            }
            dismiss()
        }

        ivShowPwd!!.setOnClickListener {
            isShowPwd = !isShowPwd
            if (isShowPwd) {
                //如果选中，显示密码
                etAccessCode!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ivShowPwd!!.setImageResource(R.mipmap.ic_pwd_show)
            } else {
                //否则隐藏密码
                etAccessCode!!.transformationMethod = PasswordTransformationMethod.getInstance()
                ivShowPwd!!.setImageResource(R.mipmap.ic_pwd_hide)
            }
            setSelection(etAccessCode!!)
        }
    }

    override fun setWindowStyle(window: Window) {
        window.setBackgroundDrawableResource(R.drawable.shape_white_bg_tran_5)
    }

    private var isShowPwd: Boolean = false

    private fun setSelection(editText: EditText) {
        //设置光标位置
        val text = editText.text
        if (text is Spannable) {
            val spanText = text as Spannable
            Selection.setSelection(spanText, text.length)
        }
    }

    override fun dismiss() {
        super.dismiss()

        isShowPwd = false
        etAccessCode!!.setText("")
    }

    lateinit var dialogClickListener: OnDialogClickListener

//   lateinit var dialogClickListener: OnDialogClickListener
//
//    fun setDialogClickListener(dialogClickListener: OnDialogClickListener) {
//        this.dialogClickListener = dialogClickListener
//    }
}

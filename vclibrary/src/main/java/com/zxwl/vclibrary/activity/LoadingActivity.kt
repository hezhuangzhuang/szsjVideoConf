package com.zxwl.vclibrary.activity

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.view.animation.LinearInterpolator
import com.zxwl.vclibrary.R
import com.zxwl.vclibrary.util.Constants
import com.zxwl.vclibrary.util.sharedpreferences.SPStaticUtils
import kotlinx.android.synthetic.main.activity_loading.*
import me.jessyan.autosize.internal.CancelAdapt

/**
 * 等待的界面
 */
class LoadingActivity : BaseMediaActivity(), CancelAdapt {
    companion object {
        fun startActivty(context: Context) {
            var intent = Intent(context, LoadingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_loading

    override fun initData() {
        super.initData()
    }

    override fun setListener() {
        super.setListener()
    }

    override fun onResume() {
        super.onResume()
        startRotationAnimator()
    }

    override fun onPause() {
        super.onPause()

        //停止动画
        stopRotationAnimator()
    }

    override fun onDestroy() {
        super.onDestroy()

        //是否需要自动接听
        SPStaticUtils.put(Constants.IS_AUTO_ANSWER, false)

        //是否是加入会议
        SPStaticUtils.put(Constants.JOIN_CONF, false)
    }

    private lateinit var rotationAnimator: ObjectAnimator

    /**
     * 启动动画
     */
    private fun startRotationAnimator() {
        rotationAnimator = ObjectAnimator.ofFloat(ivLoading, "rotation", 0f, 360f)
        rotationAnimator.setDuration(1500)
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE)
        rotationAnimator.setInterpolator(LinearInterpolator())
        rotationAnimator.start()
    }

    /**
     * 停止动画
     */
    private fun stopRotationAnimator() {
        if (null != rotationAnimator) {
            rotationAnimator.cancel()
        }
    }


}
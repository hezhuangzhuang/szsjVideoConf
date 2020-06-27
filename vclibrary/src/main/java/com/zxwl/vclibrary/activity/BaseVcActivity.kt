package com.zxwl.vclibrary.activity

import android.app.KeyguardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.huawei.opensdk.esdk.utils.ActivityStack

abstract class BaseVcActivity : AppCompatActivity() {
    /**
     * 初始化view的数据
     */
    protected abstract fun initData()

    /**
     * 设置view的监听事件
     */
    protected abstract fun setListener()

    /**
     * 获得布局layout id
     *
     * @return
     */
    protected abstract fun getLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //锁屏时弹出界面
        showWhenLocked()
        setContentView(getLayoutId())

        ActivityStack.getIns().push(this)
        initData()
        setListener()
    }

    override fun onDestroy() {
        // 结束Activity&从堆栈中移除
        ActivityStack.getIns().popup(this)
        super.onDestroy()
    }

    /**
     * 锁屏时弹出界面
     */
    protected fun showWhenLocked() {
        //TODO：在锁屏时弹出界面
        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val showingLocked = km.inKeyguardRestrictedInputMode()
        //是否有锁屏
        if (showingLocked) {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}

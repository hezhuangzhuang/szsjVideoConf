package com.hw.baselibrary.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hw.baselibrary.common.IBaseView
import com.hw.baselibrary.ui.activity.BaseActivity
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.widgets.ProgressLoading
import com.trello.rxlifecycle2.components.support.RxFragment

/**
 *author：pc-20171125
 *data:2019/11/8 09:31
 */
 abstract class BaseFragment : RxFragment(), IBaseView{

    companion object {
        private const val TAG = "BaseFragment"
        private const val STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN"
    }

    protected lateinit var mActivity: BaseActivity
    protected lateinit var mInflater: LayoutInflater
    protected lateinit var mContentView: View
    protected lateinit var mLoadingDialog: ProgressLoading

    /** 标题栏对象  */
    protected var mTitleBar: TitleBar? = null

    /** 状态栏沉浸  */
    private lateinit var mImmersionBar: ImmersionBar

    /**
     * 获取标题栏 id
     */
    protected fun getTitleId(): Int {
        return 0
    }

    /**
     * 获得全局的 Activity
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
//        mActivity = context as Activity

        mActivity = context as BaseActivity

        mLoadingDialog  = mActivity!!.mLoadingDialog

    }

    abstract fun initData(bundle: Bundle?)

    abstract fun bindLayout(): Int

    abstract fun initView(savedInstanceState: Bundle?, contentView: View)

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            val isSupportHidden = it.getBoolean(STATE_SAVE_IS_HIDDEN)
            fragmentManager?.beginTransaction()?.let {
                if (isSupportHidden) {
                    it.hide(this).commitAllowingStateLoss()
                } else {
                    it.show(this).commitAllowingStateLoss()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        mInflater = inflater
        setRootLayout(bindLayout())
        return mContentView
    }

    fun setRootLayout(layoutId: Int) {
        if (layoutId <= 0) return
        mContentView = mInflater.inflate(layoutId, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        initData(bundle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
//        mLoadingDialog = ProgressLoading.create(mActivity)
        initView(savedInstanceState, mContentView)
        initTitleBar()
        initImmersion()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        (mContentView.parent as ViewGroup).removeView(mContentView)
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "onSaveInstanceState")
        super.onSaveInstanceState(outState)
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: ")
        super.onDestroy()
    }

    fun <T : View> findViewById(@IdRes id: Int): T {
        return mContentView.findViewById(id)
    }

    /*
        显示加载框，默认实现
     */
    override fun showLoading() {
        mLoadingDialog.showLoading()
    }

    /*
        隐藏加载框，默认实现
     */
    override fun dismissLoading() {
        mLoadingDialog.hideLoading()
    }

    protected fun initTitleBar() {
        if (getTitleId() > 0) {
            // 勤快模式
            val view = findViewById(getTitleId()) as View
            if (view is TitleBar) {
                mTitleBar = view as TitleBar
            }
        } else if (getTitleId() == 0 && view is ViewGroup) {
            // 懒人模式
            mTitleBar = (mActivity as BaseActivity).findTitleBar(view as ViewGroup)
        }
    }

    /**
     * 初始化沉浸式
     */
    protected fun initImmersion() {
        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            statusBarConfig().init()

            // 设置标题栏沉浸
            if (getTitleId() > 0) {
                ImmersionBar.setTitleBar(this, findViewById(getTitleId()) as View)
            } else if (mTitleBar != null) {
                ImmersionBar.setTitleBar(this, mTitleBar)
            }
        }
    }

    /**
     * 是否在Fragment使用沉浸式
     */
    open fun isStatusBarEnabled(): Boolean {
        return false
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    protected fun getStatusBarConfig(): ImmersionBar {
        return mImmersionBar
    }

    /**
     * 初始化沉浸式
     */
    private fun statusBarConfig(): ImmersionBar {
        //在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this)
            // 默认状态栏字体颜色为黑色
            .statusBarDarkFont(statusBarDarkFont())
            // 解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
            .keyboardEnable(true)
        return mImmersionBar
    }

    /**
     * 获取状态栏字体颜色
     */
    open fun statusBarDarkFont(): Boolean {
        // 返回真表示黑色字体
        return true
    }
}
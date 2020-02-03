package com.hw.baselibrary.ui.activity

import android.app.Activity
import android.os.Bundle
import android.view.*
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.hw.baselibrary.common.AppManager
import com.hw.baselibrary.common.IBaseView
import com.hw.baselibrary.widgets.ProgressLoading
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

/**
 *author：pc-20171125
 *data:2019/11/8 09:51
 */
abstract class BaseActivity : RxAppCompatActivity(), IBaseView {
    protected lateinit var mContentView: View
    protected lateinit var mActivity: Activity

    public lateinit var mLoadingDialog: ProgressLoading

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

    companion object {
        const val TAG = "BaseActivity"
    }

    abstract fun initData(bundle: Bundle?)

    abstract fun bindLayout(): Int

    abstract fun initView(savedInstanceState: Bundle?, contentView: View)

    abstract fun doBusiness()

    abstract fun setListeners()

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivity = this
        super.onCreate(savedInstanceState)
        //初次进入界面时隐藏软键盘
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initData(intent.extras)
        setRootLayout(bindLayout())
        initView(savedInstanceState, mContentView)
        doBusiness()
        setListeners()
        mLoadingDialog = ProgressLoading.create(this)
        AppManager.instance.addActivity(this)
    }

    fun setRootLayout(layoutId: Int) {
        if (layoutId <= 0) return
        mContentView = LayoutInflater.from(this).inflate(layoutId, null)
        setContentView(mContentView)

        initTitleBar()

        initImmersion()
    }

    private fun initTitleBar() {
        // 初始化标题栏的监听
        if (getTitleId() > 0) {
            // 勤快模式
            val view = findViewById(getTitleId()) as View
            if (view is TitleBar) {
                mTitleBar = view
            }
        } else if (getTitleId() == 0) {
            // 懒人模式
            mTitleBar = findTitleBar(getContentView())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.instance.pushActivity(this)
    }

    /*
        显示加载框，默认实现
     */
    override fun showLoading() {
        mLoadingDialog?.showLoading()
    }

    /*
        隐藏加载框，默认实现
     */
    override fun dismissLoading() {
        mLoadingDialog?.hideLoading()
    }

    /**
     * 递归获取 ViewGroup 中的 TitleBar 对象
     */
    internal fun findTitleBar(group: ViewGroup): TitleBar? {
        for (i in 0 until group.childCount) {
            val view = group.getChildAt(i)
            if (view is TitleBar) {
                return view
            } else if (view is ViewGroup) {
                val titleBar = findTitleBar(view)
                if (titleBar != null) {
                    return titleBar
                }
            }
        }
        return null
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
     * 是否使用沉浸式状态栏
     * open
     */
    open fun isStatusBarEnabled(): Boolean {
        return true
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    open fun getStatusBarConfig(): ImmersionBar {
        return mImmersionBar
    }

    /**
     * 获取状态栏字体颜色
     */
    fun statusBarDarkFont(): Boolean {
        // 返回真表示黑色字体
        return true
    }

    /**
     * 初始化沉浸式状态栏
     */
    protected fun statusBarConfig(): ImmersionBar {
        // 在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this)
            // 默认状态栏字体颜色为黑色
            .statusBarDarkFont(statusBarDarkFont())
        return mImmersionBar
    }

    /**
     * 和 setContentView 对应的方法
     */
    fun getContentView(): ViewGroup {
        return findViewById(Window.ID_ANDROID_CONTENT)
    }

}
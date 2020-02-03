package com.hw.baselibrary.ui.activity

import android.os.Bundle
import com.hw.baselibrary.common.BaseApp
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.injection.component.ActivityComponent
import com.hw.baselibrary.injection.component.DaggerActivityComponent
import com.hw.baselibrary.injection.module.ActivityModule
import com.hw.baselibrary.injection.module.LifecycleProviderModule
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2019/11/11 16:08
 */
abstract open class BaseMvpActivity<T : BasePresenter<*>> : BaseActivity() {
    //Presenter泛型，Dagger注入
    @Inject
    lateinit var mPresenter: T

    lateinit var mActivityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivityInjection()

        initComponent()
    }

    protected abstract fun initComponent()

    private fun initActivityInjection() {
        mActivityComponent = DaggerActivityComponent.builder()
            .appComponent((application as BaseApp).appComponent)
            .activityModule(ActivityModule(this))
            .lifecycleProviderModule(LifecycleProviderModule(this))
            .build()
    }

}
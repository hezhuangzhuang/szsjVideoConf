package com.hw.baselibrary.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hw.baselibrary.common.BaseApp
import com.hw.baselibrary.common.BasePresenter
import com.hw.baselibrary.common.IBaseView
import com.hw.baselibrary.injection.component.ActivityComponent
import com.hw.baselibrary.injection.component.DaggerActivityComponent
import com.hw.baselibrary.injection.module.ActivityModule
import com.hw.baselibrary.injection.module.LifecycleProviderModule
import javax.inject.Inject

/**
 *author：pc-20171125
 *data:2019/12/6 10:04
 */
abstract open class BaseMvpFragment<T : BasePresenter<*>> : BaseLazyFragment(), IBaseView {
    @Inject
    lateinit var mPresenter: T

    lateinit var mActivityComponent: ActivityComponent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        initActivityInjection()

        injectComponent()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * dagger注册
     */
    abstract fun injectComponent()

    private fun initActivityInjection() {
        mActivityComponent = DaggerActivityComponent.builder()
            .appComponent((activity?.application as BaseApp).appComponent)
            .activityModule(ActivityModule(activity!!))
            .lifecycleProviderModule(LifecycleProviderModule(this))
            .build()
    }

}
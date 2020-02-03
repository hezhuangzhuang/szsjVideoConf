package com.hw.kotlinmvpandroidxframe.ui.fragment


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.hw.baselibrary.ui.fragment.BaseMvpFragment
import com.hw.baselibrary.utils.ToastHelper
import com.hw.kotlinmvpandroidxframe.R
import com.hw.kotlinmvpandroidxframe.injection.component.DaggerFlowComponent
import com.hw.kotlinmvpandroidxframe.injection.module.FlowModule
import com.hw.kotlinmvpandroidxframe.mvp.contract.FlowContract
import com.hw.kotlinmvpandroidxframe.mvp.presenter.FlowPresenter
import com.hw.kotlinmvpandroidxframe.net.bean.NewBean
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class FlowFragment : BaseMvpFragment<FlowPresenter>(), FlowContract.View {
    val TAG = "FlowFragment"

    override fun injectComponent() {
        DaggerFlowComponent.builder()
            .activityComponent(mActivityComponent)
            .flowModule(FlowModule())
            .build()
            .inject(this)

        mPresenter.mRootView = this
    }

    override fun doLazyBusiness() {
        Log.i(TAG, "doLazyBusiness")
        mPresenter.queryFirstStudys(1)
    }

    override fun initData(bundle: Bundle?) {
        Log.i(TAG, "initData")
        forceLoad = true
    }

    override fun bindLayout(): Int = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        tvAddress.setOnClickListener {
            mPresenter.queryFirstStudys(1)
        }
    }

    override fun showFirstNewList(studyList: ArrayList<NewBean>) {
        ToastHelper.showShort("showEmptyView" + studyList)
    }

    override fun showMoreNewList(studyList: ArrayList<NewBean>) {
        ToastHelper.showShort("showEmptyView" + studyList)
    }

    override fun showError(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }

    override fun showEmptyView() {
        ToastHelper.showShort("showEmptyView")
    }

    override fun onError(text: String) {

    }

    override fun isStatusBarEnabled(): Boolean = !super.isStatusBarEnabled()


}

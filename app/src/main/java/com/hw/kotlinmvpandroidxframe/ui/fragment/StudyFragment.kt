package com.hw.kotlinmvpandroidxframe.ui.fragment


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.hw.baselibrary.ui.fragment.BaseLazyFragment
import com.hw.kotlinmvpandroidxframe.R

/**
 * A simple [Fragment] subclass.
 */
class StudyFragment : BaseLazyFragment() {
    override fun onError(text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doLazyBusiness() {
        mTitleBar!!.title = "StudyFragment"
    }

    override fun initData(bundle: Bundle?) {
    }

    override fun bindLayout(): Int = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
    }

}

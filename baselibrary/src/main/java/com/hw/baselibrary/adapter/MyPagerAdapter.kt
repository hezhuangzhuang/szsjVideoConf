package com.hw.baselibrary.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 *author：pc-20171125
 *data:2020/1/8 15:42
 */
class MyPagerAdapter : FragmentPagerAdapter {
    private lateinit var mFragments: List<Fragment>
    private lateinit var mTitles: Array<String>

    constructor(fm: FragmentManager, titles: Array<String>, fragments: List<Fragment>) : super(fm) {
        this.mTitles = titles
        this.mFragments = fragments
    }

    override fun getItem(position: Int): Fragment {
        return mFragments?.get(position)
    }

    override fun getCount(): Int {
        return mFragments?.size ?: 0
    }

    override  fun getPageTitle(position: Int): CharSequence {
        return mTitles[position]
    }
}
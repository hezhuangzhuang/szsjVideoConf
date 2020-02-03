package com.hw.kotlinmvpandroidxframe.injection.component

import com.hw.baselibrary.injection.PerComponentScope
import com.hw.baselibrary.injection.component.ActivityComponent
import com.hw.kotlinmvpandroidxframe.injection.module.FlowModule
import com.hw.kotlinmvpandroidxframe.ui.fragment.FlowFragment
import dagger.Component

/**
 *author：pc-20171125
 *data:2019/12/6 10:30
 */
@PerComponentScope
@Component(
    dependencies = arrayOf(ActivityComponent::class),
    modules = arrayOf(FlowModule::class)
)
interface FlowComponent {
    fun inject(fragment: FlowFragment)
}
package com.hw.kotlinmvpandroidxframe.injection.component

import com.hw.baselibrary.injection.PerComponentScope
import com.hw.baselibrary.injection.component.ActivityComponent
import com.hw.kotlinmvpandroidxframe.injection.module.MainModule
import com.hw.kotlinmvpandroidxframe.ui.activity.MainActivity
import dagger.Component


@PerComponentScope
@Component(
    modules = arrayOf(MainModule::class),
    dependencies = arrayOf(ActivityComponent::class)
)
interface MainComponent {

    fun inject(mainActivity: MainActivity)

}
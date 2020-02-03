package com.hw.baselibrary.injection.component

import android.app.Activity
import android.content.Context
import com.hw.baselibrary.injection.ActivityScope
import com.hw.baselibrary.injection.module.ActivityModule
import com.hw.baselibrary.injection.module.LifecycleProviderModule
import com.trello.rxlifecycle2.LifecycleProvider
import dagger.Component

/**
 *author：pc-20171125
 *data:2019/12/6 09:20
 * Activity级别Component
 *
 * dependencies：依赖AppComponent获取context
 *
 */
@ActivityScope
@Component(
    modules = arrayOf(
        ActivityModule::class,
        LifecycleProviderModule::class
    ),
    dependencies = arrayOf(AppComponent::class)
)
interface ActivityComponent {
    fun activity(): Activity

    fun context(): Context

    fun lifecyclerProvider(): LifecycleProvider<*>
}
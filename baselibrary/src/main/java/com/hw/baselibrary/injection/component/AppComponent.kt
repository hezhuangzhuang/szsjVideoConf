package com.hw.baselibrary.injection.component

import android.content.Context
import com.hw.baselibrary.injection.module.AppModule
import dagger.Component
import javax.inject.Singleton

/**
 *author：pc-20171125
 *data:2019/12/6 09:20
 * Application级别Component
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun context(): Context
}
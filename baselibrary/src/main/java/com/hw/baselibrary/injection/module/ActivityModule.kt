package com.hw.baselibrary.injection.module

import android.app.Activity
import com.hw.baselibrary.injection.ActivityScope
import dagger.Module
import dagger.Provides

/**
 *author：pc-20171125
 *data:2019/12/6 09:23
 * activity级别module
 */
@Module()
class ActivityModule(private val activity: Activity) {

    @ActivityScope
    @Provides
    fun provideActivity(): Activity {
        return this.activity
    }

}
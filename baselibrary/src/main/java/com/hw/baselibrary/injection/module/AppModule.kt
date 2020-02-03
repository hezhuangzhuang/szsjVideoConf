package com.hw.baselibrary.injection.module

import android.content.Context
import com.hw.baselibrary.common.BaseApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 *author：pc-20171125
 *data:2019/12/6 09:21
 * application级别module
 */
@Module
class AppModule(private val context: BaseApp) {

    @Singleton
    @Provides
    fun providesContext(): Context {
        return this.context
    }
}

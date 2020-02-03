package com.hw.baselibrary.injection.module

import com.trello.rxlifecycle2.LifecycleProvider
import dagger.Module
import dagger.Provides

/**
 *author：pc-20171125
 *data:2019/12/6 09:44
 * Rx生命周期管理能用Module
 */
@Module
class LifecycleProviderModule(private val lifecycleProvider: LifecycleProvider<*>) {

    @Provides
    fun provideLifecycleProvider(): LifecycleProvider<*> {
        return this.lifecycleProvider
    }
}
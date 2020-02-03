package com.hw.kotlinmvpandroidxframe.injection.module

import com.hw.kotlinmvpandroidxframe.mvp.model.FlowService
import dagger.Module
import dagger.Provides

/**
 *author：pc-20171125
 *data:2019/12/6 10:28
 */
@Module
class FlowModule {

    @Provides
    fun provideFlowService(): FlowService {
        return FlowService()
    }
}
package com.hw.kotlinmvpandroidxframe.injection.module

import `object`.Conf
import com.hw.kotlinmvpandroidxframe.mvp.model.ConfService
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class MainModule @Inject constructor() {

    @Provides
    fun providesConfService(): ConfService {
        return ConfService()
    }

}
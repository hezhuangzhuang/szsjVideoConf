package com.hw.baselibrary.common

import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.hw.baselibrary.injection.component.AppComponent
import com.hw.baselibrary.injection.component.DaggerAppComponent
import com.hw.baselibrary.injection.module.AppModule

/**
 *author：pc-20171125
 *data:2019/11/7 15:47
 */
open class BaseApp : Application() {

    lateinit var appComponent: AppComponent

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()

        initAppInjection()

        context = this

        //ARouter初始化
        ARouter.openLog()
        // 打印日志
        ARouter.openDebug()
        ARouter.init(this)
    }

    /**
     * application Component初始化
     */
    private fun initAppInjection() {
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }


}
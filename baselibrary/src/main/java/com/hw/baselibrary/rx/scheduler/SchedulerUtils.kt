package com.hw.baselibrary.rx.scheduler

import com.hazz.kotlinmvp.rx.scheduler.IoMainScheduler

/**
 * Created by xuhao on 2017/11/17.
 * desc:
 */

object SchedulerUtils {

    fun <T> ioToMain(): IoMainScheduler<T> {
        return IoMainScheduler()
    }
}

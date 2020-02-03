package com.hw.baselibrary.image

import android.content.Context
import android.graphics.drawable.Drawable

/**
 *author：pc-20171125
 *data:2019/11/9 12:00
 */
interface ImageFactory<out T:ImageStrategy> {
    /**
     * 创建一个图片加载策略
     */
     fun createImageStrategy(): T

    /**
     * 创建加载占位图
     */
     fun createPlaceholder(context: Context): Drawable

    /**
     * 创建加载错误占位图
     */
     fun createError(context: Context): Drawable

    /**
     * 清除内存缓存
     */
     fun clearMemoryCache(context: Context)

    /**
     * 清除磁盘缓存
     */
     fun clearDiskCache(context: Context)
}
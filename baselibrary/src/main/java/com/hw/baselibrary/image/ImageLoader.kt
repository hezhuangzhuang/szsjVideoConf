package com.hw.baselibrary.image

import android.app.Application
import android.app.Fragment
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import java.io.File

/**
 *author：pc-20171125
 *data:2019/11/9 11:50
 */
class ImageLoader {

    constructor(context: Any) {
        this.context = context
    }

    companion object {
        /** 图片生产工厂  */
        private var sImageFactory: ImageFactory<*>? = null

        /** 图片加载策略  */
        private var sImageStrategy: ImageStrategy? = null

        /** 加载中占位图  */
        private var sPlaceholder: Drawable? = null

        /** 加载出错占位图  */
        private var sError: Drawable? = null

        fun init(application: Application) {
            // 使用 Glide 进行初始化图片加载器
            init(application, GlideFactory())
        }

        /**
         * 使用指定的图片加载器进行初始化
         *
         * @param application               上下文对象
         * @param factory                   图片加载器生成对象
         */
        fun init(application: Application, factory: ImageFactory<*>) {
            sImageFactory = factory
            sImageStrategy = factory.createImageStrategy()
            sPlaceholder = factory.createPlaceholder(application)
            sError = factory.createError(application)
        }

        fun with(context: Context): ImageLoader {
            return ImageLoader(context)
        }

        fun with(fragment: Fragment): ImageLoader {
            return ImageLoader(fragment)
        }

        fun with(fragment: androidx.fragment.app.Fragment): ImageLoader {
            return ImageLoader(fragment)
        }
    }

    /**
     * 清除图片缓存
     */
    fun clear(context: Context) {
        clearMemoryCache(context)
        clearDiskCache(context)
    }

    /**
     * 清除内存缓存
     */
    fun clearMemoryCache(context: Context) {
        sImageFactory!!.clearMemoryCache(context)
    }

    /**
     * 清除磁盘缓存
     */
    fun clearDiskCache(context: Context) {
        sImageFactory!!.clearDiskCache(context)
    }


    lateinit var context: Any
    var circle: Int = 0
    lateinit var url: String
    @DrawableRes
    var resourceId: Int = 0
    var isGif: Boolean = false

    var placeholder = sPlaceholder
    var error = sError

    var width: Int = 0
    var height: Int = 0

    lateinit var view: ImageView

    fun gif(): ImageLoader {
        this.isGif = true
        return this
    }

    fun circle(): ImageLoader {
        return circle(Integer.MAX_VALUE)
    }

    fun circle(circle: Int): ImageLoader {
        this.circle = circle
        return this
    }

    fun load(url: String): ImageLoader {
        this.url = url
        return this
    }

    fun load(file: File): ImageLoader {
        this.url = Uri.fromFile(file).toString()
        return this
    }

    fun load(@DrawableRes id: Int): ImageLoader {
        this.resourceId = id
        return this
    }

    fun placeholder(placeholder: Drawable): ImageLoader {
        this.placeholder = placeholder
        return this
    }

    fun error(error: Drawable): ImageLoader {
        this.error = error
        return this
    }

    fun override(width: Int, height: Int): ImageLoader {
        this.width = width
        this.height = height
        return this
    }

    fun into(view: ImageView) {
        this.view = view
        sImageStrategy!!.load(this)
    }
}
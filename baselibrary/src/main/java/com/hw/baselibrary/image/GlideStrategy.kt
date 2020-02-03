package com.hw.baselibrary.image

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 *author：pc-20171125
 *data:2019/11/9 12:02
 */
class GlideStrategy :ImageStrategy {

    @SuppressLint("CheckResult")
    override fun load(loader: ImageLoader) {
        val manager = getRequestManager(loader.context)

        if (loader.isGif) {
            manager.asGif()
        }

        val builder: RequestBuilder<Drawable>
        if (loader.url != null && "" != loader.url) {
            builder = manager.load(loader.url.trim { it <= ' ' })
        } else if (loader.resourceId != 0) {
            builder = manager.load(loader.resourceId)
        } else {
            builder = manager.load(loader.error)
        }

        if (loader.placeholder != null) {
            val options = RequestOptions.errorOf(loader.error).placeholder(loader.placeholder)
            if (loader.circle != 0) {
                if (loader.circle == Integer.MAX_VALUE) {
                    // 裁剪成圆形
                    options.circleCrop()
                } else {
                    // 圆角裁剪
                    options.transform(RoundedCorners(loader.circle))
                }
            }

            builder.apply(options)
        }

        if (loader.width != 0 && loader.height != 0) {
            builder.override(loader.width, loader.height)
        }

        builder.into(loader.view)
    }

    /**
     * 获取一个 Glide 的请求对象
     */
    private fun getRequestManager(`object`: Any?): RequestManager {
        requireNotNull(`object`) { "You cannot start a load on a null Context" }
        if (`object` is Context) {
            return if (`object` is FragmentActivity) {
                Glide.with((`object` as FragmentActivity?)!!)
            } else if (`object` is Activity) {
                Glide.with((`object` as Activity?)!!)
            } else {
                Glide.with((`object` as Context?)!!)
            }
        } else if (`object` is Fragment) {
            return Glide.with((`object` as Fragment?)!!)
        } else if (`object` is androidx.fragment.app.Fragment) {
            return Glide.with((`object` as androidx.fragment.app.Fragment?)!!)
        }
        // 如果不是上面这几种类型就直接抛出异常
        throw IllegalArgumentException("This object is illegal")
    }
}
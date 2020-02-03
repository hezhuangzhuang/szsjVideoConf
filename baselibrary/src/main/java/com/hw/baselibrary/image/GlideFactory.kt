package com.hw.baselibrary.image

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.hw.baselibrary.R

/**
 *author：pc-20171125
 *data:2019/11/9 12:01
 */
class GlideFactory:ImageFactory<GlideStrategy> {

    override fun createImageStrategy(): GlideStrategy {
        return GlideStrategy()
    }

    override fun createPlaceholder(context: Context): Drawable {
        return ContextCompat.getDrawable(context, R.drawable.image_loading)!!
    }

    override fun createError(context: Context): Drawable {
        return ContextCompat.getDrawable(context, R.drawable.image_load_err)!!
    }

    override fun clearMemoryCache(context: Context) {
        // 清除内存缓存（必须在主线程）
        Glide.get(context).clearMemory()
    }

    override fun clearDiskCache(context: Context) {
        Thread(Runnable {
            // 清除本地缓存（必须在子线程）
            Glide.get(context).clearDiskCache()
        }).start()
    }
}
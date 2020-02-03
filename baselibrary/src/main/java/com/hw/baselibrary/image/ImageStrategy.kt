package com.hw.baselibrary.image

/**
 *author：pc-20171125
 *data:2019/11/9 11:50
 */
interface ImageStrategy {
        
    /**
     * 加载图片
     */
    fun load(loader: ImageLoader)
}
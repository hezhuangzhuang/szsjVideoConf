package com.hw.kotlinmvpandroidxframe.ui.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hw.baselibrary.image.ImageLoader
import com.hw.baselibrary.utils.DateUtils
import com.hw.kotlinmvpandroidxframe.R
import com.hw.kotlinmvpandroidxframe.ui.adapter.item.NewsListItem



/**
 *author：pc-20171125
 *data:2019/11/11 17:11
 */
class NewsListAdapter : BaseSectionMultiItemQuickAdapter<NewsListItem, BaseViewHolder> {

    constructor(sectionHeadResId: Int, data: MutableList<NewsListItem>?) : super(
        sectionHeadResId,
        data
    ) {
        addItemType(MultipleItem.TEXT, R.layout.item_text)
        addItemType(MultipleItem.TEXT_SMALL_IMG, R.layout.item_text_and_small_img)
        addItemType(MultipleItem.TEXT_SMALL_VIDEO, R.layout.item_text_and_small_img)
        addItemType(MultipleItem.TEXT_BIG_IMG, R.layout.item_text_and_big_img)
        addItemType(MultipleItem.TEXT_MULTIPLE_IMG, R.layout.item_text_and_multiple_img)
    }

    override fun convertHead(helper: BaseViewHolder?, item: NewsListItem?) {
    }

    override fun convert(helper: BaseViewHolder, item: NewsListItem?) {
        var newsBean = item?.newBean

        helper.setText(R.id.tv_title, newsBean?.title)
        helper.setText(R.id.tv_website, newsBean?.announcer)
        helper.setText(
            R.id.tv_time,
            DateUtils.longToString(newsBean!!.createDate, DateUtils.FORMAT_DATE)
        )

        var imageView: ImageView? = null;

        when (item?.itemType) {
            //一张小图
            MultipleItem.TEXT_SMALL_VIDEO, MultipleItem.TEXT_SMALL_IMG -> {
                imageView = helper.getView(R.id.iv_img)
                ImageLoader.with(mContext).circle(3).load(newsBean?.pic1).into(imageView!!)
            }

            //一张大图
            MultipleItem.TEXT_BIG_IMG -> {
                imageView = helper.getView(R.id.iv_img)
                ImageLoader.with(mContext).circle(3).load(newsBean?.pic1).into(imageView!!)
            }

            //多图
            MultipleItem.TEXT_MULTIPLE_IMG -> {

            }
        }
    }
}
package com.zxwl.vclibrary.adapter.item

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.entity.SectionMultiEntity

/**
 * 组织或用户的bean
 */
class OrganOrUserBeanItem(var bean: MultiItemEntity) : SectionMultiEntity<MultiItemEntity>(bean) {
    override fun getItemType(): Int {
        return bean.itemType
    }
}
package com.zxwl.vclibrary.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.zxwl.vclibrary.adapter.SearchPeopleAdapter

/**
 * 组织结构的bean
 */
data class OrganBean(
    val activeDate: String,
    val category: Int,
    val createName: String,
    val deptCode: String,
    val description: String,
    val disabledDate: String,
    val id: String,
    val isCancel: Int,
    val isDisabled: Int,
    val isLeaf: Int,
    val lastUpdateName: String,
    val listOrder: Int,
    val name: String,
    val oldCodeDisabledDate: String,
    val oldDeptCode: String,
    val parentCode: String,
    val parentId: String,
    val path: String,
    val pathLevel: Int,
    val pathtree: String,
    val remark: String,
    val zgrsId: String
) : MultiItemEntity {

    override fun getItemType(): Int {
        return SearchPeopleAdapter.TYPE_ORGAN_BEAN
    }

}
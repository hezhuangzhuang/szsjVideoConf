package com.zxwl.vclibrary.bean

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.zxwl.vclibrary.adapter.SearchPeopleAdapter

/**
 * 接口获取到的bean
 */
data class PoliceBean(
    val age: Int,
    val birthday: String,
    val childDeptIds: String,
    val createName: String,
    val currentJob: String,
    val departmentId: String,
    val deptCode: String,
    val deptName: String,
    val gender: Int,
    val govFeature: String,
    val id: String,
    val idcard: String,
    val isDeleted: Int,
    val job: String,
    val lastUpdateName: String,
    val leadercode: String,
    val listOrder: Int,
    val marriage: String,
    val mobilePhone: String,
    val nation: String,
    val nativePlace: String,
    val pathLevel: Int,
    val personStatus: String,
    val personnelStatus: String,
    val policeRank: String,
    val position: String,
    val pwd: String,
    val rank: String,
    val regName: String,
    val remark: String,
    val scIP: String,
    val secondLevelUnits: String,
    val status: Int,
    val telePhone: String,
    val threeLevelUnits: String,
    val uri: String,
    val userLoginId: String,
    val userName: String,
    val userPhotoPath: String,
    val userType: Int,
    val workDepartmentId: String,
    val workDeptCode: String,
    val workDeptName: String,
    val workPosition: String,

    //是否选中
    var check: Boolean
) : MultiItemEntity {
    override fun getItemType(): Int {
        return SearchPeopleAdapter.TYPE_USER_BEAN
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PoliceBean

        if (uri != other.uri) return false
        if (userName != other.userName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri.hashCode()
        result = 31 * result + userName.hashCode()
        return result
    }
}
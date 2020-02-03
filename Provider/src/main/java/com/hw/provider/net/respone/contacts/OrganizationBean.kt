package com.hw.provider.net.respone.contacts

/**
 *author：pc-20171125
 *data:2020/1/8 18:06
 * 组织机构返回的数据
 * type1 是组织 2是人员
 */
data class OrganizationBean(
    var count: Int,
    var depId: Int,
    var depName: String,
    var name: String,
    var sip: String,
    var type:Int,
    var level:Int
)
package com.hw.provider.router.provider.constacts

import com.alibaba.android.arouter.facade.template.IProvider
import com.hw.baselibrary.common.BaseData
import com.hw.provider.net.respone.contacts.PeopleBean
import io.reactivex.Observable

/**
 *author：pc-20171125
 *data:2020/1/9 13:53
 * 获取通讯录的服务接口
 *
 * TODO：跨模块调用接口的示例
 * 例如，此时ConfModule需要调用ConstactsModule中的方法，实现步骤如下
 * 1.在BaseLib创建一个接口，例如：IContactsModuleService，
 *   该接口继承IProvider，该接口中提供供其它模块调用的方法
 *
 * 2.在ConstactsModule中创建一个类ContactsModuleServiceImp，
 * 实现IContactsModuleService接口，该类是实际调用接口获取数据的地方，
 * 并给该类添加注解@Route
 *
 * 3.在BaseLib中创建一个类ContactsModuleRouteService，
 * 该类中有包含IContactsModuleService接口所有的方法，
 * 用ARouter.getInstance().navigation(IContactsModuleService::class.java)方法
 * 获取IContactsModuleService，
 * 然后用得到的IContactsModuleService调用方法，IContactsModuleService会去调用
 * ConstactsModule中ContactsModuleServiceImp类里的方法来得到数据，
 */
public interface IContactsModuleService : IProvider {

    /**
     * 获取所有联系人
     */
    fun getAllPeople(): Observable<BaseData<PeopleBean>>

    /**
     * 添加人员到群组
     */
    fun addPeopleToGroupChat(
        groupId: String,
        ids: String
    ): Observable<BaseData<PeopleBean>>

    /**
     * 添加人员到群组
     */
    fun deletePeopleToGroupChat(
        groupId: String,
        ids: String
    ): Observable<BaseData<PeopleBean>>

}
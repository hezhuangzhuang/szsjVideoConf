package com.zxwl.vclibrary.activity

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.gyf.immersionbar.ImmersionBar
import com.huawei.opensdk.commonservice.common.LocContext
import com.huawei.opensdk.commonservice.localbroadcast.CustomBroadcastConstants
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcast
import com.huawei.opensdk.commonservice.localbroadcast.LocBroadcastReceiver
import com.huawei.opensdk.demoservice.Member
import com.hw.baselibrary.net.NetWorkContants
import com.hw.baselibrary.net.RetrofitManager
import com.hw.baselibrary.net.Urls
import com.hw.baselibrary.net.api.ControlApi
import com.hw.baselibrary.rx.scheduler.CustomCompose
import com.zxwl.vclibrary.R
import com.zxwl.vclibrary.adapter.SearchPeopleAdapter
import com.zxwl.vclibrary.adapter.SelectedPeopleAdapter
import com.zxwl.vclibrary.adapter.item.OrganOrUserBeanItem
import com.zxwl.vclibrary.bean.BaseData
import com.zxwl.vclibrary.bean.OrganBean
import com.zxwl.vclibrary.bean.PoliceBean
import com.zxwl.vclibrary.util.KeyboardUtils
import com.zxwl.vclibrary.util.LogUtils
import com.zxwl.vclibrary.util.NetWorkUtils
import com.zxwl.vclibrary.util.ToastHelper
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_select_people.*
import kotlinx.android.synthetic.main.cl_bottom_control.*
import kotlinx.android.synthetic.main.include_search_title.*
import kotlinx.android.synthetic.main.include_title.*
import me.jessyan.autosize.internal.CancelAdapt
import java.io.Serializable

/**
 * 选择人员
 */
class SelectPeopleActivity : BaseVcActivity(), View.OnClickListener , CancelAdapt {

    //布局的样式
    private var layoutMode = LAYOUT_ALL_PEOPLE

    //全部的人员
    private val allPeopleAdapter: SearchPeopleAdapter by lazy {
        SearchPeopleAdapter(mutableListOf())
    }

    //已选择
    private val selectedPeopleAdapter: SelectedPeopleAdapter by lazy {
        SelectedPeopleAdapter(mutableListOf())
    }

    //搜索的适配器
    private val searchPeopleAdapter: SearchPeopleAdapter by lazy {
        SearchPeopleAdapter(mutableListOf())
    }

    //已选的人员列表
    private var selectPeoples = mutableListOf<PoliceBean>()

    //已存在不能选择的人员列表
    private var noSelectPeople = listOf<Member>()

    override fun initData() {
        noSelectPeople = intent.getSerializableExtra(NO_SELECT_LIST) as List<Member>

        ImmersionBar.with(this)
            // 默认状态栏字体颜色为黑色
            // 解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
            .keyboardEnable(true)
            .init()

        initAllAdapter()
        initSelectedAdapter()
        initSearchAdapter()

        showAllLayout()

        getOrganAndUser(Urls.BASE_DEPT_NAME, Urls.BASE_DEPT_ID)
    }

    /**
     * 获取组织下的成员和子级
     */
    private fun getOrganAndUser(deptName: String, deptId: String) {
        Observable.zip(
            getOrgans(deptId),
            getUsers(deptId),
            object :
                BiFunction<BaseData<OrganBean>, BaseData<PoliceBean>, List<OrganOrUserBeanItem>> {
                override fun apply(
                    organBaseData: BaseData<OrganBean>,
                    policeBaseData: BaseData<PoliceBean>
                ): List<OrganOrUserBeanItem> {
                    var organOrUserBeanItemList = mutableListOf<OrganOrUserBeanItem>()

                    //成员
                    if (NetWorkContants.SUCCESS.equals(policeBaseData.msg) && null != policeBaseData.data && policeBaseData.data.size > 0) {
                        val policeItems = policeBaseData.data.map {
                            OrganOrUserBeanItem(it)
                        }
                        organOrUserBeanItemList.addAll(policeItems)
                    }

                    //组织
                    if (NetWorkContants.SUCCESS.equals(organBaseData.msg) && null != organBaseData.data && organBaseData.data.size > 0) {
                        val organItems = organBaseData.data.map {
                            OrganOrUserBeanItem(it)
                        }
                        organOrUserBeanItemList.addAll(organItems)
                    }
                    return organOrUserBeanItemList
                }
            }
        )
            .subscribe({ organOrUserBeanItemList ->
                if (organOrUserBeanItemList.size > 0) {
                    //判断是否包含在已选人员列表中
                    checkSelects(organOrUserBeanItemList)

                    allPeopleAdapter.replaceData(organOrUserBeanItemList)

                    //设置全选样式
                    setAllSelectStatus()

                    //设置选中的人员数量
                    setSelectNumber()

                    //添加子结构
                    addChildOrgan(deptId, deptName)
                }
            }, {
                LogUtils.e("请求异常:${it.message}")
                ToastHelper.showShort("请求异常:${it.message}")
            }
            )
    }

    /**
     * 判断是否在已选人员列表中
     */
    private fun checkSelects(organOrUserBeanItemList: List<OrganOrUserBeanItem>) {
        organOrUserBeanItemList.forEach {
            if (it.bean is PoliceBean) {
                val policeBean = it.bean as PoliceBean
                val contains = selectPeoples.contains(policeBean)
                policeBean.check = contains
            }
        }
    }

    /**
     * 添加子组织的view
     */
    private fun addChildOrgan(deptId: String, deptName: String) {
        //判断是否是根目录
        if (deptId == Urls.BASE_DEPT_ID) {
            //删除所有成员
            llOrgan.removeAllViews()
            //添加成员
            llOrgan.addView(createText(deptId, deptName))
            return
        }

        llOrgan.addView(createText(deptId, deptName))

        for (i in 0 until llOrgan.childCount) {
            if (i < llOrgan.childCount - 1) {
                val childAt = llOrgan.getChildAt(i) as TextView
                childAt.setTextColor(
                    ContextCompat.getColor(
                        LocContext.getContext(),
                        R.color.color_007AFF
                    )
                )
                childAt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    0,
                    0,
                    R.mipmap.ic_arrow,
                    0
                )
            } else {
                val childAt = llOrgan.getChildAt(i) as TextView
                childAt.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

        Handler().postDelayed({ scrollView.arrowScroll(View.FOCUS_RIGHT) }, 100)
    }

    /**
     * 创建text
     *
     * @param deptId
     * @param deptName
     * @return
     */
    private fun createText(deptId: String, deptName: String): View {
        val tvOrgan = View.inflate(this, R.layout.item_add_organ, null) as TextView

        tvOrgan.text = deptName

        tvOrgan.setOnClickListener(View.OnClickListener {
            //判断当前点击的view在父控件中的下标
            val indexOfChild = llOrgan.indexOfChild(tvOrgan)
            val childCount = llOrgan.childCount

            //如果下标为最后一个则不进行操作
            if (indexOfChild == childCount - 1) {
                return@OnClickListener
            }

            //如果点击的是根目录则删除下面的所有子view
            if (Urls.BASE_DEPT_ID.equals(deptId)) {
                while (llOrgan.childCount > 1) {
                    llOrgan.removeViewAt(llOrgan.childCount - 1)
                }
            } else {
                while (llOrgan.childCount > indexOfChild) {
                    llOrgan.removeViewAt(llOrgan.childCount - 1)
                }
            }

            for (j in 0 until llOrgan.childCount) {
                val childAt = llOrgan.getChildAt(j) as TextView
                childAt.setTextColor(
                    ContextCompat.getColor(
                        LocContext.getContext(),
                        R.color.color_007AFF
                    )
                )
            }

            tvOrgan.setTextColor(ContextCompat.getColor(LocContext.getContext(), R.color.color_999))

            //获取数据
            getOrganAndUser(deptName, deptId)
        })
        return tvOrgan
    }


    /**
     * 获取组织
     */
    private fun getOrgans(deptId: String): Observable<BaseData<OrganBean>> {
        return RetrofitManager.create(ControlApi::class.java, Urls.BASE_URL)
            .getOrgans(deptId)
            .compose(CustomCompose())
    }

    /**
     * 获取成员
     */
    private fun getUsers(deptId: String): Observable<BaseData<PoliceBean>> {
        return RetrofitManager.create(ControlApi::class.java, Urls.BASE_URL)
            .getUsers(deptId)
            .compose(CustomCompose())
    }

    /**
     * 搜索警员
     */
    private fun searchUsers(name: String) {
        RetrofitManager.create(ControlApi::class.java, Urls.BASE_URL)
            .searchUsers(name)
            .compose(CustomCompose())
            .subscribe({ policeBaseData ->
                if (NetWorkContants.SUCCESS.equals(policeBaseData.msg)) {
                    if (null != policeBaseData.data && policeBaseData.data.size > 0) {
                        var organOrUserBeanItemList = mutableListOf<OrganOrUserBeanItem>()

                        //成员
                        if (NetWorkContants.SUCCESS.equals(policeBaseData.msg) && null != policeBaseData.data && policeBaseData.data.size > 0) {
                            val policeItems = policeBaseData.data.map {
                                OrganOrUserBeanItem(it)
                            }
                            organOrUserBeanItemList.addAll(policeItems)
                        }

                        //判断是否包含在已选人员列表中
                        checkSelects(organOrUserBeanItemList)

                        searchPeopleAdapter.replaceData(organOrUserBeanItemList)
                    } else {
                        searchPeopleAdapter.replaceData(mutableListOf())
                    }
                } else {
                    ToastHelper.showShort("${policeBaseData.msg}")
                }
            }, {
                ToastHelper.showShort("搜索异常:${it.message}")
                LogUtils.e("搜索异常:${it.message}")
            })
    }

    /**
     * 搜索的适配器
     */
    private fun initSearchAdapter() {
        searchPeopleAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item = searchPeopleAdapter.getItem(position)

            val policeBean = item?.bean as PoliceBean

            policeBean.check = !policeBean.check
            //是否选中
            if (policeBean.check) {
                selectPeoples.add(policeBean)
            } else {
                selectPeoples.remove(policeBean)
            }

            searchPeopleAdapter.notifyDataSetChanged()
        }
        rlSearchPeople.adapter = searchPeopleAdapter
        rlSearchPeople.layoutManager = LinearLayoutManager(this)
    }

    /**
     * 已选人员的适配器
     */
    private fun initSelectedAdapter() {
        selectedPeopleAdapter.setOnItemChildClickListener { adapter, view, position ->
            val item = selectedPeopleAdapter.getItem(position)
            selectPeoples.remove(item)
            selectedPeopleAdapter.remove(position)
        }
        rlSelectedPeople.adapter = selectedPeopleAdapter
        rlSelectedPeople.layoutManager = LinearLayoutManager(this)
    }

    /**
     * 全部人员的适配器
     */
    private fun initAllAdapter() {
        allPeopleAdapter.setOnItemChildClickListener(object :
            BaseQuickAdapter.OnItemChildClickListener {
            override fun onItemChildClick(
                adapter: BaseQuickAdapter<*, *>?,
                view: View?,
                position: Int
            ) {
                val item = allPeopleAdapter.getItem(position)
                when (item?.itemType) {
                    SearchPeopleAdapter.TYPE_ORGAN_BEAN -> {
                        val organBean = item.bean as OrganBean
                        //获取子组织的人员和列表
                        getOrganAndUser(organBean.name, organBean.id)
                    }

                    SearchPeopleAdapter.TYPE_USER_BEAN -> {
                        val policeBean = item.bean as PoliceBean

                        policeBean.check = !policeBean.check
                        //是否选中
                        if (policeBean.check) {
                            selectPeoples.add(policeBean)
                        } else {
                            selectPeoples.remove(policeBean)
                        }

                        setSelectNumber()

                        setAllSelectStatus()

                        allPeopleAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
        rlAllPeople.adapter = allPeopleAdapter
        rlAllPeople.layoutManager = LinearLayoutManager(this)
    }

    //是否全选
    private var isAllCheck = false

    /**
     * 设置全选的样式
     */
    private fun setAllSelectStatus() {
        //所有的user
        val allPolices = getAllPolices()

        //选中的user
        val checkPolices = filterPolices(allPolices)

        //选中的user数大于0并且数量等于所有的user则代表全选
        isAllCheck = allPolices.size > 0 && allPolices.size == checkPolices.size

        tvAllSelect.setCompoundDrawablesRelativeWithIntrinsicBounds(
            if (isAllCheck) R.mipmap.ic_check_true else R.mipmap.ic_check_false,
            0,
            0,
            0
        )
    }

    /**
     * 返回选中的user
     */
    private fun filterPolices(allPolices: List<OrganOrUserBeanItem>): List<OrganOrUserBeanItem> {
        //选中的user
        val checkPolices = allPolices.filter {
            val policeBean = it.bean as PoliceBean
            policeBean.check
        }

        return checkPolices
    }

    /**
     * 获取所有的user
     */
    private fun getAllPolices(): List<OrganOrUserBeanItem> {
        val data = allPeopleAdapter.data

        val allPolices = data.filter {
            it.itemType == SearchPeopleAdapter.TYPE_USER_BEAN
        }
        return allPolices
    }

    /**
     * 设置选中的数量
     */
    private fun setSelectNumber() {
        tvSelectNumber.setText("已选择:${selectPeoples.size}人")
    }

    override fun setListener() {
        etSearchContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(s)) {
                    Handler().postDelayed(Runnable {
                        searchPeopleAdapter.replaceData(mutableListOf())
                    }, 300)
                } else {
                    Handler().postDelayed(Runnable {
                        searchUsers(s.toString())
                    }, 300)
                }
            }
        })

        ivBack.setOnClickListener(this)
        tvRightOper.setOnClickListener(this)
        flSearch.setOnClickListener(this)
        ivDeleteSearchContent.setOnClickListener(this)
        tvSearchCancle.setOnClickListener(this)
        tvSelectNumber.setOnClickListener(this)
        tvAllSelect.setOnClickListener(this)
        btConfirm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> {
                if (layoutMode == LAYOUT_ALL_PEOPLE) {
                    finish()
                    return
                }

                showAllLayout()
            }

            tvSearchCancle -> {
                showAllLayout()
            }

            flSearch -> {
                showSearchLayout()
            }

            ivDeleteSearchContent -> {
                etSearchContent.setText("")
            }

            tvSelectNumber -> {
                if (selectPeoples.size <= 0) {
                    ToastHelper.showShort("请选择参会人员")
                    return
                }
                showSelectedLayout()
            }

            btConfirm -> {
                LocBroadcast.getInstance()
                    .sendBroadcast(CustomBroadcastConstants.ADD_ATTENDEE_TO_CONF, selectPeoples)
                finish()
            }

            tvAllSelect -> {
                val allPolices = getAllPolices()

                isAllCheck = !isAllCheck

                selectPeoples.clear()

                allPolices.forEach {
                    val policeBean = it.bean as PoliceBean
                    policeBean.check = isAllCheck
                    if (isAllCheck) {
                        selectPeoples.add(policeBean)
                    }
                }

                allPeopleAdapter.notifyDataSetChanged()

                setSelectNumber()

                setAllSelectStatus()
            }

        }
    }

    override fun onBackPressed() {
        if (layoutMode == LAYOUT_ALL_PEOPLE) {
            finish()
            return
        }

        showAllLayout()
    }

    /**
     * 显示全部人员
     */
    private fun showAllLayout() {
        layoutMode = LAYOUT_ALL_PEOPLE

        tvTitle.text = "请选择"

        clTitle.isVisible = true
        flSearch.isVisible = true
        clSearchTitle.isVisible = false
        tvRightOper.isVisible = true

        rlAllPeople.isVisible = true
        rlSelectedPeople.isVisible = false
        rlSearchPeople.isVisible = false

        clBottomControl.isVisible = true

        scrollView.isVisible = true

        ImmersionBar.setTitleBar(this, clTitle)

        val data = allPeopleAdapter.data
        checkSelects(data)
        allPeopleAdapter.replaceData(data)

        setSelectNumber()

        setAllSelectStatus()

        etSearchContent.setText("")

        Handler().postDelayed(Runnable { KeyboardUtils.hideSoftInput(etSearchContent) }, 300)

    }

    /**
     * 显示已选择人员
     */
    private fun showSelectedLayout() {
        layoutMode = LAYOUT_SELECTED_PEOPLE

        tvTitle.text = "已选择"

        clTitle.isVisible = true
        flSearch.isVisible = false
        flSearch.visibility = View.GONE
        clSearchTitle.isVisible = false
        tvRightOper.isVisible = false

        rlAllPeople.isVisible = false
        rlSelectedPeople.isVisible = true
        rlSearchPeople.isVisible = false

        clBottomControl.isVisible = false

        scrollView.isVisible = false

        ImmersionBar.setTitleBar(this, clTitle)

        selectedPeopleAdapter.replaceData(selectPeoples)
    }

    /**
     * 显示搜索人员
     */
    private fun showSearchLayout() {
        layoutMode = LAYOUT_SEARCH_PEOPLE

        clTitle.isVisible = false
        flSearch.isVisible = false
        clSearchTitle.isVisible = true

        rlAllPeople.isVisible = false
        rlSelectedPeople.isVisible = false
        rlSearchPeople.isVisible = true

        clBottomControl.isVisible = false

        scrollView.isVisible = false

        ImmersionBar.setTitleBar(this, clSearchTitle)

        KeyboardUtils.showSoftInput(etSearchContent)
    }

    override fun getLayoutId(): Int = R.layout.activity_select_people

    companion object {
        val LAYOUT_ALL_PEOPLE = 0

        val LAYOUT_SELECTED_PEOPLE = 1

        val LAYOUT_SEARCH_PEOPLE = 2

        val NO_SELECT_LIST = "NO_SELECT_LIST"

        fun startActivity(context: Context, noSelectList: List<Member>) {
            var intent = Intent(context, SelectPeopleActivity::class.java)
            intent.putExtra(NO_SELECT_LIST, noSelectList as Serializable)
            context.startActivity(intent)
        }
    }
}

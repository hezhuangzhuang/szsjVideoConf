import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gyf.immersionbar.ImmersionBar
import com.hjq.bar.OnTitleBarListener
import com.hw.baselibrary.image.ImageLoader
import com.hw.baselibrary.ui.fragment.BaseLazyFragment
import com.hw.baselibrary.utils.ToastHelper
import com.hw.baselibrary.widgets.XCollapsingToolbarLayout
import com.hw.kotlinmvpandroidxframe.R
import com.hw.kotlinmvpandroidxframe.mvp.contract.HomeContract
import com.hw.kotlinmvpandroidxframe.mvp.presenter.HomePresenter
import com.hw.kotlinmvpandroidxframe.net.bean.NewBean
import com.hw.kotlinmvpandroidxframe.ui.adapter.NewsListAdapter
import com.hw.kotlinmvpandroidxframe.ui.adapter.item.NewsListItem
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : BaseLazyFragment(),
    XCollapsingToolbarLayout.OnScrimsListener,
    HomeContract.View {
    override fun onError(text: String) {
    }

    private lateinit var adapter: NewsListAdapter

    private var pageNum: Int = 0

    private val dataList: ArrayList<NewsListItem> by lazy {
        ArrayList<NewsListItem>()
    }

    private val mPresenter by lazy {
        HomePresenter()
    }

    init {
        mPresenter.attachView(this)
    }

    //加载更多
    private var loadingMore = false

    override fun doLazyBusiness() {
        ToastHelper.showShort("请求数据")
        mTitleBar!!.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onRightClick(v: View?) {
                ToastHelper.showShort("点击右边按钮")
            }

            override fun onTitleClick(v: View?) {
                ToastHelper.showShort("点击title")
            }

            override fun onLeftClick(v: View?) {
                ToastHelper.showShort("请求数据FlowFragment数据")
                mPresenter.queryFirstStudys(1)
            }
        })

        tvAddress.setOnClickListener {
            mPresenter.queryFirstStudys(1)
        }

        mPresenter.queryFirstStudys(1)
    }

    override fun initData(bundle: Bundle?) {
        // 给这个ToolBar设置顶部内边距，才能和TitleBar进行对齐
        ImmersionBar.setTitleBar(mActivity, t_test_title)

        //设置渐变监听
        ctl_test_bar.setOnScrimsListener(this)

        rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    val childCount = rvList.childCount;
                    val itemCount = rvList.layoutManager!!.itemCount;
                    val firstVisibleItem =
                        (rvList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (firstVisibleItem + childCount == itemCount) {
                        if (!loadingMore) {
                            loadingMore = true
                            mPresenter.loadMoreStudys(pageNum + 1)
                        }
                    }
                }
            }
        })
    }

    override fun bindLayout(): Int = R.layout.fragment_home

    override fun initView(savedInstanceState: Bundle?, contentView: View) {
        ImageLoader.with(this)
            .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1573887714&di=b945e289149ad5506c49237ce6ae02f4&imgtype=jpg&er=1&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201312%2F27%2F20131227232853_fJCmr.jpeg")
            .circle(20)
            .into(ivContent)

        adapter = NewsListAdapter(R.layout.item_test, dataList)
        rvList.adapter = adapter
        rvList.layoutManager = LinearLayoutManager(mActivity)
    }

    /**
     * 是否使用沉浸式，true：使用
     * 默认为不使用
     */
    override fun isStatusBarEnabled(): Boolean {
        return !super.isStatusBarEnabled()
    }

    override fun onScrimsStateChange(layout: XCollapsingToolbarLayout?, shown: Boolean) {
        if (shown) {
            tvAddress.setTextColor(ContextCompat.getColor(mActivity, R.color.black))
            tv_test_search.setBackgroundResource(R.drawable.bg_home_search_bar_gray)
            getStatusBarConfig().statusBarDarkFont(true).init()
        } else {
            tvAddress.setTextColor(ContextCompat.getColor(mActivity, R.color.white))
            tv_test_search.setBackgroundResource(R.drawable.bg_home_search_bar_transparent)
            getStatusBarConfig().statusBarDarkFont(false).init()
        }
    }

    override fun statusBarDarkFont(): Boolean {
        return ctl_test_bar.isScrimsShown()
    }

    override fun showFirstNewList(studyList: ArrayList<NewBean>) {
        pageNum = 1

        var dataList: ArrayList<NewsListItem> = ArrayList<NewsListItem>()
        studyList.forEach {
            dataList.add(NewsListItem((it)))
        }

        adapter.replaceData(dataList)
    }

    override fun showMoreNewList(studyList: ArrayList<NewBean>) {
        loadingMore = false

        pageNum += 1

        var dataList: ArrayList<NewsListItem> = ArrayList<NewsListItem>()

        studyList.forEach {
            dataList.add(NewsListItem((it)))
        }

        adapter.addData(dataList)
    }

    override fun showError(errorMsg: String) {
        ToastHelper.showShort(errorMsg)
    }

    override fun showEmptyView() {
        ToastHelper.showShort("没有数据了")
    }

    override fun onDestroy() {
        super.onDestroy()

        mPresenter.detachView()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment()

    }
}
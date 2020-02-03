package com.hw.huaweivclib.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hw.baselibrary.net.RetrofitManager;
import com.hw.baselibrary.net.Urls;
import com.hw.baselibrary.rx.scheduler.CustomCompose;
import com.hw.baselibrary.ui.activity.BaseActivity;
import com.hw.baselibrary.utils.ToastHelper;
import com.hw.baselibrary.utils.sharedpreferences.SPStaticUtils;
import com.hw.huaweivclib.R;
import com.hw.huaweivclib.adapter.ChairSelectAdapter;
import com.hw.huaweivclib.net.ConfControlApi;
import com.hw.huaweivclib.net.respone.BaseData;
import com.hw.huaweivclib.net.respone.ConfBeanRespone;
import com.hw.provider.router.RouterPath;
import com.hw.provider.user.UserContants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 选择主席界面
 */
@Route(path = RouterPath.Huawei.CHAIR_SELECT)
public class ChairSelectActivity extends BaseActivity {
    private RecyclerView rvList;
    private TitleBar titleBar;

    private ChairSelectAdapter chairSelectAdapter;

    private List<ConfBeanRespone.DataBean.SiteStatusInfoListBean> onlineList = new ArrayList<>();

    //会议id
    private String smcConfId;

    @Override
    public void initData(@Nullable Bundle bundle) {
        smcConfId = getIntent().getStringExtra(RouterPath.Huawei.FILED_SMC_CONF_ID);

        onlineList = (List<ConfBeanRespone.DataBean.SiteStatusInfoListBean>) getIntent().getSerializableExtra(RouterPath.Huawei.FILED_ONLINE_LIST);
        initAdapter();
    }

    private void initAdapter() {
        chairSelectAdapter = new ChairSelectAdapter(R.layout.item_chair_select, onlineList);
        chairSelectAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tv_set_chair) {
                    ConfBeanRespone.DataBean.SiteStatusInfoListBean statusInfoListBean = (ConfBeanRespone.DataBean.SiteStatusInfoListBean) adapter.getItem(position);
                    setConfChair(statusInfoListBean);
                }
            }
        });
    }

    /**
     * 指定主席并退出会议
     *
     * @param statusInfoListBean
     */
    private void setConfChair(final ConfBeanRespone.DataBean.SiteStatusInfoListBean statusInfoListBean) {
        RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .setConfChair(smcConfId, statusInfoListBean.siteUri)
                .flatMap(new Function<BaseData, ObservableSource<BaseData>>() {
                    @Override
                    public ObservableSource<BaseData> apply(BaseData baseData) throws Exception {
                        return leaveConfRequest();
                    }
                })
                .compose(new CustomCompose())
                .subscribe(new Consumer<BaseData>() {
                    @Override
                    public void accept(BaseData baseData) throws Exception {
                        //请求成功
                        if (null != baseData && BaseData.SUCEESS_CODE == baseData.code) {
                            ToastHelper.INSTANCE.showShort("指定主席成功");
                            finish();
                        } else {
                            ToastHelper.INSTANCE.showShort(baseData.msg);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ToastHelper.INSTANCE.showShort(throwable.getMessage());
                    }
                });
    }

    /**
     * 离开会议的网络请求
     */
    private Observable<BaseData> leaveConfRequest() {
        return RetrofitManager.INSTANCE.create(ConfControlApi.class, Urls.INSTANCE.getFILE_URL())
                .leaveConf(smcConfId, SPStaticUtils.getString(UserContants.HUAWEI_ACCOUNT));
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_chair_select;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState, @NotNull View contentView) {
        titleBar = findViewById(R.id.titleBar);
        rvList = findViewById(R.id.rv_list);

        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(chairSelectAdapter);
    }

    @Override
    public void doBusiness() {

    }

    @Override
    public void setListeners() {
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                finish();
            }

            @Override
            public void onTitleClick(View v) {

            }

            @Override
            public void onRightClick(View v) {

            }
        });
    }

    @Override
    public void onError(@NotNull String text) {

    }
}

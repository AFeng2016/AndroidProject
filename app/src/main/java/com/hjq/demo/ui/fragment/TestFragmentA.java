package com.hjq.demo.ui.fragment;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.hjq.bar.TitleBar;
import com.hjq.demo.R;
import com.hjq.demo.common.CommonLazyFragment;
import com.hjq.demo.widget.XCollapsingToolbarLayout;

import org.xutils.view.annotation.ViewInject;

/**
 * author : HJQ
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 项目炫酷效果示例
 */
public class TestFragmentA extends CommonLazyFragment
        implements XCollapsingToolbarLayout.OnScrimsListener {

    @ViewInject(R.id.abl_test_bar)
    AppBarLayout mAppBarLayout;
    @ViewInject(R.id.ctl_test_bar)
    XCollapsingToolbarLayout mCollapsingToolbarLayout;
    @ViewInject(R.id.t_test_title)
    Toolbar mToolbar;
    @ViewInject(R.id.tb_test_bar)
    TitleBar mTitleBar;

    @ViewInject(R.id.tv_test_address)
    TextView mAddressView;
    @ViewInject(R.id.tv_test_search)
    TextView mSearchView;

    public static TestFragmentA newInstance() {
        return new TestFragmentA();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test_a;
    }

    @Override
    protected int getTitleBarId() {
        return R.id.tb_test_bar;
    }

    @Override
    protected void initView() {
        // 给这个ToolBar设置顶部内边距，才能和TitleBar进行对齐
        ImmersionBar.setTitleBar(getSupportActivity(), mToolbar);

        //设置渐变监听
        mCollapsingToolbarLayout.setOnScrimsListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

    @Override
    public boolean statusBarDarkFont() {
        return mCollapsingToolbarLayout.isScrimsShown();
    }

    /**
     * {@link XCollapsingToolbarLayout.OnScrimsListener}
     */
    @Override
    public void onScrimsStateChange(boolean shown) {
        // CollapsingToolbarLayout 发生了渐变
        if (shown) {
            mAddressView.setTextColor(getResources().getColor(R.color.black));
            mSearchView.setBackgroundResource(R.drawable.bg_home_search_bar_gray);
            getStatusBarConfig().statusBarDarkFont(true).init();
        } else {
            mAddressView.setTextColor(getResources().getColor(R.color.white));
            mSearchView.setBackgroundResource(R.drawable.bg_home_search_bar_transparent);
            getStatusBarConfig().statusBarDarkFont(false).init();
        }
    }
}
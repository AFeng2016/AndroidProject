package com.hjq.demo.ui.fragment;

import android.view.View;
import android.widget.Button;

import com.hjq.demo.R;
import com.hjq.demo.common.CommonLazyFragment;
import com.hjq.demo.ui.activity.AboutActivity;
import com.hjq.demo.ui.activity.LoginActivity;
import com.hjq.demo.ui.activity.RegisterActivity;
import com.hjq.demo.ui.activity.RicheditorActivity;
import com.hjq.demo.ui.activity.WebActivity;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 *    author : HJQ
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 项目界面跳转示例
 */
public class TestFragmentD extends CommonLazyFragment
        implements View.OnClickListener {

    @ViewInject(R.id.btn_test_login)
    Button mLoginView;
    @ViewInject(R.id.btn_test_register)
    Button mRegisterView;
    @ViewInject(R.id.btn_test_about)
    Button mAboutView;
    @ViewInject(R.id.btn_test_browser)
    Button mBrowserView;

    public static TestFragmentD newInstance() {
        return new TestFragmentD();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_test_d;
    }

    @Override
    protected int getTitleBarId() {
        return R.id.tb_test_d_title;
    }

    @Override
    protected void initView() {
        mLoginView.setOnClickListener(this);
        mRegisterView.setOnClickListener(this);
        mAboutView.setOnClickListener(this);
        mBrowserView.setOnClickListener(this);
    }

    @Event(R.id.btn_test_richeditor)
    private void on_btn_test_richeditor(View v){
        startActivity(RicheditorActivity.class);
    }


    /**
     * {@link View.OnClickListener}
     */
    @Override
    public void onClick(View v) {
        if (v == mLoginView) {
            startActivity(LoginActivity.class);
        }else if (v == mRegisterView) {
            startActivity(RegisterActivity.class);
        }else if (v == mAboutView) {
            startActivity(AboutActivity.class);
        }else if (v == mBrowserView) {
            startActivity(WebActivity.class);
        }
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }
}
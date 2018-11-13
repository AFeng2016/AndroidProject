package com.hjq.demo.ui.activity;

import android.os.Build;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.BarHide;
import com.hjq.demo.R;
import com.hjq.demo.common.CommonActivity;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;

import java.util.List;

import org.xutils.view.annotation.ViewInject;

/**
 * author : HJQ
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 启动界面 -- 没有权限则申请，有的话就进入主页面
 */
public class LauncherActivity extends CommonActivity
        implements OnPermission, Animation.AnimationListener {

    @ViewInject(R.id.iv_launcher_bg)
    ImageView mImageView;
    @ViewInject(R.id.iv_launcher_icon)
    ImageView mIconView;
    @ViewInject(R.id.iv_launcher_name)
    TextView mTextView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    protected int getTitleBarId() {
        return 0;
    }

    @Override
    protected void initView() {
        //初始化动画
        initStartAnim();
        //设置状态栏和导航栏参数
        getStatusBarConfig()
                .fullScreen(true)//有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
                .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)//隐藏状态栏
                .transparentNavigationBar()//透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
                .init();


    }

    //动画持续时间
    private static final int ANIM_TIME = 1000;

    /**
     * 启动动画
     */
    private void initStartAnim() {
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.4f, 1.0f);
        aa.setDuration(ANIM_TIME * 2);
        aa.setAnimationListener(this);
        mImageView.startAnimation(aa);

        ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(ANIM_TIME);
        mIconView.startAnimation(sa);

        RotateAnimation ra = new RotateAnimation(180, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(ANIM_TIME);
        mTextView.startAnimation(ra);
    }

    //自动申请权限
    private void requestFilePermission() {
        //必须设置 targetSdkVersion >= 23 才能正常检测权限
        if (getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
            XXPermissions.with(this)
                    .permission(Permission.Group.STORAGE)
                    .request(this);
        } else {
            startActivity(HomeActivity.class);
            finish();
        }

    }

    /**
     * {@link OnPermission}
     */

    @Override
    public void hasPermission(List<String> granted, boolean isAll) {
        //拥有权限后,跳转
        startActivity(HomeActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        //禁用返回键
        //super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
            if (XXPermissions.isHasPermission(LauncherActivity.this, Permission.Group.STORAGE)) {
                hasPermission(null, true);
            } else {
                requestFilePermission();
            }
        }

    }

    @Override
    public boolean isSupportSwipeBack() {
        //不使用侧滑功能
        return !super.isSupportSwipeBack();
    }

    @Override
    public void noPermission(List<String> denied, boolean quick) {
        if (quick) {
            ToastUtils.show("没有权限访问文件，请手动授予权限");
            XXPermissions.gotoPermissionSettings(LauncherActivity.this, true);
        } else {
            ToastUtils.show("请先授予文件读写权限");
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestFilePermission();
                }
            }, 2000);
        }
    }

    /**
     * {@link Animation.AnimationListener}
     */

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        requestFilePermission();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }


    @Override
    protected void initOrientation() {
        //Android 8.0踩坑记录：Only fullscreen opaque activities can request orientation
        // https://www.jianshu.com/p/d0d907754603
        //super.initOrientation();
    }
}

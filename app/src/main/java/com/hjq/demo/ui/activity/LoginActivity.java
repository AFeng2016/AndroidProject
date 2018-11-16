package com.hjq.demo.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.demo.Constant;
import com.hjq.demo.R;
import com.hjq.demo.bean.User;
import com.hjq.demo.common.CommonActivity;
import com.hjq.demo.tool.CommonMethod;
import com.hjq.demo.tool.SystemInfoUtils;
import com.hjq.demo.widget.LoadingDialog;
import com.hjq.toast.ToastUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Map;

/**
 * author : HJQ
 * github : https://github.com/getActivity/AndroidProject
 * time   : 2018/10/18
 * desc   : 登录界面
 */
public class LoginActivity extends CommonActivity {

    @ViewInject(R.id.ll_loginWithQQ)
    LinearLayout ll_loginWithQQ;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected int getTitleBarId() {
        return 0;
    }


    @Override
    protected void initView() {

    }


    //微信登录
    @Event(R.id.ll_loginWithWX)
    private void btnWxLoginClick(View v) {
        MobclickAgent.onEvent(thisAct, "login_wx");
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(this).setShareConfig(config);
        UMShareAPI.get(this).getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, authListener);
    }

    //QQ登录
    @Event(R.id.ll_loginWithQQ)
    private void btnQQLogin(View v) {
        MobclickAgent.onEvent(thisAct, "login_qq");
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(this).setShareConfig(config);
        UMShareAPI.get(this).getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, authListener);
    }


    UMAuthListener authListener = new UMAuthListener() {


        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        /**
         * 授权成功的回调
         * @param share_media 平台名称
         * @param i 行为序号，开发者用不上
         * @param map 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            Log.d("--login", "onComplete: " + map);
            LoadingDialog.show(thisAct, ll_loginWithQQ);
            RequestParams params = new RequestParams(Constant.DOMAIN + "user/appLogin");
            params.addBodyParameter("user.openId", map.get("openid"));
            params.addBodyParameter("user.unionid", map.get("uid"));
            params.addBodyParameter("user.imei", SystemInfoUtils.getImei(thisAct));
            params.addBodyParameter("user.niName", map.get("screen_name"));
            String gender = map.get("gender");
            if (gender != null && gender.equals("男")) gender = "1";
            else if (gender != null && gender.equals("女")) gender = "2";
            else gender = "2";
            params.addBodyParameter("user.sex", gender);
            params.addBodyParameter("user.country", map.get("country"));
            params.addBodyParameter("user.province", map.get("province"));
            params.addBodyParameter("user.city", map.get("city"));
            params.addBodyParameter("user.photo", map.get("iconurl"));
            params.addBodyParameter("user.packName", thisAct.getPackageName());
            String pushId = PushAgent.getInstance(thisAct).getRegistrationId();
            params.addBodyParameter("user.pushId", pushId);
            params.addBodyParameter("user.loginType", share_media.name());
            String userChannel = CommonMethod.getAppMetaData(thisAct, "UMENG_CHANNEL");
            if (userChannel != null && userChannel.length() > 0) {
                params.addBodyParameter("user.userChannel", userChannel);
            }
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    try {
                        if (!TextUtils.isEmpty(result)) {
                            User user = new Gson().fromJson(result, new TypeToken<User>() {
                            }.getType());
                            ToastUtils.show("登录成功");
                            User.userLogin(thisAct, user);
                            finish();
                        } else {
                            LoadingDialog.cancel();
                            ToastUtils.show("登录失败，请重试");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ToastUtils.show("网络连接繁忙，请重试");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    LoadingDialog.cancel();
                }

            });
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean isSupportSwipeBack() {
        //不使用侧滑功能
        return !super.isSupportSwipeBack();
    }


}
package com.hjq.demo.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hjq.demo.R;
import com.hjq.demo.Service.DownloadService;
import com.hjq.demo.bean.Appversion;
import com.hjq.demo.common.CommonActivity;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * 更新软件
 * Created by Administrator on 2016/3/15.
 */
public class UpdateActivity extends CommonActivity {


    @ViewInject(R.id.btn_update)
    Button btn_update;
    @ViewInject(R.id.tv_desc)
    TextView tv_desc;
    @ViewInject(R.id.pb)
    ProgressBar pb;

    BroadcastReceiver receiver;
    Appversion version;

    @Override
    protected int getLayoutId() {
        return R.layout.a_update_dialog;
    }

    @Override
    protected int getTitleBarId() {
        return 0;
    }

    @Override
    protected void initView() {
        version = (Appversion) getIntent().getSerializableExtra("version");
        tv_desc.setText(version.getUpDes().replace("|", "\r\n"));
        if (version.getMustup().equals("1")) {
            //强行更新
        }
    }


    @Event(R.id.btn_update)
    private void download(View v) {
        downloadApk();
        btn_update.setText("正在下载");
    }


    public void downloadApk() {
        Intent downloadIntent = new Intent(thisAct, DownloadService.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", version.getDownUrl());
        bundle.putString("apkName", version.getApkName());
        if (!TextUtils.isEmpty(version.getApkMd5())) {
            bundle.putString("apkmd5", version.getApkMd5());
        }
        downloadIntent.putExtras(bundle);
        thisAct.startService(downloadIntent);
        // 设置广播接收器，当新版本的apk下载完成后自动弹出安装界面
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.lajianghongbao");
        intentFilter.addAction("down_error");
        receiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (TextUtils.isEmpty(action)) {
                    return;
                }

                if (action.equals("com.lajianghongbao")) {
                    int progress = intent.getIntExtra("progress", 0);
                    if (progress > 0) {
                        pb.setVisibility(View.VISIBLE);
                        pb.setProgress(progress);
                        btn_update.setVisibility(View.GONE);
                    }

                    //下载完成
                    String downloadFile = intent.getStringExtra("downloadFile");
                    if (downloadFile != null && downloadFile.equals("") == false) {

                        //开始安装
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        String pathString = intent.getStringExtra("downloadFile");
                        install.setDataAndType(Uri.parse("file://" + pathString), "application/vnd.android.package-archive");
                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(install);

                        btn_update.setVisibility(View.VISIBLE);
                        btn_update.setText("立即下载");
                        pb.setVisibility(View.GONE);
                    }
                } else if (action.equals("down_error")) {
                    //下载错误
                    btn_update.setVisibility(View.VISIBLE);
                    btn_update.setText("立即更新");
                    pb.setVisibility(View.GONE);
                }


            }
        };
        registerReceiver(receiver, intentFilter);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            //不让按返回键
            return true;
        }
        return false;
    }

    @Event(R.id.iv_cancel)
    private void cancel(View v) {
        finish();
    }

    @Override
    protected void onDestroy() {
        // 移除广播接收器
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }


}

package com.hjq.demo.Service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.hjq.demo.Constant;
import com.hjq.demo.R;
import com.hjq.demo.tool.CommonMethod;
import com.hjq.demo.ui.activity.HomeActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/3/11.
 */
public class DownloadService extends IntentService {
    private static final String TAG = DownloadService.class.getSimpleName();
    private NotificationManager notificationManager;
    private Notification notification;
    private RemoteViews rViews;
    private int fileLength, downloadLength;
    // 定时器，每隔一段时间检查下载进度，然后更新Notification上的ProgressBar
    private Handler handler = new Handler();
    private Runnable run = new Runnable() {
        public void run() {
            rViews.setProgressBar(R.id.progressbar, 100, downloadLength * 100 / fileLength, false);
            notification.contentView = rViews;
            notificationManager.notify(0, notification);
            handler.postDelayed(run, 500);

            // 广播出去，用于显示下载进度
            Intent sendIntent = new Intent(Constant.ON_DOWNLOAD);
            sendIntent.putExtra("progress", downloadLength * 100 / fileLength);
            sendBroadcast(sendIntent);

            Log.d("--onLoading", "sendBroadcast-downloadLength: " + downloadLength);
            Log.d("--onLoading", "sendBroadcast-fileLength: " + fileLength);
        }
    };

    public DownloadService() {
        super(Constant.ON_DOWNLOAD);
    }

    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        // 获得下载文件的url
        String downloadUrl = bundle.getString("url");
        String apkmd5 = bundle.getString("apkmd5");
        String apkName = bundle.getString("apkName");
        if (TextUtils.isEmpty(apkName)) {
            apkName = "file.apk";
        }
        // 设置文件下载后的保存路径，保存在SD卡根目录的Download文件夹
        File dirs = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
        // 检查文件夹是否存在，不存在则创建
        if (!dirs.exists()) {
            dirs.mkdir();
        }
        File file = new File(dirs, apkName);
        // 设置Notification
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker("正在下载新版本");
        builder.setContentText(getString(R.string.app_name) + "：正在下载新版本");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        notification = builder.build();


        Intent intentNotifi = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentNotifi, 0);
        notification.contentIntent = pendingIntent;
        // 加载Notification的布局文件
        rViews = new RemoteViews(getPackageName(), R.layout.item_download_progress);
        // 设置下载进度条
        rViews.setProgressBar(R.id.progressbar, 100, 0, false);
        notification.contentView = rViews;
        notificationManager.notify(0, notification);
        // 开始下载
        downloadFile(downloadUrl, file);

        boolean hasException = false;  //判断是否出现异常

        //检查文件完整性
        try {
            File apk = new File(file.getPath());
            if (apk.exists()) {
                String md5 = CommonMethod.getFileMD5(apk);
                Log.d("--md5", "onReceive: " + md5);
                if (!TextUtils.isEmpty(md5)) {
                    if (!TextUtils.isEmpty(apkmd5)) {
                        Log.d("--md5", "apkmd5: " + apkmd5);

                        if (apkmd5.equals(md5)) {
                            // 移除通知栏
                            notificationManager.cancel(0);
                            // 广播出去，由广播接收器来处理下载完成的文件
                            Intent sendIntent = new Intent(Constant.ON_DOWNLOAD);
                            // 把下载好的文件的保存地址加进Intent
                            sendIntent.putExtra("downloadFile", file.getPath());
                            sendBroadcast(sendIntent);
                        } else {

                            try {
                                x.task().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "文件完整性检验失败，将尝试重新下载", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.d("--md5", "文件不完整，将尝试重新下载");
                            Log.d("--md5", "downloadUrl:" + downloadUrl);

                            //尝试使用断点续传方式下载
                            startDownload(downloadUrl, dirs.getPath(), new Callback.ProgressCallback<File>() {
                                @Override
                                public void onWaiting() {

                                }

                                @Override
                                public void onStarted() {
                                    Log.d("--onLoading", "onStarted: " + downloadLength);

                                }

                                @Override
                                public void onLoading(long total, long current, boolean b) {
//                                    fileLength = Math.round(total);
//                                    //做一下判断，防止自回调过于频繁，造成更新通知栏进度过于频繁，而出现卡顿的问题。
//                                    int rate = Math.round(current);
//                                    if (downloadLength != rate) {
//                                        //重新赋值
//                                        downloadLength = rate;
//                                    }
                                    //不显示进度
                                    Log.d("--onLoading", "downloadLength: " + downloadLength);
                                    Log.d("--onLoading", "fileLength: " + fileLength);
                                }

                                @Override
                                public void onSuccess(File result) {
                                    Log.d("--md5", "下载完成");

                                    // 移除通知栏
                                    notificationManager.cancel(0);
                                    // 广播出去，由广播接收器来处理下载完成的文件
                                    Intent sendIntent = new Intent(Constant.ON_DOWNLOAD);
                                    // 把下载好的文件的保存地址加进Intent
                                    sendIntent.putExtra("downloadFile", result.getPath());
                                    sendBroadcast(sendIntent);
                                }

                                @Override
                                public void onError(Throwable throwable, boolean b) {
                                    Log.d("--md5", "onError：" + throwable.getMessage());
                                    try {
                                        x.task().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "网络无法连接，请检查后重试", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    // 移除通知栏
                                    notificationManager.cancel(0);
                                    // 广播出去，由广播接收器来处理状态
                                    Intent sendIntent = new Intent("down_error");
                                    sendBroadcast(sendIntent);

                                }

                                @Override
                                public void onCancelled(CancelledException e) {

                                }

                                @Override
                                public void onFinished() {

                                }
                            });

                        }
                    } else {
                        //就算服务器不返回md5码也要尝试安装
                        hasException = true;
                    }
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            hasException = true;
        } catch (IOException e) {
            e.printStackTrace();
            hasException = true;
        }

        //如果上面的处理出现异常则尝试使用以往方式进行安装
        if (hasException) {
            // 移除通知栏
            notificationManager.cancel(0);
            // 广播出去，由广播接收器来处理下载完成的文件
            Intent sendIntent = new Intent(Constant.ON_DOWNLOAD);
            // 把下载好的文件的保存地址加进Intent
            sendIntent.putExtra("downloadFile", file.getPath());
            sendBroadcast(sendIntent);
        }


    }

    private void downloadFile(String downloadUrl, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "找不到保存下载文件的目录");
            e.printStackTrace();
        }
        InputStream ips = null;
        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.setReadTimeout(10000);
            huc.setConnectTimeout(3000);

            try {
                //友盟发现此值可能为null
                if (huc.getHeaderField("Content-Length") != null) {
                    fileLength = Integer.valueOf(huc.getHeaderField("Content-Length"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            ips = huc.getInputStream();
            // 拿到服务器返回的响应码
            int hand = huc.getResponseCode();
            if (hand == 200) {
                // 开始检查下载进度
                handler.post(run);
                // 建立一个byte数组作为缓冲区，等下把读取到的数据储存在这个数组
                byte[] buffer = new byte[8192];
                int len = 0;
                while ((len = ips.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    downloadLength = downloadLength + len;
                }
            } else {
                Log.e(TAG, "服务器返回码" + hand);
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (ips != null) {
                    ips.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onDestroy() {
        // 移除定時器
        handler.removeCallbacks(run);
        super.onDestroy();
    }


    private void startDownload(String apkUrl, String target, final Callback.ProgressCallback<File> downloadListener) {
        if (TextUtils.isEmpty(apkUrl)) {
            return;
        }

        RequestParams params = new RequestParams(apkUrl);
        params.addBodyParameter("time", String.valueOf(System.currentTimeMillis()));
        params.setAutoResume(true);// 断点续传
        params.setSaveFilePath(target);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(final File result) {
                x.task().post(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadListener != null) {
                            downloadListener.onSuccess(result);
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.err.println("下载错误：" + ex.getMessage());
                if (downloadListener != null) {
                    downloadListener.onError(ex, isOnCallback);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                if (downloadListener != null) {
                    downloadListener.onStarted();
                }
            }

            @Override
            public void onLoading(final long total, final long current, final boolean isDownloading) {
                x.task().post(new Runnable() {
                    @Override
                    public void run() {
                        if (downloadListener != null) {
                            downloadListener.onLoading(total, current, isDownloading);
                        }
                    }
                });
            }
        });
    }


}
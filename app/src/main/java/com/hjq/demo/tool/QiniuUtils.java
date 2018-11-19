package com.hjq.demo.tool;

import android.util.Log;

import com.hjq.baselibrary.listener.OnStringCallBack;
import com.hjq.baselibrary.utils.FileUtils;
import com.hjq.toast.ToastUtils;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.AsyncRun;
import com.qiniu.android.utils.Etag;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by AFeng on 2018/11/16.
 */
public class QiniuUtils {

    private QiniuUtils() {
    }

    private static UploadManager uploadManager;


    /**
     * 上传文件
     *
     * @param uploadToken    服务器token
     * @param uploadFilePath 文件路径
     * @param callBack       返回文件网络路径
     */
    public static void upload(String uploadToken, String uploadFilePath, final OnStringCallBack callBack) {
        upload(uploadToken, uploadFilePath, null, callBack);
    }


    /**
     * 上传文件
     *
     * @param uploadToken    服务器token
     * @param uploadFilePath 文件路径
     * @param uploadFileKey  指定文件名
     * @param callBack       返回文件网络路径
     */
    public static void upload(String uploadToken, String uploadFilePath, String uploadFileKey, final OnStringCallBack callBack) {
        if (uploadManager == null) {
            Configuration config = new Configuration.Builder()
                    .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
                    .putThreshhold(1024 * 1024)   // 启用分片上传阀值。默认512K
                    .connectTimeout(10)           // 链接超时。默认10秒
                    .useHttps(true)               // 是否使用https上传域名
                    .responseTimeout(60)          // 服务器响应超时。默认60秒
//                    .recorder(null)           // recorder分片上传时，已上传片记录器。默认null
//                    .recorder(recorder, keyGen)   // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                    .zone(FixedZone.zone2)        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                    .build();
            uploadManager = new UploadManager(config);
        }
        File uploadFile = new File(uploadFilePath);
        UploadOptions uploadOptions = new UploadOptions(null, null, false,
                new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        //上传进度
                    }
                }, null);

        if (uploadFileKey == null) {
            try {
                uploadFileKey = Etag.file(uploadFile) + "." + FileUtils.getExtensionName(uploadFile);
                Log.d("--respInfo", "getName: " + uploadFile.getName());
                Log.d("--respInfo", "getExtensionName: " + FileUtils.getExtensionName(uploadFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        uploadManager.put(uploadFile, uploadFileKey, uploadToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo respInfo, JSONObject jsonData) {
                        Log.d("--respInfo", "complete: " + respInfo);
                        Log.d("--respInfo", "jsonData: " + jsonData);
                        Log.d("--respInfo", "key: " + key);
                        if (respInfo.isOK()) {
                            try {
                                String fileKey = jsonData.optString("key"); //文件名
//                                String fileHash = jsonData.optString("hash");
                                callBack.onCallBack(fileKey);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            AsyncRun.runInMain(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.show("上传失败，请重试");
                                }
                            });
                        }
                    }

                }, uploadOptions);
    }


}

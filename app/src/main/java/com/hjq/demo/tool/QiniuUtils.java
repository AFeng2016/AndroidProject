package com.hjq.demo.tool;

import com.hjq.baselibrary.listener.OnStringCallBack;
import com.hjq.toast.ToastUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.utils.AsyncRun;

import org.json.JSONObject;

import java.io.File;

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
            uploadManager = new UploadManager();
        }
        File uploadFile = new File(uploadFilePath);
        UploadOptions uploadOptions = new UploadOptions(null, null, false,
                new UpProgressHandler() {
                    @Override
                    public void progress(String key, double percent) {
                        //上传进度
                    }
                }, null);

        uploadManager.put(uploadFile, uploadFileKey, uploadToken,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo respInfo, JSONObject jsonData) {
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

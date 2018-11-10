package com.hjq.demo.api;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.Map;


public class HttpUtils {


    /**
     * 异步get请求
     *
     * @param callback
     */
    public static void get(RequestParams params, final XCallBack callback) {

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                onSuccessResponse(result, callback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callback.onError();
                System.err.println("网络请求出错：" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                onFinishedResponse(callback);
            }
        });

    }

    /**
     * 异步post请求
     *
     * @param callback
     */
    public static void post(RequestParams params, final XCallBack callback) {

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                onSuccessResponse(result, callback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callback.onError();
                System.err.println("网络请求出错：" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                onFinishedResponse(callback);
            }
        });
    }


    /**
     * 带缓存数据的异步 get请求
     *
     * @param pnewCache
     * @param callback
     */
    public static void getCache(RequestParams params, final boolean pnewCache, final XCallBack callback) {

        x.http().get(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                onSuccessResponse(result, callback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callback.onError();
                System.err.println("网络请求出错：" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                onFinishedResponse(callback);
            }

            @Override
            public boolean onCache(String result) {
                onSuccessResponse(result, callback);
                return pnewCache;
            }
        });
    }

    public static void getCache(RequestParams params, final XCallBack callback) {
        getCache(params, false, callback);
    }

    // 得到缓存数据, 缓存过期后不会进入这个方法.
    // 如果服务端没有返回过期时间, 参考params.setCacheMaxAge(maxAge)方法.
    //
    // * 客户端会根据服务端返回的 header 中 max-age 或 expires 来确定本地缓存是否给 onCache 方法.
    //   如果服务端没有返回 max-age 或 expires, 那么缓存将一直保存, 除非这里自己定义了返回false的
    //   逻辑, 那么xUtils将请求新数据, 来覆盖它.
    //
    // * 如果信任该缓存返回 true, 将不再请求网络;
    //   返回 false 继续请求网络, 但会在请求头中加上ETag, Last-Modified等信息,
    //   如果服务端返回304, 则表示数据没有更新, 不继续加载数据.

    /**
     * 带缓存数据的异步 post请求
     *
     * @param pnewCache
     * @param callback
     */
    public static void postCache(RequestParams params, final boolean pnewCache, final XCacheCallBack callback) {

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(final String result) {
                // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
                x.task().post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess(result, false);
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callback.onError();
                System.err.println("网络请求出错：" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }


            @Override
            public void onFinished() {
                x.task().post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onFinished();
                        }
                    }
                });
            }

            @Override
            public boolean onCache(final String result) {
                x.task().post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onSuccess(result, true);
                        }
                    }
                });
                return pnewCache; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
            }
        });
    }

    //先显示缓存再更新
    public static void postCache(RequestParams params, final XCacheCallBack callback) {
        postCache(params, false, callback);
    }


    /**
     * 文件上传
     *
     * @param file
     * @param callback
     */
    public static void upLoadFile(RequestParams params, Map<String, File> file, final XCallBack callback) {
        if (file != null) {
            for (Map.Entry<String, File> entry : file.entrySet()) {
                params.addBodyParameter(entry.getKey(), entry.getValue().getAbsoluteFile());
            }
        }
        // 有上传文件时使用multipart表单, 否则上传原始文件流.
        params.setMultipart(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                onSuccessResponse(result, callback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                callback.onError();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                onFinishedResponse(callback);
            }
        });

    }


    /**
     * 文件下载
     *
     * @param callBack
     */
    public static void downLoadFile(RequestParams params, String path, final XDownLoadCallBack callBack) {

        params.setAutoResume(true);// 断点续传
        params.setSaveFilePath(path);
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(final File result) {
                x.task().post(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) {
                            callBack.onResponse(result);
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                x.task().post(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) {
                            callBack.onFinished();
                        }
                    }
                });
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(final long total, final long current, final boolean isDownloading) {
                x.task().post(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) {
                            callBack.onLoading(total, current, isDownloading);
                        }
                    }
                });
            }
        });

    }

    /**
     * 异步get请求返回结果,json字符串
     *
     * @param result
     * @param callBack
     */
    private static void onSuccessResponse(final String result, final XCallBack callBack) {
        //运行在UI线程，同时还有个x.task().run() 则是异步任务
        x.task().post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onSuccess(result);
                }
            }
        });
    }

    private static void onFinishedResponse(final XCallBack callBack) {
        //运行在UI线程，同时还有个x.task().run() 则是异步任务
        x.task().post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onFinished();
                }
            }
        });
    }


    //区别返回的是缓存还是新数据
    public interface XCacheCallBack {
        void onSuccess(String result, boolean isCache);//true是缓存数据，false是服务器数据

        void onError();

        void onFinished();

    }

    public interface XCallBack {
        void onSuccess(String result);

        void onError();

        void onFinished();

    }


    public interface XDownLoadCallBack {
        void onResponse(File result);

        void onLoading(long total, long current, boolean isDownloading);

        void onFinished();
    }


}

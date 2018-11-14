package com.hjq.demo;

public class Constant {
    //服务端域名
    public final static String DOMAIN = "http://www.iafeng.cc/";

    //第三方KEY
    public final static String QQAPP_ID = "1105979137";
    public final static String QQAPP_SECRET = "jODSgRir417714vY";
    public final static String WXAPP_ID = "wxbec65736501d0564";
    public final static String WXAPP_SECRET = "82bc1e49bbb56cdded05cc8875d02ea79";


    //广播接受器Action监听类型
    public enum ActionType {
        ON_DOWNLOAD, //下载中返回进度
        DOWNLOAD_ERROR, //下载错误
    }

}
package com.hjq.demo.bean;

import java.io.Serializable;

public class Appversion implements Serializable {
    private String id;
    // 假版本
    private int jiaVersion;
    // 版本
    private int version;
    // 修改描述
    private String upDes;
    // 强制更新
    private String mustup;
    // 下载路径
    private String downUrl;
    // 文件完整性验证码
    private String apkMd5;
    //APK文件名
    private String apkName;

    public String getApkMd5() {
        return apkMd5;
    }

    public void setApkMd5(String apkMd5) {
        this.apkMd5 = apkMd5;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public int getJiaVersion() {
        return jiaVersion;
    }

    public void setJiaVersion(int jiaVersion) {
        this.jiaVersion = jiaVersion;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUpDes() {
        return upDes;
    }

    public void setUpDes(String upDes) {
        this.upDes = upDes;
    }

    public String getMustup() {
        return mustup;
    }

    public void setMustup(String mustup) {
        this.mustup = mustup;
    }

    public String getDownUrl() {
        return downUrl;
    }

    public void setDownUrl(String downUrl) {
        this.downUrl = downUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

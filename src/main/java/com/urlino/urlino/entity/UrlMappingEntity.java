package com.urlino.urlino.entity;

import java.util.Date;


public class UrlMappingEntity {
    // 行键：全局唯一的短链 (例如 "abc12345")
    private String shortUrl;
    // 对应的长链接
    private String longUrl;
    // 生成此短链的用户ID
    private String userId;
    // 创建时间
    private Date createTime;
    // 点击次数
    private int clickCount;

    public UrlMappingEntity() {
    }

    public UrlMappingEntity(String shortUrl, String longUrl, String userId) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.userId = userId;
        this.createTime = new Date();
        this.clickCount = 0;
    }

    // Getters and Setters

    public String getShortUrl() {
        return shortUrl;
    }
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
    public String getLongUrl() {
        return longUrl;
    }
    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public int getClickCount() {
        return clickCount;
    }
    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }
}
//public class UrlMappingEntity {
//
//    // 用户ID和短链组合构成唯一标识，例如 "user123#abc123"
//    private String compositeKey;
//
//    // 可选字段：单独存储 userId 和 shortUrl，便于业务逻辑处理
//    private String userId;
//    private String shortUrl;
//
//    // 长链接地址
//    private String longUrl;
//
//    // 创建时间
//    private Date createTime;
//
//    // 点击次数
//    private int clickCount;
//
//    // 无参构造器
//    public UrlMappingEntity() {
//    }
//
//    // 有参构造器
//    public UrlMappingEntity(String userId, String shortUrl, String longUrl, Date createTime, int clickCount) {
//        this.userId = userId;
//        this.shortUrl = shortUrl;
//        // 组合行键，格式为 "userId#shortUrl"
//        this.compositeKey = userId + "#" + shortUrl;
//        this.longUrl = longUrl;
//        this.createTime = createTime;
//        this.clickCount = clickCount;
//    }
//
//    // Getter 和 Setter 方法
//    public String getCompositeKey() {
//        return compositeKey;
//    }
//    public void setCompositeKey(String compositeKey) {
//        this.compositeKey = compositeKey;
//    }
//    public String getUserId() {
//        return userId;
//    }
//    public void setUserId(String userId) {
//        this.userId = userId;
//        // 当更新 userId 时，需重新生成 compositeKey
//        if (this.shortUrl != null) {
//            this.compositeKey = userId + "#" + this.shortUrl;
//        }
//    }
//    public String getShortUrl() {
//        return shortUrl;
//    }
////    public void setShortUrl(String shortUrl) {
////        this.shortUrl = shortUrl;
////        if (this.userId != null) {
////            this.compositeKey = this.userId + "#" + shortUrl;
////        }
////    }
//    public String getLongUrl() {
//        return longUrl;
//    }
////    public void setLongUrl(String longUrl) {
////        this.longUrl = longUrl;
////    }
//    public Date getCreateTime() {
//        return createTime;
//    }
//    public void setCreateTime(Date createTime) {
//        this.createTime = createTime;
//    }
//    public int getClickCount() {
//        return clickCount;
//    }
//    public void setClickCount(int clickCount) {
//        this.clickCount = clickCount;
//    }
//
//    @Override
//    public String toString() {
//        return "UrlMappingEntity{" +
//                "compositeKey='" + compositeKey + '\'' +
//                ", userId='" + userId + '\'' +
//                ", shortUrl='" + shortUrl + '\'' +
//                ", longUrl='" + longUrl + '\'' +
//                ", createTime=" + createTime +
//                ", clickCount=" + clickCount +
//                '}';
//    }
//}
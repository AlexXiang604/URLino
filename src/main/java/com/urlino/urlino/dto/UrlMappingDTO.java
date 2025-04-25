package com.urlino.urlino.dto;

import java.util.Date;

public class UrlMappingDTO {
    private String shortUrl;
    private String longUrl;
    private String alias;  // 可选的自定义短链
    private Date createTime;
    private String createTimeFormatted; // 新增字段，只含年月日
    private int clickCount;
    private int daysUntilExpiry;  // 到期剩余天数
    // 新增 expireAt 字段
    private Date expireAt;

    // 需要新增的字段：
    private String userId;


    // 对应的 Getter/Setter
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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

    public int getDaysUntilExpiry() {
        return daysUntilExpiry;
    }

    public void setDaysUntilExpiry(int daysUntilExpiry) {
        this.daysUntilExpiry = daysUntilExpiry;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public String getCreateTimeFormatted() {
        return createTimeFormatted;
    }

    public void setCreateTimeFormatted(String createTimeFormatted) {
        this.createTimeFormatted = createTimeFormatted;
    }
}

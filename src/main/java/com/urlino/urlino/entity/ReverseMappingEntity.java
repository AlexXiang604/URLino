package com.urlino.urlino.entity;

public class ReverseMappingEntity {
    // 行键格式："userId#hash(longUrl)"，例如 "user123#1a2b3c4d"
    private String indexKey;
    // 存储对应的短链
    private String shortUrl;

    public ReverseMappingEntity() {
    }

    public ReverseMappingEntity(String indexKey, String shortUrl) {
        this.indexKey = indexKey;
        this.shortUrl = shortUrl;
    }

    // Getters and Setters
    public String getIndexKey() {
        return indexKey;
    }
    public void setIndexKey(String indexKey) {
        this.indexKey = indexKey;
    }
    public String getShortUrl() {
        return shortUrl;
    }
    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}

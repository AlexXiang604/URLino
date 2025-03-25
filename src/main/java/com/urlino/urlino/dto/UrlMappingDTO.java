package com.urlino.urlino.dto;

public class UrlMappingDTO {
    private String longUrl;  // 长链接
    private String alias;    // 可选别名

    // 无参构造函数
    public UrlMappingDTO() {
    }

    // 全参构造函数（可选）
    public UrlMappingDTO(String longUrl, String alias) {
        this.longUrl = longUrl;
        this.alias = alias;
    }

    // Getters and Setters
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
}

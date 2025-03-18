package com.urlino.urlino.model;

public class UrlMapping {
    private String id;      // Unique short code
    private String longUrl; // The original long URL

    public UrlMapping() {}

    public UrlMapping(String id, String longUrl) {
        this.id = id;
        this.longUrl = longUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
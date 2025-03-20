package com.urlino.urlino.service;

import com.urlino.urlino.model.UrlMapping;
import com.urlino.urlino.repository.UrlMappingRepository;
import com.google.cloud.bigtable.data.v2.models.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.security.SecureRandom;

@Service
public class UrlShortenerService {

    @Autowired
    private UrlMappingRepository Repository;

    // Characters allowed in the short URL.
    private static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    // Generate a random alphanumeric string
    private String generateShortUrl() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(ALPHANUM.length());
            sb.append(ALPHANUM.charAt(index));
        }
        return sb.toString();
    }

    public String shortenUrl(String longUrl) {

        String shortUrl = generateShortUrl();
        // generate shortUrl until we find a shortUrl that hasn't been generated before
        while (Repository.findLongUrlById(shortUrl) != null) {
            shortUrl = generateShortUrl();
        }
        UrlMapping mapping = new UrlMapping(shortUrl, longUrl);
        Repository.saveMapping(mapping);
        return shortUrl;
    }


}

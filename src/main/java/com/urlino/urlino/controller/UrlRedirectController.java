package com.urlino.urlino.controller;

import com.urlino.urlino.service.UrlMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@CrossOrigin(origins = "https://urlino-frontend-dot-rice-comp-539-spring-2022.uk.r.appspot.com/")
public class UrlRedirectController {

    @Autowired
    private UrlMappingService urlMappingService;

    @GetMapping("/{short_url}")
    public ResponseEntity<?> redirect(@PathVariable String short_url) throws URISyntaxException {
        try {
            String longUrl = urlMappingService.retrieveLongUrl(short_url);
            URI origin = new URI(longUrl);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(origin);
            return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }

    }
}

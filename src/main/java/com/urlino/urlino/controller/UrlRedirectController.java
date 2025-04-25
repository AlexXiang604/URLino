package com.urlino.urlino.controller;

import com.urlino.urlino.repository.UrlMappingRepository;
import com.urlino.urlino.service.UrlMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@CrossOrigin(origins = "https://urlino-frontend-dot-rice-comp-539-spring-2022.uk.r.appspot.com/")
//@CrossOrigin(origins = "http://localhost:8000/")

public class UrlRedirectController {

    @Autowired
    private UrlMappingService urlMappingService;

    @Autowired
    private UrlMappingRepository urlMappingRepository;


    @GetMapping("/{short_url}")
    public ResponseEntity<?> redirect(@PathVariable String short_url) throws URISyntaxException {
        try {
            String longUrl = urlMappingService.retrieveLongUrl(short_url);
            
            if (longUrl == null) {
                // 如果URL不存在，返回404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("URL not found");
            }
            
            if (longUrl.equals("URL_EXPIRED")) {
                // 如果URL已过期，显示过期提示页面
                String homepageUrl = "https://urlino-frontend-dot-rice-comp-539-spring-2022.uk.r.appspot.com/login.html";
                String htmlContent = "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "  <meta charset='UTF-8'>" +
                        "  <title>Redirecting...</title>" +
                        "</head>" +
                        "<body>" +
                        "  <script type='text/javascript'>" +
                        "    alert('The short url is expired. You are being redirected to the homepage.');" +
                        "    window.location.href = '" + homepageUrl + "';" +
                        "  </script>" +
                        "  <noscript>" +
                        "    <p>Please enable JavaScript or click <a href='" + homepageUrl + "'>here</a> to continue.</p>" +
                        "  </noscript>" +
                        "</body>" +
                        "</html>";

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_HTML);
                return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
            } else {
                // 如果URL有效，进行重定向并增加click count
                urlMappingRepository.incrementClick(short_url);
                URI origin = new URI(longUrl);
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setLocation(origin);
                return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
//                return new ResponseEntity<>(httpHeaders, HttpStatus.OK);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}

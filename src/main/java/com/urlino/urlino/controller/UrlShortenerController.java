package com.urlino.urlino.controller;

import com.urlino.urlino.model.UrlMapping;
import com.urlino.urlino.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/urlino")
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService Service;

    @PostMapping
    public UrlMapping shortenUrl(@RequestBody String url){
        return Service.shortenUrl(url);
    }

    @GetMapping("/{shorturl}")
    public String getOriginlUrl(@PathVariable String shorturl) {
        return Service.getLongUrl(shorturl);
    }
//    @RequestMapping(value = "/{url}", method = RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity shortenUrl(@PathVariable String url) {
//        String shortUrlEntry = Service.shortenUrl(url);
//        return ResponseEntity.ok(shortUrlEntry);
//    }

//    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
//    @ResponseBody
//    public ResponseEntity getUrl(@PathVariable String key) {
//        String url = Service.getLongUrl(key);
//        return ResponseEntity.ok(url);
//    }
}
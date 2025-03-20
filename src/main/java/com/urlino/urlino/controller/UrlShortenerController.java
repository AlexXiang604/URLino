package com.urlino.urlino.controller;

import com.urlino.urlino.model.UrlMapping;
import com.urlino.urlino.service.UrlShortenerService;
import com.urlino.urlino.repository.UrlMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/urlino")
public class UrlShortenerController {

    @Autowired
    private UrlShortenerService Service;
    private UrlMappingRepository Repository;

    @RequestMapping(value = "/{url}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity shortenUrl(@PathVariable String url) {
        String shortUrlEntry = Service.shortenUrl(url);
        return ResponseEntity.ok(url);
    }

    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getUrl(@PathVariable String key) {
        String url = Repository.findLongUrlById(key);
        return ResponseEntity.ok(url);
    }
}
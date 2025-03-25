package com.urlino.urlino.controller;

import com.urlino.urlino.dto.UrlMappingDTO;
import com.urlino.urlino.entity.UrlMappingEntity;
import com.urlino.urlino.service.UrlMappingService;
import com.urlino.urlino.service.UserService;
import com.urlino.urlino.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.util.Optional;


@RequestMapping("/service")
@CrossOrigin(origins = "http://localhost:8000")
@RestController
public class UrlMappingController {

    @Autowired
    private UrlMappingService urlMappingService;

    @Autowired
    private UserService userService;  // 用于获取用户类型（免费/付费）

    @Autowired
    private JwtUtil jwtUtil;



    // 调试接口：短链接retrieve长链接
    @GetMapping("/retrieve/{shortUrl}")
    public ResponseEntity<?> retrieveLongUrl(@PathVariable String shortUrl) {
        try {
            String longUrl = urlMappingService.retrieveLongUrl(shortUrl);
            if (longUrl != null) {
                return ResponseEntity.ok(longUrl);
            } else {
                return ResponseEntity.status(404).body("Mapping not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


//    // Widget 1: 生成短链接口
//    // 参数：userId, longUrl, 可选 alias
//    @PostMapping("/shorten")
//    public ResponseEntity<?> shortenUrl(
//            @RequestHeader("userId") String userId,  // 从请求头获取 userId
//            @RequestBody UrlMappingDTO request  // 从请求体获取 longUrl 和 alias
//    ) {
//        try {
//            String shortUrl = urlMappingService.createMapping(
//                    userId,
//                    request.getLongUrl(),
//                    Optional.ofNullable(request.getAlias())
//            );
//            // 拼接完整短链
//            String fullShortUrl = "https://urlino.com/" + shortUrl;
//            return ResponseEntity.ok(fullShortUrl);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }

//    // Widget 1: 生成短链接口
//    // 参数：userId, longUrl, 可选 alias
//    @PostMapping("/shorten")
//    public ResponseEntity<?> shortenUrl(
//            Principal principal,  // 从 Spring Security 中获取当前用户
//            @RequestBody UrlMappingDTO request  // 从请求体获取 longUrl 和 alias
//    ) {
//        try {
//            String userId = principal.getName();  // 获取当前用户的 userId
//            String shortUrl = urlMappingService.createMapping(
//                    userId,
//                    request.getLongUrl(),
//                    Optional.ofNullable(request.getAlias())
//            );
//            // 拼接完整短链
//            String fullShortUrl = "https://urlino.com/" + shortUrl;
//            return ResponseEntity.ok(fullShortUrl);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UrlMappingDTO request
    ) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtUtil.extractUserId(token);

            String shortUrl = urlMappingService.createMapping(
                    userId,
                    request.getLongUrl(),
                    Optional.ofNullable(request.getAlias())
            );
            return ResponseEntity.ok("https://urlino.com/" + shortUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }


    /**
     * 编辑短链（仅限付费用户）
     */
    @PutMapping
    public ResponseEntity<?> editShortUrl(
            @RequestHeader("userId") String userId,
            @RequestBody UrlMappingDTO request
    ) {
        try {
            // 检查用户是否为付费用户
            if (!userService.isPremiumUser(userId)) {
                return ResponseEntity.status(403).body("Become a premium user to edit short URLs.");
            }

            String shortUrl = urlMappingService.editShortUrl(
                    userId,
                    request.getLongUrl(),
                    Optional.ofNullable(request.getAlias())
            );
            return ResponseEntity.ok(shortUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

//    @PostMapping("/shorten")
//    public ResponseEntity<?> shortenUrl(@RequestBody UrlMappingEntity request) {
//        try {
//            UrlMappingEntity mapping = service.createMapping(request.getLongUrl());
//            return ResponseEntity.ok(mapping);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }




//    // Widget 2: 查询长链接口
//    // 参数：userId, shortUrl
//    @GetMapping("/retrieve")
//    public ResponseEntity<?> retrieveLongUrl(@RequestParam String userId,
//                                             @RequestParam String shortUrl) {
//        try {
//            String longUrl = urlMappingService.retrieveLongUrl(userId, shortUrl);
//            if (longUrl != null) {
//                return ResponseEntity.ok(longUrl);
//            } else {
//                return ResponseEntity.status(404).body("Mapping not found");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error: " + e.getMessage());
//        }
//    }

}

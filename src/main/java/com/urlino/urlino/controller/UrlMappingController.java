package com.urlino.urlino.controller;

import com.urlino.urlino.dto.UrlMappingDTO;
import com.urlino.urlino.entity.UrlMappingEntity;
import com.urlino.urlino.service.UrlMappingService;
import com.urlino.urlino.service.UserService;
import com.urlino.urlino.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.*;


@RequestMapping("/service")
//@CrossOrigin(origins = "http://localhost:8000")
@CrossOrigin(
        origins = "https://urlino-frontend-dot-rice-comp-539-spring-2022.uk.r.appspot.com/",
        //origins = "http://localhost:8000/",
        allowedHeaders = {"Authorization", "Content-Type"},
        methods = {RequestMethod.GET,RequestMethod.POST, RequestMethod.OPTIONS}
)
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
            if (longUrl == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Short URL not found");
            }
            if (longUrl.equals("URL_EXPIRED")) {
                return ResponseEntity.status(HttpStatus.GONE)
                        .body("This URL has expired");
            }
            return ResponseEntity.ok(longUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving URL: " + e.getMessage());
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
            if (!userService.isPremiumUser(userId)) {
                String shortUrl = urlMappingService.createMapping(
                        userId,
                        request.getLongUrl(),
                        Optional.ofNullable(request.getAlias())
                );
                //return ResponseEntity.ok("http://localhost:8080/" + shortUrl);
                return ResponseEntity.ok("https://urlino-backend-dot-rice-comp-539-spring-2022.uk.r.appspot.com/" + shortUrl);

            }
            else{
                String shortUrl = urlMappingService.editShortUrl(
                        userId,
                        request.getLongUrl(),
                        Optional.ofNullable(request.getAlias())
                );
                //return ResponseEntity.ok("http://localhost:8080/" + shortUrl);
                return ResponseEntity.ok("https://urlino-backend-dot-rice-comp-539-spring-2022.uk.r.appspot.com/" + shortUrl);

            }

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


    @GetMapping("/mappings")
    public ResponseEntity<?> getUserMappings(
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            // 从请求头中提取 token，并解析出 userId
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtUtil.extractUserId(token);

            // 通过业务层接口获取当前用户的所有 URL mapping 记录
            List<UrlMappingDTO> mappings = urlMappingService.getUserMappings(userId);

            // 将 UrlMappingEntity 转换为 List<Map<String, Object>> 格式，便于前端解析
            List<Map<String, Object>> mappingList = new ArrayList<>();
            for (UrlMappingDTO mapping : mappings) {
                Map<String, Object> mappingMap = new HashMap<>();
                mappingMap.put("shortUrl", mapping.getShortUrl());
                mappingMap.put("longUrl", mapping.getLongUrl());
//                mappingMap.put("userId", mapping.getUserId());
//                mappingMap.put("createTime", mapping.getCreateTime());
                mappingMap.put("createTime", mapping.getCreateTimeFormatted());
                mappingMap.put("clickCount", mapping.getClickCount());
//                mappingMap.put("expireAt", mapping.getExpireAt());
                mappingMap.put("expireAt", mapping.getDaysUntilExpiry());
                mappingList.add(mappingMap);
            }

            // 构造返回的响应 Map，格式上和 login 接口类似
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Mappings retrieved successfully");
            response.put("mappings", mappingList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 出现异常时，构造错误返回信息
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to retrieve mappings");
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


}

package com.urlino.urlino.controller;

import com.urlino.urlino.dto.UserLoginDTO;
import com.urlino.urlino.dto.UserRegistrationDTO;
import com.urlino.urlino.entity.UserEntity;
import com.urlino.urlino.service.UserService;
import com.urlino.urlino.util.JwtUtil;
import com.urlino.urlino.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "https://urlino-frontend-dot-rice-comp-539-spring-2022.uk.r.appspot.com/")
//@CrossOrigin(origins = "http://localhost:8000/")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;


    /**
     * 用户注册
     *
     * @param registerRequest 用户注册请求（包含 email、username、password）
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDTO registerRequest) {
        try {
            if (userService.emailExists(registerRequest.getEmail())) {
                throw new IllegalArgumentException("Email is already registered");
            }
            // 对密码进行加密
            String passwordHash = PasswordUtil.hashPassword(registerRequest.getPassword());

            // 创建用户实体
            UserEntity user = new UserEntity();
            user.setUserId(generateUserId());  // 生成用户 ID
            user.setEmail(registerRequest.getEmail());
            user.setUsername(registerRequest.getUsername());
            user.setPasswordHash(passwordHash);
            user.setPremium(false);  // 默认是非付费用户
            user.setCreatedAt(System.currentTimeMillis());
            user.setUpdatedAt(System.currentTimeMillis());

            // 保存用户信息
            userService.saveUser(user);

            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * 生成用户 ID（示例逻辑）
     */
    private String generateUserId() {
        String userId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return userId;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginRequest) {
        // 通过邮箱和密码查找用户
        UserEntity user = userService.findByEmailAndPassword(loginRequest.getEmail(), loginRequest.getPassword());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
        // 2. 生成 JWT token
        String token = jwtUtil.generateToken(user.getUserId());

        // 3. 返回 token 给前端
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Login successful");
//        response.put("token", token);

        // 返回 token 以及用户基本信息
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("premium", user.isPremium()); // premium 状态



        // 登录成功
        return ResponseEntity.ok(response);
    }

    /**
     * Profile 接口：通过解析请求头中的 token 获取用户最新信息，
     * 用于页面加载时刷新用户状态，确保 premium 状态等信息时刻最新
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            // 从请求头中提取 token，注意 Bearer 前缀
            String token = authHeader.replace("Bearer ", "");
            // 解析 token 获取 userId
            String userId = jwtUtil.extractUserId(token);

            // 根据 userId 查询最新用户信息
            UserEntity user = userService.findById(userId);

            // 返回需要的用户数据
            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getUserId());
            result.put("username", user.getUsername());
            result.put("email", user.getEmail());
            result.put("premium", user.isPremium());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Invalid token"));
        }
    }


    /**
     * 升级用户到 Premium
     * 此接口通过解析请求中的 token 来获取用户身份，
     * 并更新数据库中用户的 isPremium 字段为 true。
     */
    @PutMapping("/upgrade")
    public ResponseEntity<?> upgradeUserToPremium(@RequestHeader("Authorization") String authHeader,
                                                  @RequestBody Map<String, Object> payload) {
        try {
            // 提取 token（去掉 "Bearer " 前缀）
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtUtil.extractUserId(token);

            // 可以对 payload 中的数据进行校验，比如判断 paymentSuccessful 是否为 true
            Boolean paymentSuccessful = (Boolean) payload.get("paymentSuccessful");
            if (paymentSuccessful == null || !paymentSuccessful) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Payment not verified"));
            }

            // 更新数据库里 isPremium 字段，假定 userService 提供相应方法
            boolean updated = userService.upgradeToPremium(userId);
            if (updated) {
                return ResponseEntity.ok(Collections.singletonMap("message", "User upgraded to premium."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("message", "Upgrade failed, please try again later."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Invalid token or error occurred."));
        }
    }
}

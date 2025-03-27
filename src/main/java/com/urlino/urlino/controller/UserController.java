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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "https://urlino-frontend-dot-rice-comp-539-spring-2022.uk.r.appspot.com/")
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
        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("token", token);

        // 登录成功
        return ResponseEntity.ok(response);
    }
}

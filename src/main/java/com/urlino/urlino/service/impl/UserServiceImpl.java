package com.urlino.urlino.service.impl;

import com.urlino.urlino.entity.UserEntity;
import com.urlino.urlino.repository.UserRepository;
import com.urlino.urlino.service.UserService;
import com.urlino.urlino.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService { //实现接口
    @Autowired
    private UserRepository userRepository;  // 注入 UserRepository

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }

    @Override
    public boolean isPremiumUser(String userId) {
        UserEntity user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        return user.isPremium();
    }

    @Override
    public UserEntity findById(String userId) {
        return userRepository.findById(userId); // 调用 UserRepository 的 findById 方法
    }

//    @Override
//    public UserEntity findByEmailAndPassword(String email, String passwordHash) {
//        return userRepository.findByEmailAndPassword(email, passwordHash);
//    }

    @Override
    public UserEntity findByEmailAndPassword(String email, String rawPassword) {
//        UserEntity user = userRepository.findByEmail(email);
//        if (user == null) return null;
//
//        // 校验密码
//        String hash = PasswordUtil.hashPassword(rawPassword);
//        if (!hash.equals(user.getPasswordHash())) {
//            return null;
//        }
//        return user;
        UserEntity user = userRepository.findByEmail(email);
        if (user == null) return null;

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            return null;
        }

        return user;
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }



}

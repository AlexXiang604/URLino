package com.urlino.urlino.service.impl;


import com.urlino.urlino.entity.UserEntity;
import com.urlino.urlino.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // 通过用户名查找用户
//        UserEntity user = userRepository.findByUsername(username);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found with username: " + username);
//        }
//
//        // 将 UserEntity 转换为 Spring Security 的 UserDetails
//        return new User(user.getUsername(), user.getPasswordHash(), new ArrayList<>());
//    }

    // JWT token 存的是 userId，不是 email！
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        UserEntity user = userRepository.findById(userId); // 👈 你得写这个方法
        if (user == null) {
            throw new UsernameNotFoundException("User not found with userId: " + userId);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),
                user.getPasswordHash(),
                new ArrayList<>()
        );
    }
}

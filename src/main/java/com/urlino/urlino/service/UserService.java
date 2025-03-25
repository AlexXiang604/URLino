package com.urlino.urlino.service;


import com.urlino.urlino.entity.UserEntity;

public interface UserService {
    /**
     * 保存用户信息
     *
     * @param user 用户实体
     */
    void saveUser(UserEntity user);


    /**
     * 检查用户是否为付费用户
     *
     * @param userId 用户 ID
     * @return 如果是付费用户，返回 true；否则返回 false
     */
    boolean isPremiumUser(String userId);

    UserEntity findByEmailAndPassword(String email, String passwordHash); // 新增方法
    UserEntity findById(String userId); // 新增方法
    boolean emailExists(String email);
}



package com.urlino.urlino.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;


public class UserEntity {
    private String userId;  // 用户 ID
    private String email;   // 邮箱
    private String username; // 用户名
    private String passwordHash; // 密码哈希值
    private boolean isPremium; // 是否为付费用户
    private long createdAt; // 创建时间
    private long updatedAt; // 更新时间

    // 无参构造函数
    public UserEntity() {
    }

    // 全参构造函数
    public UserEntity(String userId, String email, String username, String passwordHash, boolean isPremium, long createdAt, long updatedAt) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isPremium = isPremium;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}

////使用mongodb
//@Data  // 自动生成 getter、setter、toString 等方法
//@Document(collection = "users")  // 指定 MongoDB 集合名称
//public class UserEntity {
//    @Id
//    private String userId;  // 用户 ID
//    private String email;   // 邮箱
//    private String username; // 用户名
//    private String password; // 密码
//    private boolean isPremium; // 是否为付费用户
//
//    // 无参构造函数（MongoDB 需要）
//    public UserEntity() {
//    }
//
//    // 全参构造函数
//    public UserEntity(String userId, String email, String username, String password, boolean isPremium) {
//        this.userId = userId;
//        this.email = email;
//        this.username = username;
//        this.password = password;
//        this.isPremium = isPremium;
//    }
//}

//=======================================
//使用mysql的情况
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "users")
//public class UserEntity {
//    @Id
//    private String userId;  // 用户 ID
//    private String email;   // 邮箱
//    private String username; // 用户名
//    private String password; // 密码
//    private boolean isPremium; // 是否为付费用户
//
//    // 无参构造函数（JPA 需要）
//    public UserEntity() {
//    }
//
//    // 全参构造函数
//    public UserEntity(String userId, String email, String username, String password, boolean isPremium) {
//        this.userId = userId;
//        this.email = email;
//        this.username = username;
//        this.password = password;
//        this.isPremium = isPremium;
//    }
//
//    // Getters and Setters
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public boolean isPremium() {
//        return isPremium;
//    }
//
//    public void setPremium(boolean premium) {
//        isPremium = premium;
//    }
//}
//==============================
//public class UserEntity {
//
//    // 用户唯一标识，对应 Bigtable 的行键
//    private String userId;
//
//    // 用户名
//    private String username;
//
//    // 是否会员
//    private boolean isMember;
//
//    // 注册日期（可使用 Date 或 String，根据业务需求）
//    private String registrationDate;
//
//    // 无参构造器
//    public UserEntity() {
//    }
//
//    // 有参构造器
//    public UserEntity(String userId, String username, boolean isMember, String registrationDate) {
//        this.userId = userId;
//        this.username = username;
//        this.isMember = isMember;
//        this.registrationDate = registrationDate;
//    }
//
//    // Getter 和 Setter 方法
//    public String getUserId() {
//        return userId;
//    }
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//    public String getUsername() {
//        return username;
//    }
//    public void setUsername(String username) {
//        this.username = username;
//    }
//    public boolean isMember() {
//        return isMember;
//    }
//    public void setMember(boolean member) {
//        isMember = member;
//    }
//    public String getRegistrationDate() {
//        return registrationDate;
//    }
//    public void setRegistrationDate(String registrationDate) {
//        this.registrationDate = registrationDate;
//    }
//
//    @Override
//    public String toString() {
//        return "UserInfoEntity{" +
//                "userId='" + userId + '\'' +
//                ", username='" + username + '\'' +
//                ", isMember=" + isMember +
//                ", registrationDate='" + registrationDate + '\'' +
//                '}';
//    }
//}

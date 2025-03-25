package com.urlino.urlino.dto;

public class UserLoginDTO {
    private String email;     // 邮箱
    private String password;  // 密码

    // Getter 和 Setter 方法
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

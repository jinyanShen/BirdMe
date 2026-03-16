package com.java.birdme.bean;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


/**
 * @description User
 * @author admin
 * @date 2025-02-25
 */
@TableName("user")
public class User {
    /**
     * User unique identifier id
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * Username (unique)
     */
    private String username;
    /**
     * User password (encrypted storage)
     */
    private String password;
    /**
     * User role 0-regular user, 1-admin
     */
    private Integer role;
    /**
     * User's real name
     */
    private String name;
    /**
     * User age
     */
    private Integer age;
    /**
     * User phone number (unique)
     */
    private String phone;
    /**
     * User avatar url
     */
    private String avatarUrl;
    /**
     * Creation time
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createdAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}

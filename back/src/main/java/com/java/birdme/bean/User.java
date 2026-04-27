package com.java.birdme.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


/**
 * @description User
 * @author admin
 * @date 2025-02-25
 */
@TableName("user")
public class User {
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    /**
     * User role 0-regular user, 1-admin, 2-rescue station
     */
    private Integer role;

    private String name;

    private Integer age;

    private String phone;

    private String avatarUrl;

    /**
     * Associated rescue station ID (for rescue station role)
     */
    private Integer rescueStationId;

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

    public Integer getRescueStationId() {
        return rescueStationId;
    }

    public void setRescueStationId(Integer rescueStationId) {
        this.rescueStationId = rescueStationId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}

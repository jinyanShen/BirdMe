package com.java.birdme.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
/**
 * @description Address
 * @author admin
 * @date 2026-03-14
 */
@TableName("address")
public class Address {
    /**
     * Address unique ID
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * User ID
     */
    private Integer userId;
    /**
     * Recipient name
     */
    private String recipientName;
    /**
     * Recipient phone
     */
    private String recipientPhone;
    /**
     * Province
     */
    private String province;
    /**
     * City
     */
    private String city;
    /**
     * District
     */
    private String district;
    /**
     * Detailed address
     */
    private String detailAddress;
    /**
     * Is default address 0-no, 1-yes
     */
    private Integer isDefault;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

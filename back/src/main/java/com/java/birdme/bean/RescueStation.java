package com.java.birdme.bean;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Rescue station entity
 * Contains information about bird rescue stations
 */
@TableName("rescue_station")
public class RescueStation {
    @TableId(value="id", type= IdType.AUTO)
    /**
     * Rescue station unique identifier
     */
    private Integer id;
    /**
     * Rescue station name
     */
    private String name;
    /**
     * Rescue station address
     */
    private String address;
    /**
     * Latitude
     */
    private Double latitude;
    /**
     * Longitude
     */
    private Double longitude;
    /**
     * Contact phone
     */
    private String phone;
    /**
     * Email address
     */
    private String email;
    /**
     * Opening hours
     */
    private String openingHours;
    /**
     * Description
     */
    private String description;
    /**
     * Contact person
     */
    private String contactPerson;

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
}

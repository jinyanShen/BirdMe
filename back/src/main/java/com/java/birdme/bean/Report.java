package com.java.birdme.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Bird rescue report entity
 * Contains information about bird rescue reports submitted by users
 */
@TableName("report")
public class Report {

    /**
     * Report unique identifier
     */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * Bird name
     */
    private String birdName;
    /**
     * Bird species
     */
    private String species;
    /**
     * Latitude
     */
    private Double latitude;
    /**
     * Longitude
     */
    private Double longitude;
    /**
     * Location
     */
    private String location;
    /**
     * Injury type
     */
    private String injuryType;
    /**
     * Injury description
     */
    private String injuryDescription;
    /**
     * Image URL
     */
    private String imageUrl;
    /**
     * Status (PENDING, PROCESSING, COMPLETED, CANCELLED)
     */
    private String status;
    /**
     * Creation time
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createdAt;
    /**
     * Update time
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updatedAt;
    /**
     * Submitter ID
     */
    private String submitterId;
    /**
     * Rescue station ID
     */
    private Integer rescueStationId;
    /**
     * Notes
     */
    private String notes;

    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBirdName() {
        return birdName;
    }

    public void setBirdName(String birdName) {
        this.birdName = birdName;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInjuryType() {
        return injuryType;
    }

    public void setInjuryType(String injuryType) {
        this.injuryType = injuryType;
    }

    public String getInjuryDescription() {
        return injuryDescription;
    }

    public void setInjuryDescription(String injuryDescription) {
        this.injuryDescription = injuryDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(String submitterId) {
        this.submitterId = submitterId;
    }

    public Integer getRescueStationId() {
        return rescueStationId;
    }

    public void setRescueStationId(Integer rescueStationId) {
        this.rescueStationId = rescueStationId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

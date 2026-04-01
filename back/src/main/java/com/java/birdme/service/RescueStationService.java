package com.java.birdme.service;

import com.java.birdme.bean.RescueStation;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;

import java.util.List;

/**
 * Rescue station service
 * Provides business logic for rescue stations
 */
public interface RescueStationService {
    /**
     * Add new
     */
    ReturnResp insert(RescueStation rescueStation);

    /**
     * Update
     */
    ReturnResp update(RescueStation rescueStation);

    /**
     * Query by primary key id
     */
    ReturnResp getOne(Integer id);

    /**
     * Query with pagination
     */
    PageResp page(PageReq<RescueStation> pageReq);

    /**
     * Get all rescue stations
     */
    ReturnResp all();

    /**
     * Delete
     */
    ReturnResp delete(List<Integer> ids);

    /**
     * Get nearby rescue stations
     * @param latitude User's latitude
     * @param longitude User's longitude
     * @param radius Search radius in kilometers
     */
    List<RescueStation> getNearbyStations(double latitude, double longitude, double radius);
}
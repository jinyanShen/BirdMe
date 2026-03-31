package com.java.birdme.controller;

import com.java.birdme.bean.RescueStation;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.service.RescueStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Rescue station controller
 * Handles API requests related to rescue stations
 */
@RestController
@RequestMapping("/rescue-station")
@CrossOrigin
public class RescueStationController {

    @Autowired
    private RescueStationService rescueStationService;

    /**
     * Add new
     **/
    @RequestMapping("/insert")
    public ReturnResp insert(@RequestBody RescueStation rescueStation) {
        return rescueStationService.insert(rescueStation);
    }

    /**
     * Batch delete
     **/
    @RequestMapping("/delete")
    public ReturnResp delete(@RequestBody Integer[] ids) {
        return rescueStationService.delete(Arrays.asList(ids));
    }

    /**
     * Update
     **/
    @RequestMapping("/update")
    public ReturnResp update(@RequestBody RescueStation rescueStation) {
        return rescueStationService.update(rescueStation);
    }

    /**
     * Query by primary key id
     **/
    @RequestMapping("/getOne")
    public ReturnResp getOne(@RequestParam("id") Integer id) {
        return rescueStationService.getOne(id);
    }

    /**
     * Query with pagination
     **/
    @RequestMapping("/page")
    public PageResp page(@RequestBody PageReq<RescueStation> pageReq) {
        return rescueStationService.page(pageReq);
    }

    /**
     * Get all rescue stations
     **/
    @RequestMapping("/all")
    public ReturnResp all() {
        return rescueStationService.all();
    }

    /**
     * Get nearby rescue stations
     * @param latitude User's latitude
     * @param longitude User's longitude
     * @param radius Search radius in kilometers (default: 50)
     */
    @RequestMapping("/nearby")
    public ReturnResp getNearbyStations(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "50") double radius) {
        try {
            List<RescueStation> stations = rescueStationService.getNearbyStations(latitude, longitude, radius);
            return ReturnResp.success(stations);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get nearby rescue stations");
        }
    }
}

package com.java.birdme.controller;

import com.java.birdme.bean.Report;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.service.InjuryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Injury report controller for heatmap and statistics
 */
@RestController
@RequestMapping("/api/injury")
public class InjuryController {

    @Autowired
    private InjuryService injuryService;

    /**
     * Get heatmap data
     */
    @GetMapping("/heatmap")
    public ReturnResp getHeatmapData(
            @RequestParam(required = false, defaultValue = "30") String timeRange,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String injuryType) {

        Map<String, Object> result = injuryService.getHeatmapData(timeRange, species, injuryType);
        return ReturnResp.success(result);
    }

    /**
     * Get recent reports
     */
    @GetMapping("/recent")
    public ReturnResp getRecentReports(@RequestParam(defaultValue = "10") int limit) {
        List<Report> reports = injuryService.getRecentReports(limit);
        return ReturnResp.success(reports);
    }

}
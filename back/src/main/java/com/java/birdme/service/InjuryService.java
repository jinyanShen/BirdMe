package com.java.birdme.service;

import com.java.birdme.bean.Report;
import java.util.List;
import java.util.Map;

/**
 * Injury report service for heatmap and statistics
 */
public interface InjuryService {

    /**
     * Get heatmap data for injury reports
     * @param timeRange Time range: 7,30,90,all
     * @param species Filter by bird species
     * @param injuryType Filter by injury type
     * @return Heatmap data with points, stats, hotspots
     */
    Map<String, Object> getHeatmapData(String timeRange, String species, String injuryType);

    /**
     * Get recent reports for display
     * @param limit Number of records to return
     * @return List of recent reports
     */
    List<Report> getRecentReports(int limit);
}
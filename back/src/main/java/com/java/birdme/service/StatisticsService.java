package com.java.birdme.service;

import java.util.Map;

/**
 * Statistics service
 * Provides business logic for statistics and analytics
 */
public interface StatisticsService {
    /**
     * Get report statistics
     */
    Map<String, Object> getReportStatistics();
    
    /**
     * Get reports by status
     */
    Map<String, Object> getReportsByStatus();
    
    /**
     * Get reports by injury type
     */
    Map<String, Object> getReportsByInjuryType();
    
    /**
     * Get reports over time
     */
    Map<String, Object> getReportsOverTime();
    
    /**
     * Get rescue station statistics
     */
    Map<String, Object> getRescueStationStatistics();
    
    /**
     * Get overall statistics
     */
    Map<String, Object> getOverallStatistics();
}
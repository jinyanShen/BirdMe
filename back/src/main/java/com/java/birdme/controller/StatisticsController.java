package com.java.birdme.controller;

import com.java.birdme.bean.ReturnResp;
import com.java.birdme.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Statistics controller
 * Handles API requests related to statistics and analytics
 */
@RestController
@RequestMapping("/statistics")
@CrossOrigin
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * Get report statistics
     */
    @RequestMapping("/report")
    public ReturnResp getReportStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getReportStatistics();
            return ReturnResp.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get report statistics");
        }
    }
    
    /**
     * Get reports by status
     */
    @RequestMapping("/reports-by-status")
    public ReturnResp getReportsByStatus() {
        try {
            Map<String, Object> statistics = statisticsService.getReportsByStatus();
            return ReturnResp.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get reports by status");
        }
    }
    
    /**
     * Get reports by injury type
     */
    @RequestMapping("/reports-by-injury-type")
    public ReturnResp getReportsByInjuryType() {
        try {
            Map<String, Object> statistics = statisticsService.getReportsByInjuryType();
            return ReturnResp.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get reports by injury type");
        }
    }
    
    /**
     * Get reports over time
     */
    @RequestMapping("/reports-over-time")
    public ReturnResp getReportsOverTime() {
        try {
            Map<String, Object> statistics = statisticsService.getReportsOverTime();
            return ReturnResp.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get reports over time");
        }
    }
    
    /**
     * Get rescue station statistics
     */
    @RequestMapping("/rescue-station")
    public ReturnResp getRescueStationStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getRescueStationStatistics();
            return ReturnResp.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get rescue station statistics");
        }
    }
    
    /**
     * Get overall statistics
     */
    @RequestMapping("/overall")
    public ReturnResp getOverallStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getOverallStatistics();
            return ReturnResp.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get overall statistics");
        }
    }
}
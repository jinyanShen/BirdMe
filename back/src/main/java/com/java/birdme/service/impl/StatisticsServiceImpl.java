package com.java.birdme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.java.birdme.bean.Report;
import com.java.birdme.dao.ReportMapper;
import com.java.birdme.dao.RescueStationMapper;
import com.java.birdme.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Statistics service implementation
 * Provides business logic implementation for statistics and analytics
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private RescueStationMapper rescueStationMapper;

    @Override
    public Map<String, Object> getReportStatistics() {
        List<Report> allReports = reportMapper.selectList(null);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalReports", allReports.size());

        long pendingCount = allReports.stream().filter(r -> "PENDING".equals(r.getStatus())).count();
        long processingCount = allReports.stream().filter(r -> "PROCESSING".equals(r.getStatus())).count();
        long completedCount = allReports.stream().filter(r -> "COMPLETED".equals(r.getStatus())).count();
        long cancelledCount = allReports.stream().filter(r -> "CANCELLED".equals(r.getStatus())).count();

        statistics.put("pendingReports", (int) pendingCount);
        statistics.put("processingReports", (int) processingCount);
        statistics.put("completedReports", (int) completedCount);
        statistics.put("cancelledReports", (int) cancelledCount);

        return statistics;
    }

    @Override
    public Map<String, Object> getReportsByStatus() {
        Map<String, Object> result = new HashMap<>();

        // Get reports by each status
        LambdaQueryWrapper<com.java.birdme.bean.Report> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(com.java.birdme.bean.Report::getStatus, "PENDING");
        long pendingCount = reportMapper.selectCount(pendingWrapper);

        LambdaQueryWrapper<com.java.birdme.bean.Report> processingWrapper = new LambdaQueryWrapper<>();
        processingWrapper.eq(com.java.birdme.bean.Report::getStatus, "PROCESSING");
        long processingCount = reportMapper.selectCount(processingWrapper);

        LambdaQueryWrapper<com.java.birdme.bean.Report> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(com.java.birdme.bean.Report::getStatus, "COMPLETED");
        long completedCount = reportMapper.selectCount(completedWrapper);

        LambdaQueryWrapper<com.java.birdme.bean.Report> cancelledWrapper = new LambdaQueryWrapper<>();
        cancelledWrapper.eq(com.java.birdme.bean.Report::getStatus, "CANCELLED");
        long cancelledCount = reportMapper.selectCount(cancelledWrapper);

        result.put("PENDING", pendingCount);
        result.put("PROCESSING", processingCount);
        result.put("COMPLETED", completedCount);
        result.put("CANCELLED", cancelledCount);

        return result;
    }

    @Override
    public Map<String, Object> getReportsByInjuryType() {
        Map<String, Object> result = new HashMap<>();

        // Get all reports
        LambdaQueryWrapper<com.java.birdme.bean.Report> wrapper = new LambdaQueryWrapper<>();
        List<com.java.birdme.bean.Report> allReports = reportMapper.selectList(wrapper);

        // Count by injury type
        Map<String, Integer> injuryTypeCount = new HashMap<>();
        for (com.java.birdme.bean.Report report : allReports) {
            String injuryType = report.getInjuryType();
            if (injuryType != null) {
                injuryTypeCount.put(injuryType, injuryTypeCount.getOrDefault(injuryType, 0) + 1);
            }
        }

        result.put("injuryTypes", injuryTypeCount);

        return result;
    }

    @Override
    public Map<String, Object> getReportsOverTime() {
        Map<String, Object> result = new HashMap<>();

        // Get all reports
        LambdaQueryWrapper<com.java.birdme.bean.Report> wrapper = new LambdaQueryWrapper<>();
        List<com.java.birdme.bean.Report> allReports = reportMapper.selectList(wrapper);

        // Group by month (simplified implementation)
        Map<String, Integer> monthlyCount = new HashMap<>();
        for (com.java.birdme.bean.Report report : allReports) {
            if (report.getCreatedAt() != null) {
                String createdAt = report.getCreatedAt().toString();
                // Extract month from created_at (format: yyyy-MM-dd HH:mm:ss)
                String month = createdAt.substring(0, 7); // yyyy-MM
                monthlyCount.put(month, monthlyCount.getOrDefault(month, 0) + 1);
            }
        }

        result.put("monthlyReports", monthlyCount);

        return result;
    }

    @Override
    public Map<String, Object> getRescueStationStatistics() {
        Map<String, Object> result = new HashMap<>();

        // Get all rescue stations
        LambdaQueryWrapper<com.java.birdme.bean.RescueStation> wrapper = new LambdaQueryWrapper<>();
        List<com.java.birdme.bean.RescueStation> allStations = rescueStationMapper.selectList(wrapper);

        result.put("totalStations", allStations.size());

        // Count stations by status (if applicable)
        result.put("activeStations", allStations.size());

        return result;
    }

    @Override
    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> result = new HashMap<>();

        // Report statistics
        Map<String, Object> reportStats = getReportStatistics();
        result.putAll(reportStats);

        // Rescue station statistics
        Map<String, Object> stationStats = getRescueStationStatistics();
        result.putAll(stationStats);

        // Reports by status
        Map<String, Object> statusStats = getReportsByStatus();
        result.put("reportsByStatus", statusStats);

        // Reports by injury type
        Map<String, Object> injuryStats = getReportsByInjuryType();
        result.putAll(injuryStats);

        // Reports over time
        Map<String, Object> timeStats = getReportsOverTime();
        result.putAll(timeStats);

        return result;
    }
}

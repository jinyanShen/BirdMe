package com.java.birdme.service;

import com.java.birdme.bean.Report;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;
import java.util.List;
import java.util.Map;

/**
 * Report service
 * Provides business logic for bird rescue reports
 */
public interface ReportService {
    /**
     * Add new report
     */
    ReturnResp insert(Report report);

    /**
     * Update report
     */
    ReturnResp update(Report report);

    /**
     * Query by primary key id
     */
    ReturnResp getOne(Integer id);

    /**
     * Query with pagination
     */
    PageResp page(PageReq<Report> pageReq);

    /**
     * Get all reports
     */
    ReturnResp all();

    /**
     * Delete reports
     */
    ReturnResp delete(List<Integer> ids);
    
    /**
     * Get reports by submitter ID
     */
    List<Report> getReportsBySubmitterId(String submitterId);
    
    /**
     * Get reports by rescue station ID
     */
    List<Report> getReportsByRescueStationId(Integer rescueStationId);
    
    /**
     * Get reports by status
     */
    List<Report> getReportsByStatus(String status);
    
    /**
     * Update report status
     */
    ReturnResp updateReportStatus(Integer reportId, String status, String notes);
    
    /**
     * Get report statistics
     */
    Map<String, Object> getReportStatistics();
}
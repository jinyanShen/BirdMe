package com.java.birdme.controller;

import com.java.birdme.bean.Report;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Report controller
 * Handles API requests related to bird rescue reports
 */
@RestController
@RequestMapping("/report")
@CrossOrigin
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    /**
     * Add new report
     **/
    @RequestMapping("/insert")
    public ReturnResp insert(@RequestBody Report report) {
        return reportService.insert(report);
    }

    /**
     * Batch delete
     **/
    @RequestMapping("/delete")
    public ReturnResp delete(@RequestBody Integer[] ids) {
        return reportService.delete(Arrays.asList(ids));
    }

    /**
     * Update report
     **/
    @RequestMapping("/update")
    public ReturnResp update(@RequestBody Report report) {
        return reportService.update(report);
    }

    /**
     * Query by primary key id
     **/
    @RequestMapping("/getOne")
    public ReturnResp getOne(@RequestParam("id") Integer id) {
        return reportService.getOne(id);
    }

    /**
     * Query with pagination
     **/
    @RequestMapping("/page")
    public PageResp page(@RequestBody PageReq<Report> pageReq) {
        return reportService.page(pageReq);
    }

    /**
     * Get all reports (admin only)
     */
    @RequestMapping("/all")
    public ReturnResp all() {
        return reportService.all();
    }
    
    /**
     * Get reports by submitter ID
     */
    @RequestMapping("/by-submitter")
    public ReturnResp getReportsBySubmitterId(@RequestParam String submitterId) {
        try {
            List<Report> reports = reportService.getReportsBySubmitterId(submitterId);
            return ReturnResp.success(reports);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get reports");
        }
    }
    
    /**
     * Get reports by status
     */
    @RequestMapping("/by-status")
    public ReturnResp getReportsByStatus(@RequestParam String status) {
        try {
            List<Report> reports = reportService.getReportsByStatus(status);
            return ReturnResp.success(reports);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get reports");
        }
    }
    
    /**
     * Submit new report
     */
    @RequestMapping("/submit")
    public ReturnResp submitReport(@RequestBody Report report) {
        return reportService.insert(report);
    }
    
    /**
     * Update report status (rescue station only)
     */
    @RequestMapping("/update-status")
    public ReturnResp updateReportStatus(
            @RequestParam Integer reportId,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {
        return reportService.updateReportStatus(reportId, status, notes);
    }
    
    /**
     * Get report statistics (admin only)
     */
    @RequestMapping("/statistics")
    public ReturnResp getReportStatistics() {
        try {
            Map<String, Object> statistics = reportService.getReportStatistics();
            return ReturnResp.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnResp.fail("Failed to get report statistics");
        }
    }
}
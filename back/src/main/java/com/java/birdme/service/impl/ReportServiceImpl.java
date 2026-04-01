package com.java.birdme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.birdme.bean.Report;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.dao.ReportMapper;
import com.java.birdme.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Report service implementation
 * Provides business logic implementation for bird rescue reports
 */
@Service
public class ReportServiceImpl implements ReportService {
    
    @Autowired
    private ReportMapper reportMapper;
    
    @Override
    public ReturnResp insert(Report report) {
        if (report == null) {
            return ReturnResp.fail("Required parameters missing");
        }

        report.setCreatedAt(new Date());
        report.setUpdatedAt(new Date());
        report.setStatus("PENDING");
        reportMapper.insert(report);
        return ReturnResp.success();
    }

    @Override
    public ReturnResp update(Report report) {
        report.setUpdatedAt(new Date());
        int ret = reportMapper.updateById(report);
        return ret > 0 ? ReturnResp.success() : ReturnResp.fail();
    }

    @Override
    public ReturnResp getOne(Integer id) {
        Report report = reportMapper.selectById(id);
        return ReturnResp.success(report);
    }

    @Override
    public PageResp page(PageReq<Report> pageReq) {
        Page<Report> page = new Page<>(pageReq.getOffset(), pageReq.getPageSize());

        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        if (pageReq.getData() != null) {
            String keyword = pageReq.getData().getBirdName();
            String status = pageReq.getData().getStatus();
            
            if (keyword != null && !keyword.isEmpty()) {
                wrapper.and(w -> w.like(Report::getBirdName, keyword).or().like(Report::getLocation, keyword));
            }
            if (status != null && !status.isEmpty()) {
                wrapper.eq(Report::getStatus, status);
            }
        }
        Page<Report> reportPage = reportMapper.selectPage(page, wrapper);
        List<Report> records = reportPage.getRecords();

        return PageResp.page(pageReq.getOffset(), pageReq.getPageSize(), (int) reportPage.getTotal(), records);
    }

    @Override
    public ReturnResp all() {
        return ReturnResp.success(reportMapper.selectList(null));
    }

    @Override
    public ReturnResp delete(List<Integer> ids) {
        Integer ret = reportMapper.deleteBatchIds(ids);
        return ret > 0 ? ReturnResp.success() : ReturnResp.fail();
    }
    
    @Override
    public List<Report> getReportsBySubmitterId(String submitterId) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Report::getSubmitterId, submitterId);
        return reportMapper.selectList(wrapper);
    }
    
    @Override
    public List<Report> getReportsByRescueStationId(Integer rescueStationId) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Report::getRescueStationId, rescueStationId);
        return reportMapper.selectList(wrapper);
    }
    
    @Override
    public List<Report> getReportsByStatus(String status) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Report::getStatus, status);
        return reportMapper.selectList(wrapper);
    }
    
    @Override
    public ReturnResp updateReportStatus(Integer reportId, String status, String notes) {
        Report report = reportMapper.selectById(reportId);
        if (report != null) {
            report.setStatus(status);
            report.setUpdatedAt(new Date());
            report.setNotes(notes);
            int ret = reportMapper.updateById(report);
            return ret > 0 ? ReturnResp.success() : ReturnResp.fail();
        }
        return ReturnResp.fail("Report not found");
    }
    
    @Override
    public Map<String, Object> getReportStatistics() {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        List<Report> allReports = reportMapper.selectList(wrapper);
        
        Map<String, Object> statistics = new java.util.HashMap<>();
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
}
package com.java.birdme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.birdme.bean.*;
import com.java.birdme.dao.ReportMapper;
import com.java.birdme.dao.RescueStationMapper;
import com.java.birdme.dao.UserMapper;
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

    @Autowired
    private RescueStationMapper rescueStationMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public ReturnResp insert(Report report) {
        if (report == null) {
            return ReturnResp.fail("Required parameters missing");
        }

        report.setCreatedAt(new Date());
        report.setUpdatedAt(new Date());
        report.setStatus("PENDING");

        if (report.getRescueStationId() == null && report.getLatitude() != null && report.getLongitude() != null) {
            //Add rescue station
            RescueStation newStation = new RescueStation();
            newStation.setName(report.getName());
            newStation.setAddress(report.getLocation());
            newStation.setLatitude(report.getLatitude());
            newStation.setLongitude(report.getLongitude());
            newStation.setPhone(report.getSubmitterId());
            newStation.setDescription("Auto-registered rescue station from report #" + System.currentTimeMillis());
            rescueStationMapper.insert(newStation);
            report.setRescueStationId(newStation.getId());

            //Add user
            User user = new User();
            if (report.getName().contains("(")) {
                user.setUsername(report.getName().substring(0, report.getName().indexOf("(")).trim());
            } else {
                user.setUsername(report.getName());
            }
            user.setName(report.getName());
            user.setPassword("123456");
            user.setRescueStationId(newStation.getId());
            user.setRole(2);
            user.setCreatedAt(new Date());
            userMapper.insert(user);
        }

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
        String keyword = pageReq.getData().getBirdName();
        String status = pageReq.getData().getStatus();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Report::getBirdName, keyword).or().like(Report::getLocation, keyword));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Report::getStatus, status);
        }

        Page<Report> reportPage = reportMapper.selectPage(page, wrapper);
        List<Report> records = reportPage.getRecords();

        return PageResp.page(pageReq.getOffset(), pageReq.getPageSize(), (int) reportPage.getTotal(), records);
    }

    @Override
    public PageResp pageMy(PageReq<Report> pageReq) {
        Page<Report> page = new Page<>(pageReq.getOffset(), pageReq.getPageSize());

        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Report::getRescueStationId, pageReq.getData().getRescueStationId());
        String keyword = pageReq.getData().getBirdName();
        String status = pageReq.getData().getStatus();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Report::getBirdName, keyword).or().like(Report::getLocation, keyword));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Report::getStatus, status);
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

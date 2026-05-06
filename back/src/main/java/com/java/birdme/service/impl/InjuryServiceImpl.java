package com.java.birdme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.java.birdme.bean.Report;
import com.java.birdme.bean.User;
import com.java.birdme.dao.ReportMapper;
import com.java.birdme.dao.UserMapper;
import com.java.birdme.service.InjuryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Injury report service implementation
 */
@Service
public class InjuryServiceImpl implements InjuryService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, Object> getHeatmapData(String timeRange, String species, String injuryType) {
        Map<String, Object> result = new HashMap<>();

        // Build query wrapper
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(Report::getLatitude)
                .isNotNull(Report::getLongitude);

        // Filter by status (only pending and processing reports)
        wrapper.in(Report::getStatus, "PENDING", "PROCESSING");

        // Time range filter
        if (!"all".equals(timeRange)) {
            int days = Integer.parseInt(timeRange);
            Date startDate = Date.from(LocalDateTime.now().minusDays(days)
                    .atZone(ZoneId.systemDefault()).toInstant());
            wrapper.ge(Report::getCreatedAt, startDate);
        }

        // Species filter
        if (species != null && !species.isEmpty()) {
            wrapper.eq(Report::getBirdName, species);
        }

        // Injury type filter
        if (injuryType != null && !injuryType.isEmpty()) {
            wrapper.eq(Report::getInjuryType, injuryType);
        }

        List<Report> reports = reportMapper.selectList(wrapper);

        if (reports.isEmpty()) {
            result.put("points", new ArrayList<>());
            result.put("stats", new HashMap<>());
            result.put("hotspots", new ArrayList<>());
            result.put("speciesList", new ArrayList<>());
            return result;
        }

        // Aggregate by location (round to 2 decimal places ~1.1km精度)
        Map<String, Integer> areaStats = new HashMap<>();
        Map<String, Set<String>> areaSpecies = new HashMap<>();
        Map<String, String> areaInjuryTypes = new HashMap<>();

        for (Report report : reports) {
            if (report.getLatitude() != null && report.getLongitude() != null) {
                String gridKey = String.format("%.2f,%.2f",
                        Math.round(report.getLatitude() * 100) / 100.0,
                        Math.round(report.getLongitude() * 100) / 100.0);

                areaStats.put(gridKey, areaStats.getOrDefault(gridKey, 0) + 1);
                areaSpecies.computeIfAbsent(gridKey, k -> new HashSet<>()).add(report.getBirdName());

                String currentType = areaInjuryTypes.get(gridKey);
                if (currentType == null && report.getInjuryType() != null) {
                    areaInjuryTypes.put(gridKey, report.getInjuryType());
                }
            }
        }

        // Build heatmap points
        List<Map<String, Object>> points = new ArrayList<>();
        int maxCount = areaStats.values().stream().max(Integer::compareTo).orElse(1);

        for (Map.Entry<String, Integer> entry : areaStats.entrySet()) {
            String[] coords = entry.getKey().split(",");
            Map<String, Object> point = new HashMap<>();
            point.put("lat", Double.parseDouble(coords[0]));
            point.put("lng", Double.parseDouble(coords[1]));
            point.put("intensity", Math.min(1.0, (double) entry.getValue() / maxCount));
            point.put("reportCount", entry.getValue());

            Set<String> speciesSet = areaSpecies.get(entry.getKey());
            if (speciesSet != null && !speciesSet.isEmpty()) {
                point.put("species", speciesSet.iterator().next());
            }
            point.put("injuryType", areaInjuryTypes.get(entry.getKey()));
            points.add(point);
        }

        // Build hotspots (areas with >= 5 reports)
        List<Map<String, Object>> hotspots = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : areaStats.entrySet()) {
            if (entry.getValue() >= 5) {
                Map<String, Object> hotspot = new HashMap<>();
                String[] coords = entry.getKey().split(",");
                hotspot.put("area", String.format("Area at %.2f, %.2f",
                        Double.parseDouble(coords[0]), Double.parseDouble(coords[1])));
                hotspot.put("reportCount", entry.getValue());

                String severity;
                if (entry.getValue() >= 30) severity = "Severe";
                else if (entry.getValue() >= 15) severity = "High";
                else if (entry.getValue() >= 8) severity = "Medium";
                else severity = "Low";
                hotspot.put("severity", severity);

                Set<String> speciesSet = areaSpecies.get(entry.getKey());
                if (speciesSet != null && !speciesSet.isEmpty()) {
                    List<String> speciesList = new ArrayList<>(speciesSet);
                    hotspot.put("commonSpecies", speciesList.subList(0, Math.min(3, speciesList.size())));
                }
                hotspots.add(hotspot);
            }
        }
        hotspots.sort((a, b) -> Integer.compare(
                (Integer) b.get("reportCount"), (Integer) a.get("reportCount")));

        // Build statistics
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReports", reports.size());
        stats.put("affectedSpecies", reports.stream()
                .map(Report::getBirdName)
                .filter(Objects::nonNull)
                .distinct()
                .count());
        stats.put("highRiskAreas", hotspots.size());

        long resolvedCount = reports.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .count();
        stats.put("rescuedCount", resolvedCount);

        // Get species list for filter dropdown
        LambdaQueryWrapper<Report> speciesWrapper = new LambdaQueryWrapper<>();
        speciesWrapper.select(Report::getBirdName)
                .isNotNull(Report::getBirdName)
                .groupBy(Report::getBirdName);
        List<Report> speciesReports = reportMapper.selectList(speciesWrapper);
        List<String> speciesList = speciesReports.stream()
                .map(Report::getBirdName)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        result.put("points", points);
        result.put("stats", stats);
        result.put("hotspots", hotspots);
        result.put("speciesList", speciesList);

        return result;
    }

    @Override
    public List<Report> getRecentReports(int limit) {
        LambdaQueryWrapper<Report> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(Report::getLatitude)
                .isNotNull(Report::getLongitude)
                .orderByDesc(Report::getCreatedAt)
                .last("LIMIT " + limit);

        List<Report> reports = reportMapper.selectList(wrapper);

        // Set submitter name for each report (optional, if you need user name)
        if (CollectionUtils.isNotEmpty(reports)) {
            for (Report report : reports) {
                if (report.getSubmitterId() != null) {
                    User user = userMapper.selectById(report.getSubmitterId());
                    if (user != null) {
                        report.setSubmitter(user.getName());
                    }
                }
            }
        }

        return reports;
    }
}
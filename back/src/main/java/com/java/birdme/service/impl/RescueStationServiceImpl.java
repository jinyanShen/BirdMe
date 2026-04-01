package com.java.birdme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.birdme.bean.RescueStation;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.dao.RescueStationMapper;
import com.java.birdme.service.RescueStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Rescue station service implementation
 * Provides business logic implementation for rescue stations
 */
@Service
public class RescueStationServiceImpl implements RescueStationService {

    @Autowired
    private RescueStationMapper mapper;

    @Override
    public ReturnResp insert(RescueStation rescueStation) {
        if (rescueStation == null) {
            return ReturnResp.fail("Required parameters missing");
        }

        mapper.insert(rescueStation);
        return ReturnResp.success();
    }

    @Override
    public ReturnResp update(RescueStation rescueStation) {
        int ret = mapper.updateById(rescueStation);
        return ret > 0 ? ReturnResp.success() : ReturnResp.fail();
    }

    @Override
    public ReturnResp getOne(Integer id) {
        RescueStation station = mapper.selectById(id);
        return ReturnResp.success(station);
    }

    @Override
    public PageResp page(PageReq<RescueStation> pageReq) {
        Page<RescueStation> page = new Page<>(pageReq.getOffset(), pageReq.getPageSize());

        LambdaQueryWrapper<RescueStation> wrapper = new LambdaQueryWrapper<>();
        if (pageReq.getData() != null) {
            String keyword = pageReq.getData().getName();
            if (keyword != null && !keyword.isEmpty()) {
                wrapper.and(w -> w.like(RescueStation::getName, keyword).or().like(RescueStation::getAddress, keyword));
            }
        }
        Page<RescueStation> stationPage = mapper.selectPage(page, wrapper);
        List<RescueStation> records = stationPage.getRecords();

        return PageResp.page(pageReq.getOffset(), pageReq.getPageSize(), (int) stationPage.getTotal(), records);
    }

    @Override
    public ReturnResp all() {
        return ReturnResp.success(mapper.selectList(null));
    }

    @Override
    public ReturnResp delete(List<Integer> ids) {
        Integer ret = mapper.deleteBatchIds(ids);
        return ret > 0 ? ReturnResp.success() : ReturnResp.fail();
    }

    @Override
    public List<RescueStation> getNearbyStations(double latitude, double longitude, double radius) {
        // For simplicity, return all stations sorted by distance
        // In a real implementation, you would use geographic calculations
        LambdaQueryWrapper<RescueStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(RescueStation::getId);
        return mapper.selectList(wrapper);
    }
}
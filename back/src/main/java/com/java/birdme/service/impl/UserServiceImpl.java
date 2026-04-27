package com.java.birdme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.birdme.bean.*;
import com.java.birdme.dao.RescueStationMapper;
import com.java.birdme.dao.UserMapper;
import com.java.birdme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper mapper;
    @Autowired
    private RescueStationMapper rescueStationMapper;

    @Override
    public ReturnResp insert(User user) {
        if (user == null) {
            return ReturnResp.fail("Required parameters missing");
        }

        mapper.insert(user);
        return ReturnResp.success();
    }

    @Override
    public ReturnResp update(User user) {
        if (user.getRole() == 2){
            LambdaQueryWrapper<RescueStation> rescueStationWrapper = new LambdaQueryWrapper<>();
            rescueStationWrapper.eq(RescueStation::getName, user.getName());
            RescueStation rescueStation = rescueStationMapper.selectOne(rescueStationWrapper);
            rescueStation.setPhone(user.getPhone());

            rescueStationMapper.updateById(rescueStation);
        }


        int ret = mapper.updateById(user);
        return ret > 0 ? ReturnResp.success() : ReturnResp.fail();
    }

    @Override
    public ReturnResp getOne(Integer userId) {
        User user = mapper.selectById(userId);
        return ReturnResp.success(user);
    }

    @Override
    public PageResp page(PageReq<User> pageReq) {
        Page<User> page = new Page<>(pageReq.getOffset(), pageReq.getPageSize());

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (pageReq.getData().getName() != null && !pageReq.getData().getName().isEmpty()) {
            wrapper.like(User::getName, pageReq.getData().getName());
        }
        if (pageReq.getData().getRole() != null) {
            wrapper.eq(User::getRole, pageReq.getData().getRole());
        }
        Page<User> userPage = mapper.selectPage(page, wrapper);
        List<User> records = userPage.getRecords();

        return PageResp.page(pageReq.getOffset(), pageReq.getPageSize(), (int) userPage.getTotal(), records);
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
    public User login(User user) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername()).eq(User::getPassword, user.getPassword());
        List<User> userList = mapper.selectList(wrapper);
        return CollectionUtils.isEmpty(userList) ? null : userList.get(0);
    }
}

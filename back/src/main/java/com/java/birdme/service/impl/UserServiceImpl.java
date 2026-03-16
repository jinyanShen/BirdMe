package com.java.birdme.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.java.birdme.bean.User;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;
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

    @Override
    public ReturnResp insert(User user) {
        // valid
        if (user == null) {
            return ReturnResp.fail("Required parameters missing");
        }

        mapper.insert(user);
        return ReturnResp.success();
    }

    @Override
    public ReturnResp update(User user) {
        int ret = mapper.updateById(user);
        return ret>0? ReturnResp.success(): ReturnResp.fail();
    }


    @Override
    public ReturnResp getOne(Integer userId) {
        User user = mapper.selectById(userId);
        return ReturnResp.success(user);
    }


    @Override
    public PageResp page(PageReq<User> pageReq) {
        Page<User> page = new Page<>(pageReq.getOffset(), pageReq.getPageSize());

        EntityWrapper<User> wrapper = new EntityWrapper<>();
        wrapper.like("name", "%" + pageReq.getData().getName() + "%");
        List<User> list = mapper.selectPage(page, wrapper);
        Integer total = mapper.selectCount(wrapper);

        return PageResp.page(pageReq.getOffset(), pageReq.getPageSize(), total, list);
    }

    @Override
    public ReturnResp all() {
        return ReturnResp.success(mapper.selectList(null));
    }

    @Override
    public ReturnResp delete(List<Integer> ids) {
        Integer ret = mapper.deleteBatchIds(ids);
        return ret>0? ReturnResp.success(): ReturnResp.fail();
    }

    @Override
    public User login(User user) {
        EntityWrapper<User> wrapper = new EntityWrapper<>();
        wrapper.eq("username", user.getUsername()).eq("password", user.getPassword());
        List<User> patientList = mapper.selectList(wrapper);
        return CollectionUtils.isEmpty(patientList) ? null : patientList.get(0);
    }
}

package com.java.birdme.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java.birdme.bean.Address;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.dao.AddressMapper;
import com.java.birdme.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;


import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper mapper;

    @Override
    public ReturnResp insert(Address address) {
        if (address == null) {
            return ReturnResp.fail("Required parameters missing");
        }

        mapper.insert(address);
        return ReturnResp.success();
    }

    @Override
    public ReturnResp update(Address address) {
        int ret = mapper.updateById(address);
        return ret > 0 ? ReturnResp.success() : ReturnResp.fail();
    }

    @Override
    public ReturnResp getOne(Integer addressId) {
        Address address = mapper.selectById(addressId);
        return ReturnResp.success(address);
    }


    @Override
    public PageResp page(PageReq<Address> pageReq) {
        Page<Address> page = new Page<>(pageReq.getOffset(), pageReq.getPageSize());

        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        if (pageReq.getData() != null && pageReq.getData().getUserId() != null) {
            wrapper.eq("user_id", pageReq.getData().getUserId());
        }

        mapper.selectPage(page, wrapper);

        List<Address> list = page.getRecords();
        Integer total = (int) page.getTotal();

        return PageResp.page(pageReq.getOffset(), pageReq.getPageSize(), total, list);
    }

    @Override
    public ReturnResp getByUserId(Integer userId) {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("is_default");
        wrapper.orderByDesc("created_at");

        List<Address> list = mapper.selectList(wrapper);
        return ReturnResp.success(list);
    }

    @Override
    public ReturnResp delete(List<Integer> ids) {
        Integer ret = mapper.deleteBatchIds(ids);
        return ret > 0 ? ReturnResp.success() : ReturnResp.fail();
    }

    @Override
    @Transactional
    public ReturnResp setDefault(Integer addressId, Integer userId) {
        // Set all addresses of this user to non-default
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        Address updateObj = new Address();
        updateObj.setIsDefault(0);
        mapper.update(updateObj, wrapper);

        // Set the specified address as default
        Address address = mapper.selectById(addressId);
        if (address != null && address.getUserId().equals(userId)) {
            address.setIsDefault(1);
            mapper.updateById(address);
            return ReturnResp.success();
        }

        return ReturnResp.fail("Address not found or does not belong to this user");
    }
}
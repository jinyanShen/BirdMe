package com.java.birdme.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.java.birdme.bean.Address;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.dao.AddressMapper;
import com.java.birdme.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return ret>0? ReturnResp.success(): ReturnResp.fail();
    }

    @Override
    public ReturnResp getOne(Integer addressId) {
        Address address = mapper.selectById(addressId);
        return ReturnResp.success(address);
    }

    @Override
    public PageResp page(PageReq<Address> pageReq) {
        Page<Address> page = new Page<>(pageReq.getOffset(), pageReq.getPageSize());

        EntityWrapper<Address> wrapper = new EntityWrapper<>();
        if (pageReq.getData() != null && pageReq.getData().getUserId() != null) {
            wrapper.eq("user_id", pageReq.getData().getUserId());
        }
        List<Address> list = mapper.selectPage(page, wrapper);
        Integer total = mapper.selectCount(wrapper);

        return PageResp.page(pageReq.getOffset(), pageReq.getPageSize(), total, list);
    }

    @Override
    public ReturnResp getByUserId(Integer userId) {
        EntityWrapper<Address> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderBy("is_default", false);
        wrapper.orderBy("created_at", false);
        List<Address> list = mapper.selectList(wrapper);
        return ReturnResp.success(list);
    }

    @Override
    public ReturnResp delete(List<Integer> ids) {
        Integer ret = mapper.deleteBatchIds(ids);
        return ret>0? ReturnResp.success(): ReturnResp.fail();
    }

    @Override
    @Transactional
    public ReturnResp setDefault(Integer addressId, Integer userId) {
        // Set all addresses of this user to non-default
        EntityWrapper<Address> wrapper = new EntityWrapper<>();
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

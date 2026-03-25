package com.java.birdme.service;

import com.java.birdme.bean.Address;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;

import java.util.List;

public interface AddressService {
    /**
     * Add new
     */
    ReturnResp insert(Address address);

    /**
     * Update
     */
    ReturnResp update(Address address);

    /**
     * Query by primary key id
     */
    ReturnResp getOne(Integer addressId);

    /**
     * Query with pagination
     */
    PageResp page(PageReq<Address> pageReq);

    /**
     * Query all addresses by user ID
     */
    ReturnResp getByUserId(Integer userId);

    /**
     * Delete
     */
    ReturnResp delete(List<Integer> ids);

    /**
     * Set default address
     */
    ReturnResp setDefault(Integer addressId, Integer userId);
}

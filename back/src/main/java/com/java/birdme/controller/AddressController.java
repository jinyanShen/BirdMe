package com.java.birdme.controller;

import com.java.birdme.bean.Address;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping(value = "/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * Add new
     **/
    @RequestMapping("/insert")
    public ReturnResp insert(@RequestBody Address address) {
        return addressService.insert(address);
    }

    /**
     * Update
     **/
    @RequestMapping("/update")
    public ReturnResp update(@RequestBody Address address) {
        return addressService.update(address);
    }

    /**
     * Query by primary key id
     **/
    @RequestMapping("/getOne")
    public ReturnResp getOne(@RequestParam("id") Integer id) {
        return addressService.getOne(id);
    }

    /**
     * Query with pagination
     **/
    @RequestMapping("/page")
    public PageResp page(@RequestBody PageReq<Address> pageReq) {
        return addressService.page(pageReq);
    }

    /**
     * Query all addresses by user ID
     **/
    @RequestMapping("/getByUserId")
    public ReturnResp getByUserId(@RequestParam("userId") Integer userId) {
        return addressService.getByUserId(userId);
    }

    /**
     * Batch delete
     **/
    @RequestMapping("/delete")
    public ReturnResp delete(@RequestBody Integer[] ids) {
        return addressService.delete(Arrays.asList(ids));
    }

    /**
     * Set default address
     **/
    @RequestMapping("/setDefault")
    public ReturnResp setDefault(@RequestParam("addressId") Integer addressId, @RequestParam("userId") Integer userId) {
        return addressService.setDefault(addressId, userId);
    }
}

package com.java.birdme.controller;

import com.java.birdme.bean.User;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;
import com.java.birdme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Add new
     **/
    @RequestMapping("/insert")
    public ReturnResp insert(@RequestBody User user) {
        return userService.insert(user);
    }

    /**
     * Batch delete
     **/
    @RequestMapping("/delete")
    public ReturnResp delete(@RequestBody Integer[] ids) {
        return userService.delete(Arrays.asList(ids));
    }

    /**
     * Update
     **/
    @RequestMapping("/update")
    public ReturnResp update(@RequestBody User user) {
        return userService.update(user);
    }

    /**
     * Query by primary key id
     **/
    @RequestMapping("/getOne")
    public ReturnResp getOne(@RequestParam("id") Integer id) {
        return userService.getOne(id);
    }

    /**
     * Query with pagination
     **/
    @RequestMapping("/page")
    public PageResp page(@RequestBody PageReq<User> pageReq) {
        return userService.page(pageReq);
    }

    @RequestMapping("/all")
    public ReturnResp all() {
        return userService.all();
    }
}

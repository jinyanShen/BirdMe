package com.java.birdme.service;

import com.java.birdme.bean.User;
import com.java.birdme.bean.PageReq;
import com.java.birdme.bean.PageResp;
import com.java.birdme.bean.ReturnResp;

import java.util.List;

public interface UserService {
    /**
     * Add new
     */
    ReturnResp insert(User user);

    /**
     * Update
     */
    ReturnResp update(User user);

    /**
     * Query by primary key id
     */
    ReturnResp getOne(Integer userId);

    /**
     * Query with pagination
     */
    PageResp page(PageReq<User> pageReq);

    ReturnResp all();

    ReturnResp delete(List<Integer> ids);

    User login(User user);
}

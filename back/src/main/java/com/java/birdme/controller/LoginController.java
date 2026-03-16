package com.java.birdme.controller;

import com.java.birdme.bean.User;
import com.java.birdme.service.UserService;
import com.java.birdme.bean.ReturnResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ReturnResp login(@RequestBody User user, HttpSession session){
        // Check if username is correct
        User user1 = userService.login(user);
        if(user1 == null){
            return ReturnResp.fail("Username or password error");
        }
        // Login successful
        session.setAttribute("user", user1);
        return ReturnResp.success(user1);
    }

    @PostMapping("/logout")
    public ReturnResp logout(HttpSession session){
        session.invalidate();
        return ReturnResp.success(null);
    }

}

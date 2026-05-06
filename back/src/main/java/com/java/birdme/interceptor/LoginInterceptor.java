package com.java.birdme.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String uri = request.getRequestURI();

        // 放行音频代理接口
        if (uri.startsWith("/api/audio/")) {
            return true;
        }

        // 放行登录、注册等公开接口
        if (uri.startsWith("/api/user/login") || uri.startsWith("/api/user/register")) {
            return true;
        }

        // 检查session中是否有用户信息
        Object userId = request.getSession().getAttribute("user");
        if (userId == null) {
            // 未登录，返回401状态码
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        return true;
    }
}
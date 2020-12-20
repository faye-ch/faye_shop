package com.leyou.cart.interceptor;

import com.leyou.cart.config.JwtProperties;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties jwtProperties;

    // 定义一个线程域，存放登录用户,线程内共享数据
    private static ThreadLocal<UserInfo>  THREAD_LOCAL = new ThreadLocal<>();

    //preHandle 前置通知
    //Servlet Api 作为参数，可以从中获取Cookie 用户信息
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.使用Cookie工具类，根据Requst 获取cookie
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        //2.使用Jwt 工具类根据公钥解析 jwt
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

        if (userInfo==null){
            //false 不放行
            return false;
        }
        //3.存入 载荷 到线程域，以便后面获取用户信息
        THREAD_LOCAL.set(userInfo);
        // true 放行
        return true;
    }

    //获取用户信息方法
    public static UserInfo getUserInfo(){
        return THREAD_LOCAL.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空线程的局部变量，
        THREAD_LOCAL.remove();
    }
}

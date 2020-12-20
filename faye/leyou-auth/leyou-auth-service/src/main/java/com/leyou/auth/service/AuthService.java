package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JwtConstans;
import com.leyou.common.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    public String accredit(String username, String password) {

        //1.使用远程调用技术 校验用户名和密码
        User user = userClient.queryUser(username, password);

        //2.判断 user 是否为空
        if(user==null){
            return null;
        }

        //3.生成 token
            //3.1 创建载荷
        try{
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            //调用工具类 JwtUtils 生成 token , 传进User信息,私钥 过期时间
            return JwtUtils.generateToken(userInfo,jwtProperties.getPrivateKey(),jwtProperties.getExpire());

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

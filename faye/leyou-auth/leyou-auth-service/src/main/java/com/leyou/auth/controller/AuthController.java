package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;

    //登录鉴权
    //传进 username password
    @PostMapping("accredit")
    public ResponseEntity<Void> accredit(@RequestParam("username")String username, @RequestParam("password")String password,
                                         HttpServletRequest request, HttpServletResponse response){
        //登录校验
        String token = authService.accredit(username,password);
        if(StringUtils.isBlank(token)){
            //UNAUTOHORIZED 身份未验证
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //将token 写入cookie中 并指定httpOnly 为 true ，防止通过 JS 获取和修改
        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);
        return ResponseEntity.ok(null);
    }


    /*
     * 功能描述: 用户每请求一个页面 都会进行verify验证：
     *           通过请求带来的 cookie 用公钥进行解析得到载荷信息-->即用户信息
     * @Param: [token]
     * @Return: org.springframework.http.ResponseEntity<com.leyou.common.pojo.UserInfo>
     * @Author: CHWN
     * @Date: 2020/5/9 11:46
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verify(@CookieValue("LY_TOKEN")String token,
                                           HttpServletRequest request,
                                           HttpServletResponse response){

        try{
            //1.通过工具类使用公钥解析载荷
            UserInfo user= JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

            //刷新 jwt 的有效时间 重新生成一个 jwt 并且赋值给token  时间单位是分
            token = JwtUtils.generateToken(user,jwtProperties.getPrivateKey(),jwtProperties.getExpire());

            //刷新 cookie ，重新生成一个 cookie
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire()*60);

            if (user==null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

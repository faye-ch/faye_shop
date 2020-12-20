package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    //校验注册的手机号或者用户名 是否被用过
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data")String data,@PathVariable("type")Integer type){
        Boolean bool = userService.checkUser(data,type);
        if(bool==null)
        {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }

    //发送手机验证码
    @PostMapping("code")
    public ResponseEntity<Void> sendCode(@RequestParam("phone")String phone){
       Boolean bool = userService.sendCode(phone);
       if(bool==null || !bool){
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
       }
       return new ResponseEntity<>(HttpStatus.CREATED);

    }

    //用户注册
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code")String code){
        Boolean bool = userService.register(user, code);
        if(bool==null || !bool){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //查询用户
    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username")String username,@RequestParam("password")String password){
        User user = userService.queryUser(username,password);
        if (user==null){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(user);
    }

}

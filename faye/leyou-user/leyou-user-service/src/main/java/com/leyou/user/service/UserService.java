package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.ulits.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    static final String KEY_PREFIX = "USER:CODE:PHONE:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /*
     * 功能描述 校验数据是否可用
     * @Param: [data, type]
     * @Return: java.lang.Boolean
     * @Author: CHWN
     * @Date: 2020/5/6 15:04
     */
    public Boolean checkUser(String data, Integer type) {

        User user = new User();
        if(type==1){
            user.setUsername(data);
        }
        else if(type==2){
            user.setPhone(data);
        }else{
            return null;
        }
        return userMapper.selectCount(user)==0;
    }

    //传入手机号码,发送手机验证码
    public Boolean sendCode(String phone) {

        //使用工具类,生成随机的验证码
        String code = NumberUtils.generateCode(6);

        try{
            //设置
            Map<String,String> msg = new HashMap<>();
            msg.put("phone",phone);
            msg.put("code",code);
            //发送RabbitMq 消息
            amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE","SMS.VERIFY.CODE",msg);

            //存到 Redis 缓存
            //参数：key value 过期数据数值 过期时间单位
            stringRedisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

    }

    //用户注册
    public Boolean register(User user, String code) {

        //1.校验验证码
            //1.1、从redis中取出验证码
        String redisCode = stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(code,redisCode)){
            return false;
        }

        //2.使用工具类生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //3.加盐加密码
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        user.setId(null);
        user.setCreated(new Date());
        //4.注册用户
        userMapper.insertSelective(user);
        return true;

    }

    public User queryUser(String username, String password) {

        //1.根据用户名查询用户
        User user = new User();
        user.setUsername(username);
        User ResultUser = userMapper.selectOne(user);
        //校验用户名
        if (user==null){
            //没有此用户则不用进行一下操作
            return null;
        }
        //获取数据库中的盐
        String salt = ResultUser.getSalt();
        //对用户输入的密码加盐加密
        password = CodecUtils.md5Hex(password, salt);

        //比较数据库中的密码和用户输入的密码
        if (!StringUtils.equals(password,ResultUser.getPassword()))
        {
            //用户时输入的密码错误
            return null;
        }

        return ResultUser;

    }
}

package com.leyou.filter;

import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.config.gateway.FilterProperties;
import com.leyou.config.gateway.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFliter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 5;
    }


    @Override
    public boolean shouldFilter() {
        //1.设置拦截的白名单
        List<String> allowPaths = filterProperties.getAllowPaths();
        //2.获取请求的路径
        //2.1.获取上下文
        RequestContext context = RequestContext.getCurrentContext();

        //2.2.通过上下文 获取 request 对象
        HttpServletRequest request = context.getRequest();
        String requestUrl = request.getRequestURL().toString();

        // 使用 allowPaths 集合判断有没有包含 请求路径 ? 不行因为请求路径是有域名的
        //所以应该遍历判断 请求路径有没有包含 allowPaths 集合中的字符串
        for (String allowPath : allowPaths) {
            if (StringUtils.contains(requestUrl,allowPath)){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //1.获取上下文
        RequestContext context = RequestContext.getCurrentContext();

        //2.通过上下文 获取 request 对象
        HttpServletRequest request = context.getRequest();

        //3.通过 request 获取 cookie
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        try{
            //4.通过公钥解析 token ，若解析不通过则未登录
            JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
        }catch (Exception e){
            e.printStackTrace();
            //校验异常返回 403
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }


        return null;
    }
}

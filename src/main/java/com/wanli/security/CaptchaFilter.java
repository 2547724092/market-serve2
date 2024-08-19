package com.wanli.security;

import com.wanli.config.Const;
import com.wanli.exception.CaptchaException;
import com.wanli.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component  //容器创建
public class CaptchaFilter extends OncePerRequestFilter {
    private final String loginURL = "/login";

    @Autowired
    private RedisUtil redisUtil;  //注入 redis 工具类

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //vue 使用 axios.post() http://localhost:10001/login
        String uri = request.getRequestURI();  //获得 /login
        if (uri.equals(loginURL) && request.getMethod().equals("POST")) {
            log.info("获得Login请求连接，正在校验验证码，请求路径---{}", uri);

            //调用，判断验证码
            try {
                validate(request);
            }catch (CaptchaException e){
                //发生验证码失败异常
                //代码：执行验证码失败的处理器
                loginFailureHandler.onAuthenticationFailure(request,response,e);
            }
        }
        //不是则进行登录操作，放行执行后面的代码
        filterChain.doFilter(request, response);
    }

    //验证验证码的方法
    private void validate(HttpServletRequest request) throws CaptchaException {
        //登录操作，提交请求参数
        String code = request.getParameter("code");   //前端输入的验证码
        String key = request.getParameter("key");  //存储到 redis 中的验证码的键

        //判断是否为空
        if (StringUtils.isBlank(code)) {
            //验证码是空，验证失败。抛出自定义异常
            throw new CaptchaException("验证码不得为空");
        }

        //判断是否正确
        if (!redisUtil.hget(Const.CAPTCHA, key).equals(code)) {
            throw new CaptchaException("验证码不正确");
        }

        //成功
        redisUtil.hdel(Const.CAPTCHA,key);
    }
}

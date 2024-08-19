package com.wanli.security;

import cn.hutool.json.JSONUtil;
import com.wanli.common.Result;
import com.wanli.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    //JwtUtil 工具类对象
    @Autowired
    private JwtUtil jwtUtil;

    //authentication 蚕食，验证成功会将登录输入的用户信息（用户名，密码）传入到 authentication
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        ServletOutputStream out = response.getOutputStream();

        //登录成功获得登录成功的用户数据
        // SpringSecurity 登录成功后会将用户数据封装到 authentication 对象中，用 getName() 可以获取登陆成功的用户的用户名
        String username = authentication.getName();
        String token = jwtUtil.createToken(username);

        //产生 token ，需要将 token 加入响应头，发回 vue 端
        //设置响应头 将产生的 token 响应给前端
        response.setHeader(jwtUtil.getHeader(), token);
        log.info("token==={}", token);


        //登录成功，响应给前端结果 JSON 对象
        Result success = Result.success("验证成功");
        log.info("验证成功");

        out.write(JSONUtil.toJsonStr(success).getBytes("UTF-8"));

        out.flush();
        out.close();
    }
}

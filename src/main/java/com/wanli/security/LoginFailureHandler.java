package com.wanli.security;

import cn.hutool.json.JSONUtil;
import com.wanli.common.Result;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RescaleOp;
import java.io.IOException;

@Slf4j
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        //验证失败，进行什么代码
        response.setContentType("application/json;charset=utf-8");
        ServletOutputStream out = response.getOutputStream();

        //验证失败，返回前端失败的 Result 对象
        Result fail = Result.fail(e.getMessage().equals("Bad credentials")?"用户名或密码错误":e.getMessage());
        log.info("验证失败");

        //前端程序只能接受 JSON 结果。SpringMVC 直接返回 java 对象，自动转换为 JSON 对象。现在是在 SpringSecurity 中，无法自动完成转换
        out.write(JSONUtil.toJsonStr(fail).getBytes("UTF-8"));

        out.flush();
        out.close();
    }
}

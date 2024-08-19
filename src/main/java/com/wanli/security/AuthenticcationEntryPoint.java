package com.wanli.security;

import cn.hutool.json.JSONUtil;
import com.wanli.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class AuthenticcationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        //验证 JWT 失败的处理代码
        log.info("Jwt 认证失败处理器执行....");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        ServletOutputStream out = response.getOutputStream();

        Result fail = Result.fail("JWT 验证失败，请重新登录");
        out.write(JSONUtil.toJsonStr(fail).getBytes("UTF-8"));

        out.flush();
        out.close();
    }
}

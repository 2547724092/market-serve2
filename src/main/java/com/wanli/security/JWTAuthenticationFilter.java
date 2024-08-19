package com.wanli.security;

import cn.hutool.core.util.StrUtil;
import com.wanli.entity.User;
import com.wanli.service.UserService;
import com.wanli.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.TreeSet;

@Slf4j
public class JWTAuthenticationFilter extends BasicAuthenticationFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //验证 JWT 令牌代码
        //获得前端请求头中的 token
        String jwt = request.getHeader("token");
        log.info("JWT 校验过滤器执行，JWT={}",jwt);

        //判断 jwt 是否为空
        //if(StringUtils).isBlank) 方法只能判断 JAVA 的字符串
        //前端传递字符串，1.字符串为空  2.字符串 undefined 判断 JS 字符串传递的值
        if(StrUtil.isBlankOrUndefined(jwt)){
            //后台系统：/api/captcha、/login、/logout 如果请求时这些白名单路径，jwt 就是空
            chain.doFilter(request,response);
            return;
        }
        Claims claims = jwtUtil.getClaimsToken(jwt);
        if(claims == null){
            throw new JwtException("Token 解析异常");
        }
        if(jwtUtil.isTokenExpired(claims)){
            throw new JwtException("Token 已经过期");
        }

        //从解析对象中，获取 token 负载信息中的 username
        String username = claims.getSubject();
        log.info("Jwt 验证成功，用户{}---正在访问后台系统",username);

        //JWT 验证成功，加载当前用户权限信息
        User user = userService.getUserByUsername(username);

        //需要将权限信息封装成 UsernamePasswordAuthenticationToken
        //传递参数3个：用户名，null，权限集合

        //权限集合的获取
        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(username,null,userDetailsService.getUserAuthority(user.getId()));
        SecurityContextHolder.getContext().setAuthentication(upat);

        chain.doFilter(request,response);
    }
}

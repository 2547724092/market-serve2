package com.wanli.security;

import com.wanli.security.UserAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    CaptchaFilter captchaFilter;

    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Autowired
    LoginSuccessHandler loginSucessHandler;

    @Autowired
    UserDetailsService userDetailsService;  //查询用户详细信息

    @Autowired
    AuthenticcationEntryPoint  authenticcationEntryPoint;

    @Autowired
    UserAccessDeniedHandler userAccessDeniedHandler;

    @Autowired
    LogoutSucessHandler logoutSucessHandler;

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter() throws Exception{
        return new JWTAuthenticationFilter(authenticationManager());
    }

    @Bean
    public BCryptPasswordEncoder cryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //SpringSecurity 安全验证，登录之后才能获得 token。并不是所有路径都有进行验证
    //定义白名单
    public static final String[] URL_WHITELIST = {
            "/api/captcha",
            "/login",
            "/logout",
    };

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //配置的代码:采用链式编程
        http.cors().and().csrf().disable()
                .formLogin()  //开启 vue 的登录界面进行登录，不使用 SpringSecurity 提供的登录界面
                .failureHandler(loginFailureHandler)  //登录验证失败处理器的配置
                .successHandler(loginSucessHandler)

                .and()
                .logout()
                .logoutSuccessHandler(logoutSucessHandler)

                //authorizeRequests() 对所有 URL 进行 SpringSecurity 验证
                //antMatchers() 设置请求放行的规则，规则就是白名单
                //permitAll() 对所有人都进行该规则
                //anyRequest() 表示匹配任意请求的 URL 请求
                //authenticated() 任意请求都必须被 SpringSecurity 验证之后才能正常访问
                .and()
                .authorizeRequests()
                .antMatchers(URL_WHITELIST).permitAll()
                .anyRequest().authenticated()

                /*
                该项目是前后端分离的，所及不采用 session 会话技术。禁用 session
                sessionManagement() 设置 session 的配置
                sessionCreationPolicy() 设置 session 使用规则
                  .STATELESS
                */
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                //配置异常处理器
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticcationEntryPoint)
                .accessDeniedHandler(userAccessDeniedHandler)   //配置权限不足处理器

                .and()
                .addFilter(jwtAuthenticationFilter())  //配置 JWT 验证的过滤器
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

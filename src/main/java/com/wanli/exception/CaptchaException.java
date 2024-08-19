package com.wanli.exception;


import org.springframework.security.core.AuthenticationException;

//验证验证码失败的异常
public class CaptchaException extends AuthenticationException {
    public CaptchaException(String message){
        super(message);
    }
}

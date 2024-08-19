package com.wanli.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
@ConfigurationProperties(prefix = "token.jwt")
public class JwtUtil {
    private long expire;
    private String secret;
    private String header;

    //生成 JWT 令牌
    public String createToken(String username) {
        //设置过期时间  1、得到当前日期
        Date nowDate = new Date();
        //设置过期日期  getTime() 返回当前时间的毫秒数  1秒 = 1000 毫秒
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);

        //sub setSubject: 面向的用户(谁登录所获得令牌)
        //iat setIssuedAt: 签发的时间
        //exp setExpiration: 过期时间
        //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImlhdCI6MTcxNzM5NTYyOSwiZXhwIjoxNzE4MDAwNDI5fQ.kYkPjKrfPfpbsOb0wWVWHEPzezdrep4iO8fGB8EkwhQ
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    //验证 JWT 是否正确，正确放回 Claims，不正确返回 null
    public Claims getClaimsToken(String jwt) {
        //{sub=username, iat=1717395629, exp=1718000429}
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody();   //给一个秘钥转进行解析，让其转换成 JWT，
    }

    //验证 JTW 是否过期
    public boolean isTokenExpired(Claims claims){
        //判断设置的过期时间是否在当前时间之前
        return claims.getExpiration().before(new Date());
    }
}

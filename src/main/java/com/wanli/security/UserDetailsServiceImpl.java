package com.wanli.security;

import com.wanli.entity.User;
import com.wanli.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeSet;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //直接在该方法中查询用户信息
        User user = userService.getUserByUsername(username);
        if(ObjectUtils.isEmpty(user)){
            throw new UsernameNotFoundException("查询不该到用户数据");
        }

        //如果不是 null ，需要将 user 用户详细信息交给 springSecurity
        //需要将查询出的 User 对象，封装到自定义的 AccountUser 对象中
        //new TreeSet<>() 集合中存储就是查询出的权限信息，一个用户可以具备多个权限（集合）
        return new AccountUser(user.getId(),user.getUsername(),user.getPassword(),getUserAuthority(user.getId()));
    }

    //获得登录用户的权限字符串，封装返回 List<GrantedAuthority>
    public List<GrantedAuthority> getUserAuthority(Long userId){
        //查询，用户权限字符串
        String userAuthorityInfo = userService.getUserAuthorityInfo(userId);

        //将权限字符串 转换为 List<GrantedAuthority>
        return AuthorityUtils.commaSeparatedStringToAuthorityList(userAuthorityInfo);
    }
}

package com.wanli.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wanli.entity.Menu;
import com.wanli.entity.Role;
import com.wanli.entity.User;
import com.wanli.mapper.MenuMapper;
import com.wanli.mapper.UserMapper;
import com.wanli.service.MenuService;
import com.wanli.service.RoleService;
import com.wanli.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanli.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Troye
 * @since 2024-05-28
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("username", username);
        User user = userMapper.selectOne(qw);
        return user;
    }

    @Override
    public String getUserAuthorityInfo(Long userId) {
        //调用 getNavMenuIds(userId) 方法可获得该用户所能操作的菜单id
        String authorityString = "";  //权限字符串

        //存储到 Redis 中 key，"granted"+当前登录用户名
        User user = this.getById(userId);
        String key = "granted" + user.getUsername();

        //判断是否有权限字符串，有就直接取
        if (redisUtil.hasKey(key)) {
            authorityString = (String) redisUtil.get(key);
        } else {

            //查询当前用户所具备的角色信息
            QueryWrapper role_qw = new QueryWrapper<>();
            role_qw.inSql("id", "select role_id from sys_user_role where user_id =" + userId);
            List<Role> roles = roleService.list(role_qw);

            if (roles.size() > 0) {
                String roleString = roles.stream().map(r -> "ROLE_" + r.getCode()).collect(Collectors.joining(","));
                authorityString = roleString + ",";
            }

            //查询当前用户具有哪些可操作菜单
            List<Long> menuIds = userMapper.getNavMenuIds(userId);

            //根据菜单编号查询菜单详细数据
            List<Menu> menus = menuService.listByIds(menuIds);

            if (menus.size() > 0) {
                String perms = menus.stream().map(m -> m.getPerms()).collect(Collectors.joining(","));
                authorityString += perms;
//            authorityString = authorityString.concat(perms);
                log.info("权限字符串是:{}", authorityString);
            }

            //将产生的权限字符串存入 redis
            redisUtil.set(key, authorityString);
        }

        return authorityString;

    }

    //根据用户名清除 redis 中存储的该用户的权限字符串

    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuId) {
        //查询与被删除菜单 id 相关的所有用户
        List<User> users = userMapper.listByMenuId(menuId);

        //清除查询的用户的 redis 权限，因为这个用户的权限是和删除惨菜单关的
        users.forEach(user -> {
            clearAuthorityInfo(user.getUsername());
        });
    }

    @Override
    public void clearAuthorityInfo(String username) {
        redisUtil.del("granted" + username);
    }

    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleId) {
        //查询和该角色相关的所有用户
        List<User> list = this.list(new QueryWrapper<User>().inSql("id", "select user_id from sys_user_role where role_id=" + roleId));
        list.forEach(u -> {
            this.clearAuthorityInfo(u.getUsername());
        });
    }
}

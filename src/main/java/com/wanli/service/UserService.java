package com.wanli.service;

import com.wanli.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Troye
 * @since 2024-05-28
 */
public interface UserService extends IService<User> {
    //根据 username 查询用户详细信息
    public User getUserByUsername(String username);

    //根据用户编号获得用户权限数据
    public String getUserAuthorityInfo(Long userId);

    //根据 menuId 清除该用户与该菜单关联的权限信息
    public void clearUserAuthorityInfoByMenuId(Long menuId);

    public void clearAuthorityInfo(String username);

    public void clearUserAuthorityInfoByRoleId(Long roleId);
}

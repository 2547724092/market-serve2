package com.wanli.service;

import com.wanli.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Troye
 * @since 2024-06-03
 */
public interface RoleService extends IService<Role> {
    public List<Role> listRoleByUserId(Long userId);
}

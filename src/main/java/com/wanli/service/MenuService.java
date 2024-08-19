package com.wanli.service;

import com.wanli.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wanli.entity.dto.MenuDto;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Troye
 * @since 2024-06-03
 */
public interface MenuService extends IService<Menu> {
    //根据用户查询数据（根据权限查询）
    public List<MenuDto> getCurrentUserNav(String username);

    //查询管理菜单的表格数据
    public List<Menu> tree();
}

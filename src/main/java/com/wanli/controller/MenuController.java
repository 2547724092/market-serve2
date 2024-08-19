package com.wanli.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wanli.common.Result;
import com.wanli.entity.Menu;
import com.wanli.entity.RoleMenu;
import com.wanli.entity.User;
import com.wanli.entity.dto.MenuDto;
import com.wanli.service.MenuService;
import com.wanli.service.RoleMenuService;
import com.wanli.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.wanli.common.BaseController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Troye
 * @since 2024-06-03
 */
@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleMenuService roleMenuService;

    @GetMapping("/list")
    public Result list() {
        List<Menu> tree = menuService.tree();
        return Result.success(tree);
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable Long id) {
        Menu menu = menuService.getById(id);
        return Result.success(menu);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Menu menu) {
        //设置更新时间
        menu.setUpdated(LocalDateTime.now());
        menuService.updateById(menu);
        return Result.success("菜单更新成功");
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public Result delete(@PathVariable Long id) {
        //删除菜单下存在子菜单，则删除该菜单下的所有子菜单
        int count = menuService.count(new QueryWrapper<Menu>().eq("parent_id", id));
        if (count > 0) {
            return Result.fail("亲先删除该菜单下的子菜单");
        } else {
            userService.clearUserAuthorityInfoByMenuId(id);

            menuService.removeById(id);

            roleMenuService.remove((new QueryWrapper<RoleMenu>().eq("menu_id",id)));
            return Result.success("菜单删除成功");
        }
    }

    @PostMapping("/save")
    public Result save(@RequestBody Menu menu) {  // JSON 对象通过注解转换成 java 对象
        //设置新建菜单的创建时间
        menu.setCreated(LocalDateTime.now());
        menuService.save(menu);
        return Result.success("菜单保存成功");
    }

    @GetMapping("/nav")
    public Result nav(Principal principal) {
        String username = principal.getName();  //从 security 中封装的用户对象中取用户名
        List<MenuDto> MenuDtoList = menuService.getCurrentUserNav(username);

        User user = userService.getUserByUsername(username);  //根据当前登录用户查询用户权限数据
        String userAuthorityInfo = userService.getUserAuthorityInfo(user.getId());  //权限字符串
        String[] authorities = StringUtils.tokenizeToStringArray(userAuthorityInfo, ",");

        return Result.success(MapUtil.builder().put("nav", MenuDtoList).put("authorities", authorities).map());

    }
}

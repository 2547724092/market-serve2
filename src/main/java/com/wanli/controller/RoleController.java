package com.wanli.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.prism.impl.shape.ShapeUtil;
import com.wanli.common.Result;
import com.wanli.entity.Menu;
import com.wanli.entity.Role;
import com.wanli.entity.RoleMenu;
import com.wanli.entity.UserRole;
import com.wanli.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.wanli.common.BaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Troye
 * @since 2024-06-03
 */
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private UserService userService;

    @GetMapping("/tree")
    public Result tree(){
        List<Role> list = roleService.list();
        return Result.success(list);
    }

    @Transactional
    @PostMapping("/perms/{roleId}")
    public Result perms(@PathVariable Long roleId, @RequestBody Long[] menuIds){
        List<RoleMenu> roleMenuList = new ArrayList<>();
        Arrays.stream(menuIds).forEach(mid->{
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(mid);
            roleMenuList.add(rm);
        });
        //跟新权限前先删除原有的权限
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("role_id",roleId));

        //直接批量保存 关联表集合对象
        roleMenuService.saveBatch(roleMenuList);

        //清除与该角色相关的所有用户的 redis 权限字符串
        userService.clearUserAuthorityInfoByRoleId(roleId);

        return Result.success("角色权限信息修改成功");
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable Long id) {    //{id} 是一个动态变量
        Role role = roleService.getById(id);
        List<RoleMenu> roleMenuList = roleMenuService.list(new QueryWrapper<RoleMenu>().eq("role_id", id));

        //循环关联表，取出 roleMenu 对象中的 menuId ，形成一个新的集合，这个集合就是树形控件现实的权限
        List<Long> menuIds = roleMenuList.stream().map(rm -> rm.getMenuId()).collect(Collectors.toList());

        role.setMenuIds(menuIds);
        return Result.success(role);
    }


    @PostMapping("/save")
    public Result save(@RequestBody Role role) {   //@RequestBody：处理 JSON 或 XML 格式的请求体数据，将其转换为 Java 对象。
        role.setCreated(LocalDateTime.now());
        roleService.save(role);
        return Result.success("角色添加成功");
    }

    @PostMapping("/update")
    public Result update(@RequestBody Role role) {
        //设置更新时间
        role.setUpdated(LocalDateTime.now());
        roleService.updateById(role);
        return Result.success("角色数据更新成功");
    }

    @PostMapping("/delete")
    @Transactional
    public Result delete(@RequestBody Long[] ids) {   //@RequestBody：将请求体中的 JSON 数据转换为 Java 对象
        //删除相关 id 的角色
        boolean b = roleService.removeByIds(Arrays.asList(ids));  //asList：将传入的数组转换为一个List对象

        //同步删除 sys_user_role 表
        userRoleService.remove(new QueryWrapper<UserRole>().in("role_id", ids));

        //同步删除 sys_role_menu 表
        roleMenuService.remove(new QueryWrapper<RoleMenu>().in("role_id", ids));

        return Result.success("删除角色成功");
    }

    @GetMapping("/list")
    public Result list(String name) {
        //不需要获取参数，直接调用 BaseController 中的 getPage() 方法
        //MyBatis page()分页查询
        //Page<Role> page = roleService.page(getPage());
        //返回的 Page 对象中，包含 records 属性，该属性包括角色集合

        //若 name 有值，使用模糊查询，查询分页表格数据。反正就添加分页查询
        Page<Role> page = roleService.page(getPage(), new QueryWrapper<Role>().like(StrUtil.isNotBlank(name), "name", name));
        return Result.success(page);
    }
}

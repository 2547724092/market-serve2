package com.wanli.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanli.common.Result;
import com.wanli.config.Const;
import com.wanli.entity.Role;
import com.wanli.entity.User;
import com.wanli.entity.UserRole;
import com.wanli.service.RoleService;
import com.wanli.service.UserRoleService;
import com.wanli.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.wanli.common.BaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;   //加密密码

    //保存分配角色
    @PostMapping("/role/{id}")
    public Result role(@PathVariable Long id, @RequestBody Long[] roleIds) {
        //保存角色的信息，保存 sys_user_role 表
        //定义 关系表对象集合
        List<UserRole> userRoleList = new ArrayList<>();
        Arrays.stream(roleIds).forEach(roleId -> {
            UserRole ur = new UserRole();
            ur.setUserId(id);
            ur.setRoleId(roleId);

            userRoleList.add(ur);
        });

        //保存，先清楚用户现有的角色关联信息
        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", id));

        //录入新设置的角色信息
        userRoleService.saveBatch(userRoleList);

        //用户角色改变了，权限也改变了，需要清除 redis 中该用户的权限字符串
        User user = userService.getById(id);
        userService.clearAuthorityInfo(user.getUsername());

        return Result.success("角色分配成功");
    }

    //重置密码
    @PostMapping("/repass/{id}")
    public Result repass(@PathVariable Long id) {
        User user = userService.getById(id);
        user.setPassword(passwordEncoder.encode(Const.DEFAULT_PASSWORD));  //设置密码为 666666
        user.setUpdated(LocalDateTime.now());

        userService.updateById(user);
        return Result.success("账号密码重置成功");
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable Long id) {   //地址栏传参
        User user = userService.getById(id);
        List<Role> roles = roleService.listRoleByUserId(user.getId());
        user.setRoles(roles);

        return Result.success(user);
    }

    @PostMapping("/save")
    public Result save(@RequestBody User user) {  // 直接将前端的参数 user 传到后端
        user.setCreated(LocalDateTime.now());

        //录入用户，判断头像是否输入。若 没输入，则取默认密码
        if (StrUtil.isBlank(user.getAvatar())) {
            user.setAvatar(Const.DEFAULT_AVATAR);
        }

        //录入用户，判断密码是否输入。若 没输入，则取默认密码
        if (StrUtil.isBlank(user.getPassword())) {
            user.setPassword(Const.DEFAULT_PASSWORD);
        }

        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);

        userService.save(user);
        return Result.success("创建用户成功");
    }

    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        user.setUpdated(LocalDateTime.now());
        if (StrUtil.isBlank(user.getPassword())) {
            String password = passwordEncoder.encode(user.getPassword());
            user.setPassword(password);
        }
        userService.updateById(user);
        return Result.success("用户数据更新成功");
    }

    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/list")
    public Result list(String name) {
        Page<User> page = userService.page(getPage(), new QueryWrapper<User>().like(StrUtil.isNotBlank(name), "username", name));

        //查询出user分页数据，user对象中没有roles是null。
        page.getRecords().forEach(u -> {
            u.setRoles(roleService.listRoleByUserId(u.getId()));
        });

        return Result.success(page);
    }

    @PreAuthorize("hasAuthority('sys:user:delete')")
    @PostMapping("/delete")
    @Transactional
    public Result delete(@RequestBody Long[] ids) {
        //同步删除用户和角色的关系 sys_user_role
        userRoleService.remove(new QueryWrapper<UserRole>().in("user_id", ids));

        //删除用户
        userService.removeByIds(Arrays.asList(ids));   //Arrays.asList(ids)：数组变集合
        return Result.success("用户删除成功");
    }

}

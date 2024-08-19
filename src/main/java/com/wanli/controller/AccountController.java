package com.wanli.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanli.common.Result;
import com.wanli.config.Const;
import com.wanli.entity.Account;
import com.wanli.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.wanli.common.BaseController;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Troye
 * @since 2024-06-19
 */
@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {
    @Autowired
    private AccountService accountService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/save")
    public Result save(@RequestBody Account account){
        account.setCreated(LocalDateTime.now());
        if(StrUtil.isBlank(account.getAccountImg())){
            account.setAccountImg(Const.DEFAULT_AVATAR); //设置账户头像为默认
        }

//        录入账户，密码是否有输入，如果没有输入，就取默认密码
        if (StrUtil.isBlank(account.getPassword())){
            //设置密码为默认密码
            account.setPassword(Const.DEFAULT_PASSWORD);
        }
//        页面提交明文，录入数据库中 用户密码需要SpringSecurity加密
        String password = passwordEncoder.encode(account.getPassword());
        account.setPassword(password);

        accountService.save(account);
        return Result.success("账户创建成功");
    }

    @PostMapping("/update")
    public Result update(@RequestBody Account account){
        account.setUpdated(LocalDateTime.now());
        if(StrUtil.isBlank(account.getPassword())){
            String password = passwordEncoder.encode(account.getPassword());
            account.setPassword(password);
        }
        accountService.updateById(account);
        return Result.success("账户数据更新成功");
    }

    @PreAuthorize("hasAuthority('sys:account:list')")
    @GetMapping("/list")
    public Result list(String name){   //传的参数有name，就模糊查询
        //Page<User> page = userService.page(getPage(), new QueryWrapper<User>().like(StrUtil.isNotBlank(name), "username", name));
        //下面这一句的作用是分页查询带搜索功能
        //like(StrUtil.isNotBlank(name), "name", name) 作用是如果name有值就加上模糊查询，如果name没有值就不加
        Page<Account> page = accountService.page(getPage(), new QueryWrapper<Account>().like(StrUtil.isNotBlank(name), "account_name", name));
        return Result.success(page);
    }

    @PostMapping("/delete")
    @Transactional
    public Result delete(@RequestBody Long[] ids){
        //同步删除  用户和角色的关系 sys_user_role
//        userRoleService.remove(new QueryWrapper<UserRole>().in("user_id",ids));
        //删除用户
        accountService.removeByIds(Arrays.asList(ids));
        return Result.success("删除账户操作成功");
    }

    //    根据账户id查询该账户信息
    @GetMapping("/info/{accountId}")
    public Result info(@PathVariable Long accountId){
        //查询出该id账户详细信息
        Account account = accountService.getById(accountId);
        return Result.success(account);
    }
}

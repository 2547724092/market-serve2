package com.wanli.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanli.common.Result;
import com.wanli.entity.Account;
import com.wanli.entity.Business;
import com.wanli.entity.Comment;
import com.wanli.entity.Favorite;
import com.wanli.service.AccountService;
import com.wanli.service.BusinessService;
import com.wanli.service.CommentService;
import com.wanli.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.wanli.common.BaseController;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Troye
 * @since 2024-06-22
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController extends BaseController {
    @Autowired
    FavoriteService favoriteService;

    @Autowired
    BusinessService businessService;

    @Autowired
    AccountService accountService;

    //删除评论信息
    @PostMapping("/delete")
    @Transactional
    public Result delete(@RequestBody Long[] ids) {   //@RequestBody：将请求体中的 JSON 数据转换为 Java 对象
        //删除相关 id 的角色
        boolean b = favoriteService.removeByIds(Arrays.asList(ids));  //asList：将传入的数组转换为一个List对象

        return Result.success("删除商品成功");
    }

    //查询商品信息的方法
    @GetMapping("/list")
    public Result list(String accountName, String businessName) {
        // 模糊查询并获取分页对象
        QueryWrapper<Favorite> qw = new QueryWrapper<>();
        if (StrUtil.isNotBlank(accountName)) {
            qw.inSql("account_id", "SELECT account_id FROM sys_account WHERE account_name LIKE '%" + accountName + "%'");
        }
        if (StrUtil.isNotBlank(businessName)) {
            qw.inSql("business_id", "SELECT business_id FROM sys_business WHERE business_name LIKE '%" + businessName + "%'");
        }

        // 获取分页对象
        Page<Favorite> page = favoriteService.page(getPage(), qw);

        // 获取评论列表
        List<Favorite> favoriteList = page.getRecords();

        // 填充商家名称和用户名称
        for (Favorite favorite : favoriteList) {
            Business business = businessService.getById(favorite.getBusinessId());
            if (business != null) {
                favorite.setBusiness(business);
            }

            Account account = accountService.getById(favorite.getAccountId());
            if (account != null) {
                favorite.setAccount(account);
            }
        }

        // 返回分页对象
        return Result.success(page);
    }
}

package com.wanli.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanli.common.Result;
import com.wanli.entity.Orders;
import com.wanli.entity.Ordersdetailet;
import com.wanli.entity.User;
import com.wanli.service.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.wanli.common.BaseController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author joy
 * @since 2024-06-18
 */
@RestController
@RequestMapping("/orders")
public class OrdersController extends BaseController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private BusinessService businessService;
    @Autowired
    private DeliveryaddressService deliveryaddressService;
    @Autowired
    private OrdersdetailetService ordersdetailetService;
    @Autowired
    private GoodsService goodsService;

    // 分页查询
    @GetMapping("/list")
    public Result list(@RequestParam String accountName, @RequestParam String businessName, @RequestParam Integer state, @RequestParam String beginTime, @RequestParam String endTime) {
        QueryWrapper<Orders> qw = new QueryWrapper<>();
        qw
                .inSql(StringUtils.isNotBlank(accountName), "account_id", "SELECT account_id FROM sys_account WHERE account_name LIKE '%" + accountName + "%'")
                .inSql(StringUtils.isNotBlank(businessName), "business_id", "SELECT business_id FROM sys_business WHERE business_name LIKE '%" + businessName + "%'")
                .eq(state != -1, "state", state);
        if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
            LocalDateTime parseBegin = LocalDateTime.parse(beginTime, DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime parseEnd = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME);
            qw.between("created", parseBegin, parseEnd);
        }


        Page<Orders> page = ordersService.page(getPage(), qw);

        page.getRecords().forEach(o -> {
            o.setAccount(accountService.getById(o.getAccountId()));
            o.setBusiness(businessService.getById(o.getBusinessId()));
            o.setDeliveryaddress(deliveryaddressService.getById(o.getDaId()));
        });

        return Result.success(page);
    }

    // 订单详情
    @GetMapping("/info/{ordersId}")
    public Result info(@PathVariable Long ordersId) {
        Orders orders = ordersService.getById(ordersId);
        List<Ordersdetailet> odList
                = ordersdetailetService.list(new QueryWrapper<Ordersdetailet>().eq("order_id", orders.getOrderId()));
        odList.forEach(od -> {
            od.setGoods(goodsService.getById(od.getGoodsId()));
        });

        orders.setAccount(accountService.getById(orders.getAccountId()));
        orders.setBusiness(businessService.getById(orders.getBusinessId()));
        orders.setDeliveryaddress(deliveryaddressService.getById(orders.getDaId()));
        orders.setOdList(odList);
        return Result.success(orders);
    }

    // 删除订单
    @PostMapping("/delete")
    @Transactional
    public Result delete(@RequestBody Long[] ids){
        // 删除订单明细表
        ordersdetailetService.remove(new QueryWrapper<Ordersdetailet>().in("order_id", ids));

        // 删除订单表
        ordersService.removeByIds(Arrays.asList(ids));
        return Result.success("订单删除成功");
    }


}

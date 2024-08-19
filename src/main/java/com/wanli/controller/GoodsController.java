package com.wanli.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanli.common.Result;
import com.wanli.entity.*;
import com.wanli.service.BusinessService;
import com.wanli.service.GoodsService;
import com.wanli.service.OrdersService;
import com.wanli.service.OrdersdetailetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.wanli.common.BaseController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Troye
 * @since 2024-06-18
 */
@RestController
@RequestMapping("/goods")
public class GoodsController extends BaseController {
    @Autowired
    private GoodsService goodsService;

    @Autowired
    private BusinessService businessService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrdersdetailetService ordersdetailetService;


    //添加商品信息
    @PostMapping("/save")
    public Result save(@RequestBody Goods goods) {   //@RequestBody：处理 JSON 或 XML 格式的请求体数据，将其转换为 Java 对象。
        goods.setCreated(LocalDateTime.now());
        goodsService.save(goods);
        return Result.success("商品添加成功");
    }


    //更新商品信息
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        //设置更新时间
        goods.setUpdated(LocalDateTime.now());
        goodsService.updateById(goods);
        return Result.success("商品数据更新成功");
    }


    //删除商品信息
    @PostMapping("/delete")
    @Transactional
    public Result delete(@RequestBody Long[] ids) {   //@RequestBody：将请求体中的 JSON 数据转换为 Java 对象
        //删除相关 id 的角色
        boolean b = goodsService.removeByIds(Arrays.asList(ids));  //asList：将传入的数组转换为一个List对象

        return Result.success("删除商品成功");
    }


    //查询商品具体信息的方法
    @GetMapping("/info/{id}")  ////{id} 是一个动态变量
    public Result info(@PathVariable Integer id) {
        Goods goods = goodsService.getById(id);

        //根据商家 id 查询商家名称，显示
        Business business = businessService.getById(goods.getBusinessId());
        if (business != null) {
            goods.setBusiness(business);
        }

        return Result.success(goods);
    }


    //查询商品信息的方法
    @GetMapping("/list")
    public Result list(String goodsName, String businessName) {
        // 模糊查询并获取分页对象
        QueryWrapper<Goods> qw = new QueryWrapper<>();
        qw.inSql(StrUtil.isNotBlank(businessName), "business_id", "SELECT business_id FROM sys_business WHERE business_name LIKE '%" + businessName + "%'")
                .like(StrUtil.isNotBlank(goodsName), "goods_name", goodsName);
        Page<Goods> page = goodsService.page(getPage(), qw);

        // 获取商品列表
        List<Goods> goodsList = page.getRecords();

        // 遍历商品列表并填充商家名称
        for (Goods goods : goodsList) {
            Business business = businessService.getById(goods.getBusinessId());
            if (business != null) {
                goods.setBusiness(business);
            }
        }

        // 返回分页对象
        return Result.success(page);
    }
}

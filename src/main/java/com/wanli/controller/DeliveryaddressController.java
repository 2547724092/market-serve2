package com.wanli.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wanli.common.Result;
import com.wanli.entity.Deliveryaddress;
import com.wanli.service.DeliveryaddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wanli.common.BaseController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Troye
 * @since 2024-06-19
 */
@RestController
@RequestMapping("/deliveryaddress")
public class DeliveryaddressController extends BaseController {
    @Autowired
    private DeliveryaddressService deliveryaddressService;

    @GetMapping("/list/{accountId}")
    public Result list(@PathVariable String accountId){

        List<Deliveryaddress> list = deliveryaddressService.list(new QueryWrapper<Deliveryaddress>().eq("account_id", accountId));
        return Result.success(list);
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody Long daId){
        deliveryaddressService.removeById(daId);

        return Result.success("地址删除成功");
    }
}

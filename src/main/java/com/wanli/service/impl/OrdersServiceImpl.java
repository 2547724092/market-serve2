package com.wanli.service.impl;

import com.wanli.entity.Category;
import com.wanli.entity.Orders;
import com.wanli.mapper.OrdersMapper;
import com.wanli.service.OrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Troye
 * @since 2024-06-18
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}

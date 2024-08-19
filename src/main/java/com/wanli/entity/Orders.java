package com.wanli.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author joy
 * @since 2024-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单编号
     */
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    /**
     * 下单用户编号--sys_account表account_id
     */
    @TableField("account_id")
    private String accountId;

    /**
     * 商家编号--sys_business表business_id
     */
    @TableField("business_id")
    private Long businessId;

    /**
     * 订购日期
     */
    @TableField("created")
    private LocalDateTime created;

    /**
     * 订单总价
     */
    @TableField("order_total")
    private BigDecimal orderTotal;

    /**
     * 送货地址编号--sys_deliveryaddress表da_id
     */
    @TableField("da_id")
    private Integer daId;

    /**
     * 订单状态（0：未支付； 1：已支付）
     */
    @TableField("state")
    private Integer state;

    /**
     * 订单更新时间
     */
    @TableField("updated")
    private LocalDateTime updated;

    //用户的详情信息 多对一
    @TableField(exist = false)
    private Account account;
    //商家的详情信息 多对一
    @TableField(exist = false)
    private Business business;
    //配送的详情信息 多对一
    @TableField(exist = false)
    private Deliveryaddress deliveryaddress;
    //订单明细的详情数据 一对多
    @TableField(exist = false)
    private List<Ordersdetailet> odList;
}

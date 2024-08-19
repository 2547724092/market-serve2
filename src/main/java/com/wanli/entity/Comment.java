package com.wanli.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wanli.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author Troye
 * @since 2024-06-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_comment")
public class Comment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 评论id
     */
    @TableId(value = "co_id", type = IdType.AUTO)
    private Integer coId;

    /**
     * 用户编号
     */
    @TableField("account_id")
    private String accountId;

    /**
     * 商家id
     */
    @TableField("business_id")
    private Long businessId;

    /**
     * 订单编号
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 评分
     */
    @TableField("rate")
    private Double rate;

    /**
     * 评价内容
     */
    @TableField("co_text")
    private String coText;

    /**
     * 评论图片
     */
    @TableField("co_img")
    private String coImg;

    //添加商家名称字段
    @TableField(exist = false)
    private Business business;

    //添加用户名称字段
    @TableField(exist = false)
    private Account account;
}

package com.wanli.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wanli.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author wangluwei
 * @since 2024-06-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_business")
public class Business extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 商家编号
     */
    @TableId(value = "business_id", type = IdType.AUTO)
    private Long businessId;

    /**
     * 商家名称
     */
    @TableField("business_name")
    private String businessName;

    /**
     * 商家地址
     */
    @TableField("business_address")
    private String businessAddress;

    /**
     * 商家介绍
     */
    @TableField("business_explain")
    private String businessExplain;

    /**
     * 商家图片（base64）
     */
    @TableField("business_img")
    private String businessImg;

    /**
     * 起送费
     */
    @TableField("star_price")
    private BigDecimal starPrice;

    /**
     * 配送费
     */
    @TableField("delivery_price")
    private BigDecimal deliveryPrice;

    /**
     * 逻辑删除
     */
    @TableField("del_tag")
    private Integer delTag;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;

    //自定义实体类 添加存储商家餐品分类的集合
    @TableField(exist = false)
    private List<Category> categoryList;
    @TableField(exist = false)
    private List<Business> businesses;
}

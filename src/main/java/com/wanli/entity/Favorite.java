package com.wanli.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wanli.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("sys_favorite")
public class Favorite extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏id
     */
    @TableId(value = "favorite_id", type = IdType.AUTO)
    private Long favoriteId;

    /**
     * 收藏的用户
     */
    @TableField("account_id")
    private String accountId;

    /**
     * 被收藏商家
     */
    @TableField("business_id")
    private Long businessId;

    //添加商家名称字段
    @TableField(exist = false)
    private Business business;

    //添加用户名称字段
    @TableField(exist = false)
    private Account account;
}

package com.wanli.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanli.entity.Category;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wangluwei
 * @since 2024-06-18
 */
public interface CategoryService extends IService<Category> {
    //根据商家ID 查询该商家的所有商品类别
    public List<Category> listCategoryByBusinessId(Long businessId);

    public List<Category> tree();
}

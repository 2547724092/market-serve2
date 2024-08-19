package com.wanli.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanli.entity.Category;
import com.wanli.mapper.CategoryMapper;
import com.wanli.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wangluwei
 * @since 2024-06-18
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> listCategoryByBusinessId(Long businessId) {
        List<Category> list = this.list(new QueryWrapper<Category>().inSql("category_id","select category_id from sys_business_category where business_id = "+businessId));
        return list;
    }

    @Override
    public List<Category> tree() {
        List<Category> list = this.list();
        return list;
    }
}

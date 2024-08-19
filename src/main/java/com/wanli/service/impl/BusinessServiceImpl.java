package com.wanli.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wanli.entity.Business;
import com.wanli.entity.Category;
import com.wanli.mapper.BusinessMapper;
import com.wanli.service.BusinessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Troye
 * @since 2024-06-19
 */
@Service
public class BusinessServiceImpl extends ServiceImpl<BusinessMapper, Business> implements BusinessService {




    @Override
    public List<Business> listBusinessByCategoryId(Long categoryId) {
        List<Business> list = this.list(new QueryWrapper<Business>().inSql("business_id","select business_id from sys_business_category where category_id = "+categoryId));
        return list;
    }
}

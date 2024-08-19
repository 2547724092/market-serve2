package com.wanli.service;

import com.wanli.entity.Business;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wanli.entity.Category;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Troye
 * @since 2024-06-19
 */
public interface BusinessService extends IService<Business> {
    public List<Business> listBusinessByCategoryId(Long categoryId);

}

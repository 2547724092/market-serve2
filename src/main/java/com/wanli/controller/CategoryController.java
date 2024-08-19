package com.wanli.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanli.common.Result;
import com.wanli.config.Const;
import com.wanli.entity.*;
import com.wanli.service.BusinessCategoryService;
import com.wanli.service.BusinessService;
import com.wanli.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.wanli.common.BaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BusinessService businessService;

    @Autowired
    private BusinessCategoryService businessCategoryService;
    // 在服务器端根据食品分类ID查询该分类下的所有商家信息
    @GetMapping("/businesses/{categoryId}")
    public Result aaaaaa(@PathVariable Long categoryId){
        List<Business> businessList = businessService.listBusinessByCategoryId(categoryId);
        System.out.println(businessList+"aaaaaaaaaa");

        return Result.success(businessList);
    }



    @GetMapping("/list/")
    public Result list(){
        List<Category> tree = categoryService.tree();
        return Result.success(tree);
    }

    @GetMapping("/list2")
    public Result list2(String categoryName){
        //该方法不需要定义接收分页的参数，因为BaseController中接收了，并且封装成Page，只需要调用getPage()
        //MyBatisPlus  page()分页查询
        //Page<Role> page = roleService.page(getPage());
        //返回Page对象中，包含records属性，该属性中包括 分页角色集合，还有其它相关的分页参数。

        //name 如果有值，使用模糊查询 查询分页表格数据。 如果没有值，就添加模糊查询。
        Page<Category> page = categoryService.page(getPage(),new QueryWrapper<Category>().like(StrUtil.isNotBlank(categoryName),"category_name",categoryName));
        return Result.success(page);
    }
    @PostMapping("/delete")
    @Transactional
    public Result delete(@RequestBody Long[] ids){
        //同步删除  用户和角色的关系 sys_user_role
//        userRoleService.remove(new QueryWrapper<UserRole>().in("user_id",ids));
        //删除用户
        categoryService.removeByIds(Arrays.asList(ids));
        return Result.success("删除分类操作成功");
    }
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result save(@RequestBody Category category){
        category.setCreated(LocalDateTime.now());
        if(StrUtil.isBlank(category.getCategoryCover())){
            category.setCategoryCover(Const.DEFAULT_AVATAR);  //设置用户头像为默认头像
        }

        categoryService.save(category);
        return Result.success("分类创建成功");
    }
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result update(@RequestBody Category category){
        category.setUpdated(LocalDateTime.now());
        categoryService.updateById(category);
        return Result.success("分类数据更新成功");
    }
    //根据用户id查询该用户信息
    @GetMapping("/info/{categoryId}")
    public Result info(@PathVariable Long categoryId){
        //查询出该id用户详细信息
        Category category = categoryService.getById(categoryId);

        return Result.success(category);
    }
}

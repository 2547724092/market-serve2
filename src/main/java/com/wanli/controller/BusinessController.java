package com.wanli.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanli.common.Result;
import com.wanli.config.Const;
import com.wanli.entity.Business;
import com.wanli.entity.BusinessCategory;
import com.wanli.entity.Category;
import com.wanli.service.BusinessCategoryService;
import com.wanli.service.BusinessService;
import com.wanli.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.wanli.common.BaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Troye
 * @since 2024-06-19
 */
@RestController
@RequestMapping("/business")
public class BusinessController extends BaseController {
    @Autowired
    private BusinessService businessService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BusinessCategoryService businessCategoryService;

    //删除商家ID为0的 sys_business_category表中数据
    @PostMapping("/deleteId")
    public Result del(){
        businessCategoryService.remove(new QueryWrapper<BusinessCategory>().eq("business_id",0));
        return Result.success("删除商家ID为0的关联数据");
    }
    //编辑商家信息 ： 通过上传的餐品类别名称，查找具体餐品信息，并结合当前商家ID，添加到sys_business_category表，写在save方法中
    @PostMapping("/saveCategory/{businessId}")
    @Transactional
    public Result saveCategory(@PathVariable Long businessId, @RequestBody String[] categoryNames){
        List<BusinessCategory> list = new ArrayList<>();
        //数组转集合
        Arrays.stream(categoryNames).forEach(categoryName ->{
            BusinessCategory bc = new BusinessCategory();
            bc.setBusinessId(businessId);
            categoryService.getById(categoryName);
            //查询类别名称对应的具体信息
            Category category = categoryService.getOne(new QueryWrapper<Category>().eq("category_name",categoryName));
            //取出categoryId
            Long categoryId = category.getCategoryId();
            //存入对象
            bc.setCategoryId(categoryId);
            list.add(bc);
        });
        //先删除该商家的所有餐品种类信息
        businessCategoryService.remove(new QueryWrapper<BusinessCategory>().eq("business_id",businessId));
        //删除添加的商家ID为0的数据
        businessCategoryService.remove(new QueryWrapper<BusinessCategory>().eq("business_id",0));
        //批量保存 关联表集合对象 加入上传的businessId-categoryIds
        businessCategoryService.saveBatch(list);

        return Result.success(list);
    }
    //查看餐品分类信息 用于商家模块 选择餐品分类信息
    @GetMapping("/listCategory")
    public Result list(){
        List<Category> list = categoryService.list();
        return Result.success(list);
    }
    //新增商家 根据选择餐品名称 查询餐品具体信息 存入
    @PostMapping("/listCategoryByName")
    public Result listCategoryByCategoryName(@RequestBody String[] categoryNames){
        Business business = new Business();
        List<Category> categoryList = new ArrayList<>();
        //数组转集合
        Arrays.stream(categoryNames).forEach(categoryName ->{
            categoryService.getById(categoryName);
            //查询类别名称对应的具体信息
            Category category = categoryService.getOne(new QueryWrapper<Category>().eq("category_name",categoryName));
            //存入对象 categoryList
            categoryList.add(category);
        });
        business.setCategoryList(categoryList);
        return Result.success(business);
    }
    //服务器端 新建商家
    @PostMapping("/save")
    @Transactional
    public Result save(@RequestBody Business business){
        //设置创建时间和更新时间
        business.setCreated(LocalDateTime.now());
        business.setUpdated(LocalDateTime.now());
        business.setDelTag(1);
        //若未上传商家图片，设置默认图片
        if (StrUtil.isBlank(business.getBusinessImg())){
            business.setBusinessImg(Const.DEFAULT_BUSINESS_IMG);
        }
        //添加到business表
        businessService.save(business);
        Business latestBusiness =businessService.getOne(new QueryWrapper<Business>().eq("business_name",business.getBusinessName()));
        Long latestId = latestBusiness.getBusinessId();
        //添加到business_category关联表
        BusinessCategory businessCategory = new BusinessCategory();
        business.getCategoryList().forEach(c->{
            businessCategory.setCategoryId(c.getCategoryId());
            businessCategory.setBusinessId(latestId);
            businessCategoryService.save(businessCategory);
        });
        //删除添加的商家ID为0的数据
        businessCategoryService.remove(new QueryWrapper<BusinessCategory>().eq("business_id",0));
        return Result.success("新的商家添加成功！");
    }
    //服务器端 更新商家信息
    @PostMapping("/update")
    public Result update(@RequestBody Business business){
        business.setUpdated(LocalDateTime.now());
        businessService.updateById(business);
        return Result.success("商家信息更新成功");
    }
    //服务器端 根据商家ID查询该商家详细信息
    @GetMapping("/info/{businessId}")
    public Result info(@PathVariable Long businessId){
        Business business = businessService.getById(businessId);
        //查询该商家的所有食品分类
        List<Category> categoryList = categoryService.listCategoryByBusinessId(businessId);
        System.out.println(categoryList+"aaaaaaaaaa");
        business.setCategoryList(categoryList);

        return Result.success(business);
    }
    //服务器端 根据商家ID删除商家
    @PostMapping("/delete")
    @Transactional
    public Result delete(@RequestBody Long[] ids){
        //同步删除 商家和商品分类的关系 sys_business_category
        businessCategoryService.remove(new QueryWrapper<BusinessCategory>().in("business_id",ids));
        businessCategoryService.remove(new QueryWrapper<BusinessCategory>().eq("business_id",0));
        //再删除商家 数组转集合
        businessService.removeByIds(Arrays.asList(ids));
        return Result.success("商家删除成功");
    }
    //服务器端 请求获得商家信息表格数据
    @GetMapping("/list")
    //传入搜索框中商家名称 模糊搜索
    public Result list(String name){
        Page<Business> page = businessService.page(getPage(),new QueryWrapper<Business>().like(StrUtil.isNotBlank(name),"business_name",name));
        //为自定义属性categoryList赋值 对象中没有category分类数据则为null
        page.getRecords().forEach( b ->{
            b.setCategoryList(categoryService.listCategoryByBusinessId(b.getBusinessId()));
        });
        return Result.success(page);
    }

    //取商家名字
    @GetMapping("/data")
    public Result data(){
        List<Business> business = businessService.list();
        return  Result.success(business);
    }
}

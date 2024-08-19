package com.wanli.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanli.util.RedisUtil;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BaseController {
    @Autowired
    protected HttpServletRequest request;

    //Redis 数据库操作对象
    @Autowired
    protected RedisUtil redisUtil;

    //将获取的前端分页参数数据封装成对象
    //Page 对象是 MyBatis 提供专门封装分页参数的一个类
    public Page getPage() {
        //获取前端请求提交的分页参数：current、size    request.getParameter("current");
        try {
            Integer current = ServletRequestUtils.getIntParameter(request, "current");
            Integer size = ServletRequestUtils.getIntParameter(request, "size");
            return new Page(current, size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}

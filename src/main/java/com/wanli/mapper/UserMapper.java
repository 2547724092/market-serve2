package com.wanli.mapper;

import com.wanli.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {
    //自定义：根据用户编号查询用户所能操作的菜单 id(集合)
    public List<Long> getNavMenuIds(Long userId);

    //查询与删除菜单id权限相关的所有用户数据
    public List<User> listByMenuId(Long menuId);
}

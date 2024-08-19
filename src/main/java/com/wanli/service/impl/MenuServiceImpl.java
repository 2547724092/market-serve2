package com.wanli.service.impl;

import com.wanli.entity.Menu;
import com.wanli.entity.User;
import com.wanli.entity.dto.MenuDto;
import com.wanli.mapper.MenuMapper;
import com.wanli.mapper.UserMapper;
import com.wanli.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanli.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Troye
 * @since 2024-06-03
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    //查询菜单管理界面的表格数据
    @Override
    public List<Menu> tree() {
        List<Menu> list = this.list();
        List<Menu> treeMenu = this.buildTreeMenu(list);
        return treeMenu;
    }

    @Override
    public List<MenuDto> getCurrentUserNav(String username) {
        //根据username 查询该用户详细信息
        User user = userService.getUserByUsername(username);

        List<Long> menuIds = userMapper.getNavMenuIds(user.getId());

        List<Menu> menus = this.listByIds(menuIds);   //根据菜单 id 查询所有菜单信息

        List<Menu> finalMenu = buildTreeMenu(menus);

        return convert(finalMenu);
    }

    /* 查询出菜单集合中所有子菜单数据 */
    private List<Menu> buildTreeMenu(List<Menu> menus){
        List<Menu> finalMenu = new ArrayList<>();
        for(Menu m : menus){
            //寻找子菜单
            for(Menu e : menus){
                if(e.getParentId() == m.getId()){
                    //当前循环 e 菜单 就是 m 的子菜单
                    m.getChildren().add(e);
                }
            }
            if(m.getParentId() == 0l){
                //一级菜单，直接加入最终菜单
                finalMenu.add(m);
            }
        }
        return finalMenu;
    }

     /* 将 Menu 数据转为 MenuDto */
    private List<MenuDto> convert(List<Menu> menus){
        List<MenuDto> menuDtoList = new ArrayList<>();
        menus.forEach(m -> {
            MenuDto dto = new MenuDto();
            dto.setId(m.getId());
            dto.setTitle(m.getName());
            dto.setName(m.getPerms());
            dto.setIcon(m.getIcon());
            dto.setPath(m.getPath());
            dto.setComponent(m.getComponent());
            if(m.getChildren().size() > 0){
                //m 的子菜单对象 <Menu> 泛型，也需要将子菜单几个钟 Menu 转化为 MenuDto，可以使用递归调用实现转换
                dto.setChildren(convert(m.getChildren()));
            }
            menuDtoList.add(dto);
        });
        return menuDtoList;
    }
}

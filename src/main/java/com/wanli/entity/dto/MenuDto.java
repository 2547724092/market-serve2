package com.wanli.entity.dto;

import com.wanli.entity.Menu;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuDto {
    private Long id;
    private String title;
    private String icon;
    private String path;
    private String name;  //菜单类中的 perms 权限编码
    private String component;

    private List<MenuDto> children = new ArrayList<>();

}

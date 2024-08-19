package com.wanli.common;

import lombok.Data;

import java.time.LocalDateTime;

//所有实体类的父类
@Data
public class BaseEntity {
    private LocalDateTime created;
    private LocalDateTime updated;
    private Integer statu;
}

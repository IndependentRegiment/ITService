package com.zt.config;

import org.springframework.stereotype.Component;

/*
 * 功能描述: <br>
 * 〈角色〉
 * @Author:
 * @Date:
 */

public enum RoleEnum {
    LEADER("领导"),
    ADMINISTRATOR("系统管理员"),
    OPERATOR("话务员"),
    ENGINEER("工程师"),
    USER("普通用户");

    private String name;
    RoleEnum(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
}

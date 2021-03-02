package com.zt.config;

public enum UserEnum {
    NEW("新用户"),
    OLD("老用户");

    private String name;
    UserEnum(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }
}

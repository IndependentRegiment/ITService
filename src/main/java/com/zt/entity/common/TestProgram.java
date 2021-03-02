package com.zt.entity.common;

import org.apache.catalina.User;

import java.util.ArrayList;
import java.util.List;

public class TestProgram {

    public static void main(String[] args) {
        List<UserInfo> userInfos = new ArrayList<>();
        UserInfo user = new UserInfo();
        userInfos.add(user);
        user.setOpenId("222222222222");
        userInfos.forEach(data->{
            System.out.println(data.getOpenId());
        });
    }
}

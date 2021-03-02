package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Getter
@Setter
@Repository
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel
public class UserStatus implements Serializable {

    private int id; // 用户状态id
    private String openId; // 用户id
    private String statusName; // 用户状态
    private Integer statusId; // 状态id
    private String roleName; // 用户角色

    public UserStatus(String roleName) {
        this.roleName = roleName;
    }
}

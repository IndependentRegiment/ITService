package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ApiModel
@ToString
public class UserRole implements Serializable {

    private int id;  // 用户角色表id
    private String userId; // 用户id
    private int roleId; // 用户角色Id
    private String roleName; // 用户角色名称
}

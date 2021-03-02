package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/*
 * 功能描述: <br>
 * 〈权限表〉
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@ToString
public class PermissionInfo implements Serializable {

    private Integer id; // 权限id
    private String permissionName; // 权限名称
    private String description; // 权限描述
}

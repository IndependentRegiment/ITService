package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/*
 * 功能描述: <br>
 * 〈角色表〉
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
public class RoleInfo implements Serializable {

    private Integer id; // 角色Id
    private String roleName; // 角色名称
    private String description; // 角色描述
}

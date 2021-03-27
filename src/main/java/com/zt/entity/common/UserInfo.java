package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.sql.Timestamp;

/*
 * 功能描述: <br>
 * 〈用户信息〉:
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel
public class UserInfo implements Serializable {

    private String openId; // 用户openId
    private String userName; // 用户昵称
    private Timestamp authentication; // 用户验证
    private String department; // 部门
    private String remark; // 备注
    private String company; // 公司
    private String userId; // 用户id
    private String statusName; // 用户状态
    private Integer statusId; // 状态id
    private Integer roleId; // 角色id;
    private String roleName; // 角色名称

    public UserInfo(String openId) {
        this.openId = openId;
    }
}

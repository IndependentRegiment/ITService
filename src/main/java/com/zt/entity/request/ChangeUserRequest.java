package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Repository
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangeUserRequest implements Serializable {

    private String openId; // 用户openId
    private String userName; // 用户名称
    private Integer deptId; // 部门id
    private String remark; // 备注
    private Integer companyId; // 公司id
    private String userId; // 用户工号
    private String phone; // 用户工作电话
    private String email; // 用户工作邮箱
}

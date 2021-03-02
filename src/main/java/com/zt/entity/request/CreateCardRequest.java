package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
/*
 * 功能描述: <br>
 * 〈创建或代建工单请求类〉
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateCardRequest {

    private String openId; // 当前登录用户openId
    private String userName; // 用户姓名
    private Integer priority; // 紧急度
    private Integer problemType; // 故障类型
    private Date createTime; // 故障单创建时间
    private String phone;  // 工单创建人电话
    private int deptId; //  工单创建人部门id
    private String description; // 故障描述
    private String appointmentCreate; // 预约开始时间
    private String appointmentEnd; // 预约结束时间
    private String place; // 故障放生地点
    private String deal; //  补单工程师openId
}

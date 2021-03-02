package com.zt.entity.common;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.Date;

/*
 * 功能描述: <br>
 * 〈工单详情〉
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
public class JobCardDetail {

    private String cardId; // 工单号
    private String applyName; // 工单创建人姓名
    private int priority; // 工单紧急度
    private int problemId; // 故障类型id
    private String problemType; // 故障类型
    private String assistName; // 协办人
    private String status; // 工单状态
    private String reason; // 故障原因
    private String solution; // 故障解决方案
    private String satisfaction; // 用户满意度
    private String comment; // 用户评价
    private String createTime; // 工单创建时间
    private String endTime; // 工单完成时间
    private String processNode; // 工单流程节点
    private String distribution; // 故障分配人
    private String deal; // 故障处理人
    private String change; // 故障类型变更人
    private String description; // 故障备注
    private String construction; // 工单代建人
    private String phone; // 工单创建人电话
    private String deptName; // 工单创建人部门
    private int waitCount; // 等待数量
    private String appointmentCreate; // 预约开始时间
    private String appointmentEnd; // 预约结束时间
    private String place; // 故障发生地点
    private String dealWay;  // 服务方式
    private int wayId;  // 服务方式id
    private String mobliePhone; // 申请人手机号码
}
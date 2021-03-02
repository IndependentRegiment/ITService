package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

/*
 * 功能描述: <br>
 * 〈工单表〉
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
public class JobCard {

    private String cardId;  // 工单Id
    private String applyId; // 工单创建人Id
    private String applyName; // 工单创建人姓名
    private Integer priority; // 紧急度
    private Integer problemType; // 故障类型Id
    private String assistId; // 协办人openId
    private int statusId; // 状态Id;
    private String reason; // 故障原因
    private String solution; // 故障解决方案
    private String satisfaction; // 用户满意度
    private String comment; // 用户评论
    private String createTime; // 工单创建时间
    private String endTime; // 工单完成时间
    private Integer processNode; // 工单流程节点Id
    private String distribution; // 工单分配人openId
    private String deal; // 工单处理人openId
    private String changeId; // 工单变更人openId
    private String description; // 故障描述
    private String construction; // 工单代建人openId
    private String phone;  // 工单创建人电话
    private int deptId; // 部门Id
    private int waitCount; // 等待数量
    private Timestamp appointmentCreate; // 预约开始时间
    private Timestamp appointmentEnd; // 预约结束时间
    private String place; // 故障发生地点
    private Integer dealWay; // 服务方式
    private String dealName; // 工程师
    private String mobilePhone; // 申请人手机号码
}
package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
public class CommonRequest {

    private String openId;   // 用户openid
    private Integer typeId;  // 故障类型id
    private Integer roleId;  // 用户角色id
    private Integer oldRoleId; // 原用户角色id
    private Integer countId; // 工单总数按时间筛选id
    private Integer timeId;  // 报表时间筛选天数
    private Integer month;  // 月份数
    private Integer deptId; // 部门id
    private Integer pageNo = 1; // 当前页数
    private Integer pageSize = 10; // 每页显示数量
    private Integer year; // 年份数 2020、2021
    private Integer quarter;  // 季度 1、2、3、4
    private String filePath; // 需要上传下载的文件路径
    private Integer serviceId; // 服务id
}

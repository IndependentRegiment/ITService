package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;
/*
 * 功能描述: <br>
 * 〈查看个人信息及工单统计请求类〉
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SeeCardRequest {
    private String openId; // 登录用户OpenId
    private String userRole; // 用户角色
    private String cardType; // 列表类型
    private int pageNo = 1; // 查看的页数
    private int startNo; // 分页起始位置
    private int pageSize = 10; // 每页显示数量
    private String applyName; // 申请人姓名
    private String createTime; // 工单创建时间
    private String endTime; // 工单结束时间
    private Integer problemType;  // 故障类型
    private String status; // 工单状态
}

package com.zt.entity.response;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;
/*
 * 功能描述: <br>
 * 〈个人信息及工单统计〉
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
public class UserCardResponse {

    private String openId; // 当前用户openid
    private String userName; // 姓名
    private String userRole; // 用户角色
    private int totalCount = 0; // 工单总数
    private int createCount; // 代建或申请创建的工单数
    private int finishCount; // 已完成的工单数
    private int updateCount; // 修改的工单数
    private int evaluationCount; // 评价的工单数
    private int untreateCount = 0; // 未处理的工单数
    private int treateCount = 0; // 处理的工单数
    private int assistCount; // 协办的工单数

    public UserCardResponse(String userName) {
        this.userName = userName;
    }
}

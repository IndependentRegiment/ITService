package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;
/*
 * 功能描述: <br>
 * 〈请求协办人列表〉
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AssistInfoRequest {

    private String openId;  // 用户openId
    private Integer problemType; //故障类型id
    private String userRole; // 协办角色
    private String deal; // 修改工单工程师  原工程师名字
}

package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;
/*
 * 功能描述: <br>
 * 〈修改故障单请求〉
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangeCardRequest {

    private String openId; // 用户openId
    private String cardId; // 故障单Id
    private String reason; // 故障原因
    private String solution; // 故障解决方案
    private String assist; // 协办人openId
    private Integer problemType; // 故障类型Id
    private Integer wayId; // 服务方式id
    private String deal; // 工程师openId
}

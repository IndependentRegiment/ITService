package com.zt.entity.response;

import com.zt.entity.common.UserStatus;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.security.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ApiModel
@ToString
public class HandCardResponse {

    private String cardId; // 手工单号
    private Integer cardCount; // 单数
    private String fillTime; // 填写时间
    private String openId; // 填写人openId;
    private String fillName; // 填写人姓名
    private String description; // 手工单备注
}

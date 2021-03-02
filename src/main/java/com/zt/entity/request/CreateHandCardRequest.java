package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
public class CreateHandCardRequest {

    private String cardId; // 手工单号
    private Integer cardCount; // 单数
    private String fillTime; // 填写时间
    private String openId; // 填写人openId;
    private String description; // 手工单备注
}

package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
@ApiModel
public class HandCard {

    private String cardId; // 手工单号
    private Integer cardCount; // 单数
    private Timestamp fillTime; // 填写时间
    private String openId; // 填写人openId;
    private String fillName; // 填写人姓名
    private String description; // 手工单备注
}

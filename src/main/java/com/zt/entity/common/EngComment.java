package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Repository
@ApiModel
public class EngComment {

    private String engName; // 工程师
    private Integer quarter; // 第几季度
    private Integer part = 0; // 平均满意度比值
    private Integer count = 0; // 评价单数
}

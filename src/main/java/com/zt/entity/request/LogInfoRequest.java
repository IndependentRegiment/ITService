package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
public class LogInfoRequest {

    private Integer pageNo = 1;    // 起始页数
    private Integer pageSize = 10; // 每页显示量
    private String createTime;  // 筛选开始时间
    private String endTime;     // 筛选结束时间
    private String operatorName; // 操作人
    private String operatorType; // 操作类型
    private String operatorResult; // 操作结果
}

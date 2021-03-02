package com.zt.entity.response;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
@ApiModel
public class UserCommentCountResponse {

    private List<String> timeList;    // 筛选时间列表
    private List<Integer> countList;  // 工单量列表
    private List<Integer> partList;   // 工单里百分比列表
    private Integer totalCount;       // 总工单量
    private Integer quarterPart;      // 季度满意度
    private Integer yearPart;         // 年度满意度
}

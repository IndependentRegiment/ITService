package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
@ApiModel
public class ProblemCardCount {

    private Integer deptId;  // 部门id
    private String deptName; // 部门名称
    private Integer total; // 故障单数量

    private Integer problemType; // 故障类型id
    private Integer totalCount; // 工单总数
    private String problemName; // 故障类型名称
    private Integer count; // 该故障类型工单数所占比例

    public ProblemCardCount(String problemName, Integer count) {
        this.problemName = problemName;
        this.count = count;
    }

    public ProblemCardCount(String deptName, Integer total, String problemName, Integer totalCount) {
        this.deptName = deptName;
        this.total = total;
        this.totalCount = totalCount;
        this.problemName = problemName;
    }
}

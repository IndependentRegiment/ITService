package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@ApiModel
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
public class DeptProblem {

    private String deptName; // 部门名称
    private List<Integer> countList;  // 部门对应故障类型工单数列表
}

package com.zt.entity.response;

import com.zt.entity.common.DeptProblem;
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
public class DeptProblemCountResponse {

    private List<DeptProblem> deptProblems;
    private List<String> problems;
    private Integer totalCount = 0;
}

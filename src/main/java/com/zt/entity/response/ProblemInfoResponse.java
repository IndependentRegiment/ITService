package com.zt.entity.response;

import com.zt.entity.common.ProblemInfo;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Repository
@ApiModel
@ToString
public class ProblemInfoResponse {

    private Integer typeId; // 故障类型Id
    private String typeName; //故障类型名称
    private Integer parentId; // 父级类型id
    private Integer typeNo; // 排序id
    private List<ProblemInfoResponse> problemList = new ArrayList<>();  // 二、三级类型列表
}

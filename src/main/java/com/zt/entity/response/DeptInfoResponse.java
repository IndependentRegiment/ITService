package com.zt.entity.response;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Repository
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@ToString
public class DeptInfoResponse {

    private int deptId; // 部门编号
    private String deptName; // 部门名称
    private int parentId; // 部门父级id
    private List<DeptInfoResponse> deptList = new ArrayList<>();
}

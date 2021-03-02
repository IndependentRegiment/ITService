package com.zt.entity.common;

import lombok.*;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/*
 * 功能描述: <br>
 * 〈部门表〉
 * @Param:
 * @Return:
 * @Author:
 * @Date:
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
public class DeptInfo implements Serializable {

    private int deptId; // 部门编号
    private String deptName; // 部门名称
    private Integer parentId; // 部门父级id
}

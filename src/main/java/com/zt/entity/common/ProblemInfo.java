package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * 功能描述: <br>
 * 〈故障类型〉
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel
public class ProblemInfo implements Serializable {

    private Integer typeId; // 故障类型Id
    private String typeName; //故障类型名称
    private Integer parentId; // 父级类型id
    private Integer typeNo; // 排序编号
    private Integer serviceId; // 服务id

    public ProblemInfo(Integer typeId) {
        this.typeId = typeId;
    }

    public ProblemInfo(String typeName) { this.typeName = typeName; }
}

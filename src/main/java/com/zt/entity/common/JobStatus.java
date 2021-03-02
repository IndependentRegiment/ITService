package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@Repository
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel
public class JobStatus {

    private int id;  // 状态id
    private String statusName; // 状态名称
}

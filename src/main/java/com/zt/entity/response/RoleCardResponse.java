package com.zt.entity.response;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ApiModel
@ToString
public class RoleCardResponse {

    private String title; // 工单目录名称
    private int count; // 工单目录下工单数量
}

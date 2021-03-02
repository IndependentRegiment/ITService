package com.zt.entity.common;

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
public class ServiceWay {

    private int wayId;   // 服务方式id
    private String wayName; // 服务方式名称
}

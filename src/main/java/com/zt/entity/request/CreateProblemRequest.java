package com.zt.entity.request;

import io.swagger.models.auth.In;
import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
public class CreateProblemRequest {

    private Integer serviceId; // 故障服务类型id
    private Integer typeId; // 故障类型id
    private String typeName; // 故障名称
}

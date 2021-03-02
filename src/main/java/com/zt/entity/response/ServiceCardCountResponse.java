package com.zt.entity.response;

import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
@ApiModel
public class ServiceCardCountResponse {

    private String serviceName;
    private Integer count;
}

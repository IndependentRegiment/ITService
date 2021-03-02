package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
public class RoleCardRequest {

    private String openId; // 用户openId
    private Integer typeId; // 故障类型id
}

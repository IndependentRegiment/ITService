package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@Repository
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangeStatusRequest {

    private String openId;  // 用户id
    private int statusId; // 状态id
}

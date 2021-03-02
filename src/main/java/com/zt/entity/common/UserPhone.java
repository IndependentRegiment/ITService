package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
@ApiModel
public class UserPhone {

    private String openId; // 用户openId
    private String mobilePhone; // 用户手机号码
    private String userName; // 用户姓名
    private String email; // 邮箱
}

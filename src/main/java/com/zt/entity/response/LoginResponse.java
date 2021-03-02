package com.zt.entity.response;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;
/*
 * 功能描述: <br>
 * 〈登录获取openId响应〉
 * @Author:
 * @Date:
 */
@Repository
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel
@ToString
public class LoginResponse {

    private String openId;
    private String sessionKey;
    private String unionId;
}

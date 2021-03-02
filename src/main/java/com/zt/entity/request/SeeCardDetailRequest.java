package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;
/*
 * 功能描述: <br>
 * 〈故障单详情请求〉
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SeeCardDetailRequest {

    private String openId; // 用户openId
    private String cardId; // 故障单号
}

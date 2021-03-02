package com.zt.entity.common;

import lombok.*;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/*
 * 功能描述: <br>
 * 〈协办人信息〉
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AssistInfo implements Serializable {

    private String openId;  // 协办人openId
    private String assistName; // 协办人姓名
}

package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;
/*
 * 功能描述: <br>
 * 〈用户满意度及评论〉
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentCardRequest {

    private String openId; // 用户openId
    private String cardId; // 故障单Id
    private String satisfaction; // 满意度
    private String comment; // 评论内容
}

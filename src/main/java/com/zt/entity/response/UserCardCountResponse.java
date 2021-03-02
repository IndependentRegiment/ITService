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
@ApiModel
@ToString
public class UserCardCountResponse {

    private List<String> userList;  // 工程师列表
    private List<Integer> countList; // 工单数列表
    private List<Integer> unCountList; // 非评价工单数占比
    private List<Integer> commentList; // 满意度占比
    private Integer totalCount = 0;  // 总系统工单数
}

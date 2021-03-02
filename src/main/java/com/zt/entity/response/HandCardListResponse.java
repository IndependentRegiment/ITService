package com.zt.entity.response;

import com.zt.entity.common.UserStatus;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ApiModel
@ToString
public class HandCardListResponse {

    private List<HandCardResponse> responseList = new ArrayList<>(); // 用户工程师列表
    private UserStatus userStatus; // 用户状态
}

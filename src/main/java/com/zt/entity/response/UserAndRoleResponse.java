package com.zt.entity.response;

import com.zt.entity.common.UserInfo;
import io.swagger.annotations.ApiModel;
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
public class UserAndRoleResponse {

    private List<UserInfo> userList;
    private Integer totalCount = 0;
}

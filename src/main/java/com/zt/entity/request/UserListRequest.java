package com.zt.entity.request;

import lombok.*;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
public class UserListRequest {

    private String roleName;
    private String userName;
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}

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
public class UserDept {

    private String userName;
}

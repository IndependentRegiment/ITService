package com.zt.entity.response;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ApiModel
@ToString
public class CardCountResponse {

    private String roleName;
    private String userName;
    private int id; // 用户状态id;
    private String statusName; // 用户状态
    private List<RoleCardResponse> roleList = new ArrayList<>();

    public CardCountResponse(String roleName) {
        this.roleName = roleName;
    }
    public CardCountResponse(int id) { this.id = id; }
}

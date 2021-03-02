package com.zt.entity.response;

import com.zt.entity.common.ProblemCardCount;
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
public class ProblemCardCountResponse {

    private List<ProblemCardCount> problemList;
    private Integer totalCount = 0;
}

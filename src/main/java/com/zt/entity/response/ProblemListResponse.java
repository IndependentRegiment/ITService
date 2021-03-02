package com.zt.entity.response;

import com.zt.entity.common.ProblemInfo;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * 功能描述: <br>
 * 〈故障类型列表〉
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@ToString
public class ProblemListResponse {

    private List<ProblemInfo> problemList;
}

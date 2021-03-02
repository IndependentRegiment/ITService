package com.zt.entity.response;

import com.zt.entity.common.AssistInfo;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * 功能描述: <br>
 * 〈协办人列表〉
 * @Param:
 * @Return:
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
public class AssistListResponse {

    private List<AssistInfo> assistList;
}

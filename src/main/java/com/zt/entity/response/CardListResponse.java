package com.zt.entity.response;

import com.zt.entity.common.JobCardDetail;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 * 功能描述: <br>
 * 〈工单列表〉
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
public class CardListResponse {

    private List<JobCardDetail> cardList;
    private int totalCount = 0;
}

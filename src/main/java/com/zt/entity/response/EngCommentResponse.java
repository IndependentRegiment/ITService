package com.zt.entity.response;

import com.zt.entity.common.EngComment;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Getter
@Setter
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ToString
public class EngCommentResponse {

    private List<EngComment> engComments;
    private Integer totalCount = 0;
}

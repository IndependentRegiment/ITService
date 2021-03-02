package com.zt.entity.response;

import com.zt.entity.common.LogInfo;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@ToString
public class LogInfoResponse {

    private List<LogInfo> logInfos;  // 日志信息列表
    private Integer totalCount = 0;  // 日志总量
}

package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/*
 * 功能描述: <br>
 * 〈日志信息〉
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
public class LogInfo implements Serializable {

    private String logId; // 日志Id
    private Timestamp createTime; // 日志创建时间
    private String operatorId; // 操作人openId
    private String operatorName; // 操作人姓名
    private String operatorType; // 操作类型 增删改查
    private String content; // 操作内容
    private String operatorResult; // 操作结果

    public LogInfo(String operatorId, String operatorType, String content, String operatorResult) {
        this.operatorId = operatorId;
        this.operatorType = operatorType;
        this.content = content;
        this.operatorResult = operatorResult;
    }
}

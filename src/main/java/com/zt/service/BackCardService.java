package com.zt.service;

import com.zt.entity.common.LogInfo;
import com.zt.entity.request.LogInfoRequest;
import com.zt.entity.response.LogInfoResponse;

public interface BackCardService {

    // 新增日志操作
    void addLog(LogInfo logInfo);

    // 查看日志内容
    LogInfoResponse getLogInfos(LogInfoRequest request);

}

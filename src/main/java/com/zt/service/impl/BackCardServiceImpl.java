package com.zt.service.impl;

import com.zt.dao.CardMapper;
import com.zt.dao.UserMapper;
import com.zt.entity.common.LogInfo;
import com.zt.entity.common.UserInfo;
import com.zt.entity.request.LogInfoRequest;
import com.zt.entity.response.LogInfoResponse;
import com.zt.service.BackCardService;
import com.zt.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BackCardServiceImpl implements BackCardService {

    private Logger log = LoggerFactory.getLogger(BackCardServiceImpl.class);

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void addLog(LogInfo logInfo) {
        //String logId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String logId = String.valueOf(System.currentTimeMillis());
        Timestamp timeStamp = DateUtil.getTimeStamp();
        logInfo.setLogId(logId);
        logInfo.setCreateTime(timeStamp);
        if (logInfo.getOperatorId() != null) {
            UserInfo userInfo = userMapper.getUserInfo(logInfo.getOperatorId());
            if (null != userInfo) {
                logInfo.setOperatorName(Optional.ofNullable(userInfo.getUserName()).orElse("未登记姓名"));
            }
        }
        try {
            cardMapper.addLog(logInfo);
        } catch (Exception ex) {
            log.info("add log info:{} failed:{}", logInfo, ex);
        }
    }

    @Override
    public LogInfoResponse getLogInfos(LogInfoRequest request) {
        LogInfoResponse response = new LogInfoResponse();

        if ((request.getCreateTime() != null && !"".equals(request.getCreateTime()))
                && (request.getEndTime() != null && !"".equals(request.getEndTime()))
                && request.getCreateTime().equals(request.getEndTime())) {
            request.setCreateTime(request.getCreateTime() + " 00:00:00");
            request.setEndTime(request.getEndTime()+ " 23:59:59");
        }
        Integer pageStart = (request.getPageNo() - 1) * request.getPageSize();
        request.setPageNo(pageStart);
        List<LogInfo> logInfos = cardMapper.getLogInfos(request);
        if (null != logInfos && 0 != logInfos.size()) {
            response.setTotalCount(logInfos.size());
            response.setLogInfos(logInfos);
            return response;
        }
        response.setLogInfos(new ArrayList<>());
        return response;
    }
}

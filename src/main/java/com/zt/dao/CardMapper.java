package com.zt.dao;

import com.zt.entity.common.*;
import com.zt.entity.request.CommonRequest;
import com.zt.entity.request.LogInfoRequest;
import com.zt.entity.request.SeeCardRequest;

import java.util.List;

public interface CardMapper {

    // 根据openId获取用户所有工单
    List<JobCard> getUserCardList(String openId);

    // 获取所有工单
    List<JobCard> getAllCard(SeeCardRequest request);

    // 分页查询 每页显示5条
    List<JobCard> getUserCardPage(String openId, int start, int size);

    // 根据问题id获取故障类型
    List<ProblemInfo> getAllProblemInfo(int problemId);

    // 根据节点id获取流程节点
    ProcessInfo getProcessInfo(int nodeId);

    // 创建故障单
    void createCard(JobCard card);

    // 统计工单数 by construction
    int getCardCountById(String construction);

    // 获取工单详情
    JobCard getUserCardDetail(String cardId);

    // 修改工单
    void updateCardByEng(JobCard jobCard);

    // 获取协办人列表
    List<UserInfo> getAssistUser(CommonRequest request);

    // 获取故障类型对应服务
    TypeService getServiceById(int typeId);

    // 获取1小时之内的用户创建的工单数量
    int getCardCount(JobCard card);

    // 获取服务方式
    List<ServiceWay> getServiceWay(int wayId);

    // 获取手工单列表
    List<HandCard> getHandCardList(String openId);

    // 创建手工单
    void createHandCard(HandCard handCard);

    // 获取工程师状态
    List<UserStatus> getEngStatus(String openId);

    // 新增故障类型
    void createProblem(ProblemInfo problemInfo);

    // 删除故障类型
    void deleteProblem(ProblemInfo problemInfo);

    // 统计当天、本月、今年工单总数
    List<JobCard> getAllCardCount(CommonRequest request);

    // 统计top5 故障类型工单量
    List<ProblemCardCount> getProblemCardCount(CommonRequest request);

    // 统计各部位工单量最多的故障类型
    List<ProblemCardCount> getDeptCardCount(CommonRequest request);

    // 获取问题服务类型列表
    List<ProblemService> getProblemService();

    // 修改故障类型排序
    void updateProblemNo(ProblemInfo problemInfo);

    // 获取故障类型列表根据probleminfo
    List<ProblemInfo> getProblemInfoByAll(ProblemInfo problemInfo);

    // 统计工程师满意度
    List<JobCard> getEngComment(CommonRequest request);

    // 新增日志操作
    void addLog(LogInfo logInfo);

    // 查看日志内容
    List<LogInfo> getLogInfos(LogInfoRequest request);
}

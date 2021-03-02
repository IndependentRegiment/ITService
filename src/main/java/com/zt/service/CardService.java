package com.zt.service;

import com.zt.entity.common.*;
import com.zt.entity.request.*;
import com.zt.entity.response.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CardService {

    // 获取故障单列表
    CardListResponse getCardList(SeeCardRequest request);

    // 创建故障单
    int createJobCard(CreateCardRequest request);

    // 获取所有的故障类型
    List<ProblemInfoResponse> getProblemList(CommonRequest request);

    // 获取工单详情
    JobCardDetail getJobCardInfo(SeeCardDetailRequest request);

    // 修改工单信息 工程师处理
    List<JobCardDetail> updateCardInfo(ChangeCardRequest request);

    // 获取协办人列表
    List<UserInfo> getAssistList(AssistInfoRequest request);

    // 修改工单信息 用户评价
    List<JobCardDetail> updateCardByComment(CommentCardRequest request);

    // 工程师接收工单
    List<JobCardDetail> receiveOneCard(ChangeCardRequest request);

    // 获取服务方式
    List<ServiceWay> getServiceWayList();

    // 获取手工单列表
    HandCardListResponse getHandCardList(CommonRequest request);

    // 创建手工单
    int createOneHandCard(CreateHandCardRequest handCard);

    // 获取工程师状态
    UserStatus getEngStatusInfo(CommonRequest request);

    // 获取用户提交消息提示内容
    List<String> getContent(CommonRequest request);

    // 获取所有工单
    CardListResponse getAllCard(SeeCardRequest request);

    // 修改故障单信息
    int updateCardEng(ChangeCardRequest request);

    // 创建故障类型
    int createProblem(CreateProblemRequest request);

    // 删除故障类型
    int deleteProblem(CommonRequest request);

    // 统计当天所有工单数、待处理及已处理工单数
    UserCardResponse getCardCountInfo(CommonRequest request);

    // 统计当天处理系统工单总量TOP5的工程师
    UserCardCountResponse getUserCardCount(CommonRequest request);

    // 统计评价的工单占比
    UserCommentCountResponse getUserCommentCount(CommonRequest request);

    // 统计各部门评价工单数粘占比
    UserCardCountResponse getDeptCommentCount(CommonRequest request);

    // 统计top5 故障类型工单量
    ProblemCardCountResponse getProblemCountInfo(CommonRequest request);

    // 统计各部位工单量最多的故障类型
    DeptProblemCountResponse getDeptCardCount(CommonRequest request);

    // 获取问题服务类型列表
    List<ProblemService> getProblemService();

    // 获取用户满意 、一般、 差、未评价占比
    List<CommentPart> getCommentPart(CommonRequest request);

    // 修改故障类型排序
    int updateProblemTypeNo(ProblemInfo problemInfo);

    // 统计服务类型占比
    List<ServiceCardCountResponse> getServiceCount(CommonRequest request);

    // 获取待处理工单数
    CardListResponse getUnDealCardList(CommonRequest request);

    // 统计工程师满意度
    EngCommentResponse getEngComment(CommonRequest request);

    // 发送邮件通知用户给个评价
    int sendEmailForComment(SendCardRequest request);
}

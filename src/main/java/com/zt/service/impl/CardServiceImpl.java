package com.zt.service.impl;

import com.zt.config.ProcessEnum;
import com.zt.config.RoleEnum;
import com.zt.config.StatusEnum;
import com.zt.dao.CardMapper;
import com.zt.dao.DeptMapper;
import com.zt.dao.UserMapper;
import com.zt.entity.common.*;
import com.zt.entity.request.*;
import com.zt.entity.response.*;
import com.zt.service.CardService;
import com.zt.util.DateUtil;
import com.zt.util.ExchangeMailUtil;
import com.zt.util.GetOpenIdUtil;
import com.zt.util.RegUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardService {

    private static Logger log = LoggerFactory.getLogger(CardServiceImpl.class);

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Value("${spring.profiles.active}")
    private String project;  // 部署

    @Override
    public CardListResponse getCardList(SeeCardRequest request) {
        log.info("start get openId:{} card list info service", request.getOpenId());
        CardListResponse cardListResponse = new CardListResponse();


        // get user all card info by openId and pageNo
        int startNo = (request.getPageNo() - 1) * request.getPageSize();
        int endNo = startNo + 10;
        List<JobCard> userCardList1 = cardMapper.getUserCardPage(request.getOpenId(),
                (request.getPageNo() - 1) * request.getPageSize(), request.getPageSize());

        log.info("userCardList:{}", userCardList1);
        List<JobCard> userCardList = new ArrayList<>();
        if ("普通用户".equals(request.getUserRole())) {
            for (JobCard card : userCardList1) {
                if (request.getOpenId().equals(card.getApplyId())) {
                    userCardList.add(card);
                }
            }
        } else if ("工程师".equals(request.getUserRole())) {
            for (JobCard card : userCardList1) {
                if (request.getOpenId().equals(card.getDeal())
                        || request.getOpenId().equals(card.getAssistId())) {
                    userCardList.add(card);
                }
            }
        } else if ("话务员".equals(request.getUserRole())) {
            for (JobCard card : userCardList1) {
                if (request.getOpenId().equals(card.getConstruction())) {
                    userCardList.add(card);
                }
            }
        }
        List<JobCardDetail> cardList = getJobCardDetails(userCardList, request);
        System.out.println("----------------order by createTime" + cardList);
        List<JobCardDetail> collect = cardList.stream().skip(startNo).limit(request.getPageSize()).collect(Collectors.toList());
        System.out.println("----------------by substring");
        cardList.forEach(t1 -> {
            System.out.println(t1);
        });
        log.info("get openId:{} card list info:{} success", request.getOpenId(), collect);
        if (null != cardList && 0 != cardList.size()) {
            cardListResponse.setCardList(collect);
            cardListResponse.setTotalCount(cardList.size());
        } else {
            cardListResponse.setCardList(new ArrayList<>());
            cardListResponse.setTotalCount(0);
        }

        return cardListResponse;
    }

    @Override
    public CardListResponse getAllCard(SeeCardRequest request) {
        if ((null != request.getCreateTime() && !"".equals(request.getCreateTime()))
                && (null != request.getEndTime() && !"".equals(request.getEndTime()))
                && request.getCreateTime().equals(request.getEndTime())) {
            request.setCreateTime(request.getCreateTime() + " 00:00:00");
            request.setEndTime(request.getEndTime() + " 23:59:59");
            log.info("select by createTime:{} and endTime:{}", request.getCreateTime(), request.getEndTime());
        }
        List<JobCard> allCard = cardMapper.getAllCard(request);
        CardListResponse response = new CardListResponse();
        if (null != allCard && allCard.size() != 0) {
            List<JobCard> jobCards = new ArrayList<>();
            if (null != request.getStatus()) {
                if ("未处理".equals(request.getStatus())) {
                    jobCards = allCard.stream().filter(card -> card.getProcessNode().intValue() < 3).collect(Collectors.toList());
                } else if ("处理中".equals(request.getStatus())) {
                    jobCards = allCard.stream().filter(card -> card.getProcessNode().intValue() == 3).collect(Collectors.toList());
                } else if ("已处理".equals(request.getStatus())) {
                    jobCards = allCard.stream().filter(card -> card.getProcessNode().intValue() > 3).collect(Collectors.toList());
                } else if ("未评价".equals(request.getStatus())) {
                    jobCards = allCard.stream().filter(card -> card.getProcessNode().intValue() == 4).collect(Collectors.toList());
                } else if ("已评价".equals(request.getStatus())) {
                    jobCards = allCard.stream().filter(card -> card.getProcessNode().intValue() > 4).collect(Collectors.toList());
                } else {
                    jobCards.addAll(allCard);
                }
            } else {
                jobCards.addAll(allCard);
            }

            if (null != jobCards && 0 != jobCards.size()) {
                response.setTotalCount(jobCards.size());
                //allCard.sort((c1, c2)->c2.getCreateTime().compareTo(c1.getCreateTime()));
                int startNo = (request.getPageNo() - 1) * request.getPageSize();
                List<JobCard> collect1 = jobCards.stream().skip(startNo).limit(request.getPageSize()).collect(Collectors.toList());
                List<JobCardDetail> collect = collect1.stream().map(card -> {
                    return getJobCardDetail(card);
                }).collect(Collectors.toList());
                collect.stream().forEach(card -> {
                    if (card.getSatisfaction() != null) {
                        if (Integer.valueOf(card.getSatisfaction()).intValue() == 2) {
                            card.setSatisfaction("40");
                        } else if (Integer.valueOf(card.getSatisfaction()).intValue() == 3) {
                            card.setSatisfaction("60");
                        } else if (Integer.valueOf(card.getSatisfaction()).intValue() == 4) {
                            card.setSatisfaction("80");
                        } else if (Integer.valueOf(card.getSatisfaction()).intValue() == 5) {
                            card.setSatisfaction("100");
                        }
                    }
                });
                response.setCardList(collect);
                return response;
            }
        }
        response.setCardList(new ArrayList<>());
        response.setTotalCount(0);
        return response;
    }

    public List<JobCardDetail> getJobCardDetails(List<JobCard> userCardList, SeeCardRequest request) {
        List<JobCardDetail> cardList = new ArrayList<>();
        if (null != userCardList && userCardList.size() != 0) {
            if (request.getCardType().equals(StatusEnum.ASSIST.getStatus())) {
                for (JobCard card : userCardList) {
                    if (card.getAssistId() != null && card.getProcessNode() < ProcessEnum.CARD_FINISH.getProcess()) {
                        JobCardDetail jobCardDetail = getJobCardDetail(card);
                        jobCardDetail.setStatus(request.getCardType());
                        cardList.add(jobCardDetail);
                    }
                }
                return orderByCreateTime(cardList);
            } else if (request.getCardType().equals(StatusEnum.COMMENT.getStatus())) {
                for (JobCard card : userCardList) {
                    if (ProcessEnum.CARD_COMMENT.getProcess() == card.getProcessNode()
                            && (card.getComment() != null || card.getSatisfaction() != null)) {
                        JobCardDetail jobCardDetail = getJobCardDetail(card);
                        jobCardDetail.setStatus(request.getCardType());
                        cardList.add(jobCardDetail);
                    }
                }
                // order by satisfaction desc then createTime desc
                /*List<JobCardDetail> collect = cardList.stream().filter(card -> StringUtils.isEmpty(card.getSatisfaction()))
                        .filter(card -> StringUtils.isEmpty(card.getCreateTime()))
                        .sorted(Comparator.comparing(JobCardDetail::getSatisfaction, Comparator.reverseOrder())
                        .thenComparing(JobCardDetail::getCreateTime, Comparator.reverseOrder())).collect(Collectors.toList());*/
                cardList.sort((c1, c2) -> c2.getEndTime().compareTo(c1.getEndTime()));
                return cardList;
            } else if (StatusEnum.getRealStatus(request.getCardType()) != 0) {
                if (StatusEnum.getRealStatus(request.getCardType()) == 3) {  // in deal
                    for (JobCard card : userCardList) {
                        if (card.getAssistId() == null && card.getProcessNode() == ProcessEnum.CARD_DEAL.getProcess()) {
                            JobCardDetail jobCardDetail = getJobCardDetail(card);
                            jobCardDetail.setStatus(request.getCardType());
                            cardList.add(jobCardDetail);
                        }
                    }
                    return orderByCreateTime(cardList);
                } else if (StatusEnum.getRealStatus(request.getCardType()) == 1) {
                    if (request.getUserRole().equals(RoleEnum.ENGINEER.getName())) {
                        for (JobCard card : userCardList) {
                            if (card.getProcessNode() < ProcessEnum.CARD_DEAL.getProcess()) {
                                JobCardDetail jobCardDetail = getJobCardDetail(card);
                                jobCardDetail.setStatus(request.getCardType());
                                cardList.add(jobCardDetail);
                            }
                        }
                        return orderByCreateTime(cardList);
                    } else {
                        for (JobCard card : userCardList) {
                            if (card.getProcessNode() < ProcessEnum.CARD_FINISH.getProcess()) {
                                JobCardDetail jobCardDetail = getJobCardDetail(card);
                                jobCardDetail.setStatus(request.getCardType());
                                cardList.add(jobCardDetail);
                            }
                        }
                        cardList.sort((c1, c2) -> c2.getCreateTime().compareTo(c1.getCreateTime()));
                        return cardList;
                    }
                } else if (StatusEnum.getRealStatus(request.getCardType()) == 4) {
                    for (JobCard card : userCardList) {
                        if (card.getProcessNode() == ProcessEnum.CARD_FINISH.getProcess()) {
                            JobCardDetail jobCardDetail = getJobCardDetail(card);
                            jobCardDetail.setStatus(request.getCardType());
                            cardList.add(jobCardDetail);
                        }
                    }
                    cardList.sort((c1, c2) -> c2.getEndTime().compareTo(c1.getEndTime()));
                    return cardList;
                }
                return orderByCreateTime(cardList);
            }
        }
        return cardList;
    }

    public List<JobCardDetail> orderByCreateTime(List<JobCardDetail> cardList) {
        // order by priority desc then createTime desc
        List<JobCardDetail> collect = cardList.stream().filter(card -> card.getPriority() > 0)
                .filter(card -> StringUtils.isNotEmpty(card.getCreateTime()))
                .sorted(Comparator.comparing(JobCardDetail::getPriority, Comparator.reverseOrder())
                        .thenComparing(JobCardDetail::getCreateTime)).collect(Collectors.toList());

        /*List<JobCardDetail> collect = cardList.stream()
                .filter(card -> StringUtils.isNotEmpty(card.getCreateTime()))
                .sorted(Comparator.comparing(JobCardDetail::getCreateTime))
                        .collect(Collectors.toList());*/
        return collect;
    }

    public JobCardDetail getJobCardDetail(JobCard card) {
        JobCardDetail jobCardDetail = new JobCardDetail();
        jobCardDetail.setCardId(card.getCardId());
        jobCardDetail.setApplyName(Optional.ofNullable(card.getApplyName()).orElse(getUserName(card.getApplyId())));
        jobCardDetail.setPriority(card.getPriority());
        List<ProblemInfo> allProblemInfo = cardMapper.getAllProblemInfo(card.getProblemType());
        // get mobile_phone
        /*String phone = "";
        if (null != card.getApplyId()) {
            List<UserPhone> userPhone = userMapper.getUserPhone(card.getApplyId());
            if (null != userPhone && 0 != userPhone.size()) {
                for (UserPhone user : userPhone) {
                    if (null != user.getMobilePhone()) {
                        phone = user.getMobilePhone();
                    }
                }
            }
        }*/
        jobCardDetail.setMobliePhone(Optional.ofNullable(card.getPhone()).orElse(""));

        if (null != allProblemInfo && 0 != allProblemInfo.size()) {
            jobCardDetail.setProblemId(card.getProblemType());
            jobCardDetail.setProblemType(allProblemInfo.get(0).getTypeName());
        }
        jobCardDetail.setCreateTime(card.getCreateTime());
        jobCardDetail.setProcessNode(getProcessName(card.getProcessNode()));
        if (0 != card.getStatusId()) {
            String statusName = StatusEnum.getStatusName(card.getStatusId());
            jobCardDetail.setStatus(statusName);
        }
        if (null != card.getAssistId()) {
            jobCardDetail.setAssistName(getUserName(card.getAssistId()));
        }
        if (null != card.getReason()) {
            jobCardDetail.setReason(card.getReason());
        }
        if (null != card.getSolution()) {
            jobCardDetail.setSolution(card.getSolution());
        }
        if (null != card.getSatisfaction()) {
            jobCardDetail.setSatisfaction(card.getSatisfaction());
        }
        if (null != card.getComment()) {
            jobCardDetail.setComment(card.getComment());
        }
        if (null != card.getEndTime()) {
            jobCardDetail.setEndTime(card.getEndTime());
        }
        if (null != card.getDistribution()) {
            jobCardDetail.setDistribution(getUserName(card.getDistribution()));
        }
        if (null != card.getDeal()) {
            jobCardDetail.setDeal(getUserName(card.getDeal()));
        }
        if (null != card.getChangeId()) {
            jobCardDetail.setChange(getUserName(card.getChangeId()));
        }
        if (null != card.getDescription()) {
            jobCardDetail.setDescription(card.getDescription());
        }
        if (null != card.getConstruction()) {
            jobCardDetail.setConstruction(card.getConstruction());
        }
        if (null != card.getPhone()) {
            jobCardDetail.setPhone(card.getPhone());
        }
        if (0 != card.getDeptId()) {
            List<DeptInfo> allDept = deptMapper.getAllDept(card.getDeptId());
            if (null != allDept && 0 != allDept.size()) {
                jobCardDetail.setDeptName(allDept.get(0).getDeptName());
            }
        }
        if (0 != card.getWaitCount()) {
            jobCardDetail.setWaitCount(card.getWaitCount());
        }
        if (null != card.getAppointmentCreate()) {
            jobCardDetail.setAppointmentCreate(DateUtil.getNowDateStr(card.getAppointmentCreate()));
        }
        if (null != card.getAppointmentEnd()) {
            jobCardDetail.setAppointmentEnd(DateUtil.getNowDateStr(card.getAppointmentEnd()));
        }
        if (null != card.getPlace()) {
            jobCardDetail.setPlace(card.getPlace());
        }
        if (null != card.getDealWay()) {
            List<ServiceWay> serviceWay = cardMapper.getServiceWay(card.getDealWay());
            if (serviceWay != null && serviceWay.size() != 0) {
                jobCardDetail.setDealWay(serviceWay.get(0).getWayName());
                jobCardDetail.setWayId(serviceWay.get(0).getWayId());
            }
        }
        return jobCardDetail;
    }

    //@Transactional(rollbackFor = Exception.class)  // 默认只支持RunTimeException异常回滚
    @Override
    public int createJobCard(CreateCardRequest request) {
        log.info("start openId:{} create card: request", request.getOpenId(), request);
        // create card
        JobCard jobCard = new JobCard();
        jobCard.setCardId(getCardId());
        String company = "";
        // create by user
        if (null == request.getUserName() || 0 == request.getUserName().length()) {
            String userName = getUserName(request.getOpenId());
            jobCard.setApplyId(request.getOpenId());
            UserInfo userInfo = userMapper.getUserInfo(request.getOpenId());
            if (userInfo != null) {
                List<DeptInfo> companyList = new ArrayList<>();
                if (null != userInfo.getCompany()) {
                    company = userInfo.getCompany();
                    companyList = deptMapper.getDeptByName(userInfo.getCompany());
                }
                if (null != userInfo.getDepartment()) {
                    List<DeptInfo> deptList = deptMapper.getDeptByName(userInfo.getDepartment());
                    if (null != deptList && 0 != deptList.size()) {
                        if (deptList.size() == 1) {
                            request.setDeptId(deptList.get(0).getDeptId());
                        }
                    } else {
                        if (null != companyList && 1 == companyList.size()) {
                            for (DeptInfo dept : deptList) {
                                if (dept.getParentId() == companyList.get(0).getDeptId()) {
                                    request.setDeptId(dept.getDeptId());
                                    break;
                                }
                            }
                        } else {
                            request.setDeptId(deptList.get(0).getDeptId());
                        }
                    }
                }
            }
            jobCard.setApplyName(userName);
        }
//        else { // create by operator
//            UserInfo userOpenId = getUserOpenId(request.getUserName());
//            UserInfo userInfo = userMapper.getUserInfo(request.getOpenId());
//            List<UserInfo> userByName = userMapper.getUserByName(request.getUserName());
//            if (userOpenId == null && userByName == null) { // temp user
//                jobCard.setApplyName(request.getUserName());
//                if (userInfo != null && !userInfo.getUserName().equals(request.getUserName())) {
//                    jobCard.setConstruction(request.getOpenId());
//                }
//                // need go to register
//            } else if (userByName != null && userByName.size() != 0 && userOpenId == null) {
//                // insert one to tb_User_Role use user
//                UserRole userRole = new UserRole();
//                userRole.setUserId(userByName.get(0).getOpenId());
//                userRole.setRoleId(5);
//                try {
//                    userMapper.createUserRole(userRole);
//                    jobCard.setApplyId(userByName.get(0).getOpenId());
//                    jobCard.setApplyName(userByName.get(0).getUserName());
//                    if (userInfo != null && !userInfo.getUserName().equals(request.getUserName())) {
//                        jobCard.setConstruction(request.getOpenId());
//                    }
//                } catch (Exception ex) {
//                    log.info("create user:{} role failed:{}", userByName.get(0).getOpenId(), ex);
//                }
//            } else {
//                List<RoleInfo> roleInfo = userMapper.getRoleInfo(userOpenId.getOpenId());
//                if (null != roleInfo && 0 != roleInfo.size()) {
//                    for (RoleInfo role : roleInfo) {
//                        if (!role.getRoleName().equals(RoleEnum.USER.getName())) {
//                            return 4;
//                        }
//                    }
//                }
//                jobCard.setApplyId(userOpenId.getOpenId());
//                jobCard.setApplyName(Optional.ofNullable(userOpenId.getUserName()).orElse(request.getUserName()));
//                if (userInfo != null && !userInfo.getUserName().equals(request.getUserName())) {
//                    jobCard.setConstruction(request.getOpenId());
//                }
//            }
//        }
        jobCard.setCreateTime(DateUtil.getNowDate());
        jobCard.setPriority(request.getPriority());
        jobCard.setProblemType(request.getProblemType());
        // set appoint_time
        if (null != request.getAppointmentCreate()) {
            jobCard.setAppointmentCreate(DateUtil.getTimeStampByStr(request.getAppointmentCreate()));
        }
        if (null != request.getAppointmentCreate()) {
            jobCard.setAppointmentEnd(DateUtil.getTimeStampByStr(request.getAppointmentEnd()));
        }
        if (null != request.getPlace()) {
            jobCard.setPlace(request.getPlace());
        }
        jobCard.setDescription(Optional.ofNullable(request.getDescription()).orElse(""));
        int cardCount = cardMapper.getCardCount(jobCard);
        List<RoleInfo> roleInfo = userMapper.getRoleInfo(jobCard.getApplyId());
        if (null != roleInfo && 0 != roleInfo.size()) {
            for (RoleInfo role : roleInfo) {
                if (role.getRoleName().equals(RoleEnum.OPERATOR.getName()) && cardCount >= 3) {
                    return 3;
                }
            }
        }

        jobCard.setStatusId(Integer.valueOf(StatusEnum.WAPPLY.getStatus()));
        System.err.println("------------" + DateUtil.getNowDate());
        jobCard.setProcessNode(ProcessEnum.CARD_APPLY.getProcess());

        if (null != request.getPhone()) {
            if (!RegUtil.IsPhone(request.getPhone())
                    && !RegUtil.IsMobilePhone(request.getPhone())) return 6;

            jobCard.setPhone(request.getPhone());
            if (request.getPhone().trim().length() == 11) {
                jobCard.setMobilePhone(request.getPhone());
                // 修改用户信息表
                if (null != jobCard.getApplyId()) {
                    List<UserPhone> userPhone = userMapper.getUserPhone(jobCard.getApplyId());
                    if (null == userPhone || 0 == userPhone.size()) {
                        UserPhone userPhone1 = new UserPhone();
                        userPhone1.setOpenId(jobCard.getApplyId());
                        userPhone1.setUserName(jobCard.getApplyName());
                        userPhone1.setMobilePhone(jobCard.getMobilePhone());
                        try {
                            userMapper.createUserPhone(userPhone1);
                            log.info("add user:{} phone info success", jobCard.getApplyId());
                        } catch (Exception ex) {
                            log.info("add user:{} phone info failed:{}", jobCard.getApplyId(), ex);
                        }
                    } else {
                        UserPhone userPhone1 = userPhone.get(0);
                        if (userPhone1.getMobilePhone() == null) {
                            userPhone1.setMobilePhone(request.getPhone());
                            try {
                                userMapper.updatePhoneByName(userPhone1);
                                log.info("add user:{} phone info success", jobCard.getApplyId());
                            } catch (Exception ex) {
                                log.info("update user:{} phone info failed:{}", jobCard.getApplyId(), ex);
                            }
                        }
                    }
                }
            }
        } else {
            return 5;
        }
        jobCard.setDeptId(Optional.ofNullable(request.getDeptId()).orElse(0));
        // 设置 话务员
//        System.err.println("------"+ RoleEnum.OPERATOR.name());
//        if (null == jobCard.getDistribution()) {
//            jobCard.setDistribution(getOperator(request.getProblemType(), RoleEnum.OPERATOR.getName(), company).getOpenId());
//        }
        jobCard.setProcessNode(ProcessEnum.CARD_DISTRIBUTION.getProcess());
        // 判断是否为补单
        if (null != request.getDeal() && request.getDeal().length() != 0) {
            jobCard.setDeal(request.getDeal());
        } else {
            // 设置 工程师
            String openId = getOperator(request.getProblemType(), RoleEnum.ENGINEER.getName(), company).getOpenId();
            jobCard.setDeal(openId);
        }
        log.info("card will be deal by openId:{}", jobCard.getDeal());
        //jobCard.setProcessNode(ProcessEnum.CARD_DEAL.getProcess());
        // set wait_count
        setWaitCount(jobCard);
        try {
            cardMapper.createCard(jobCard);
            // send eamil to notice deal 工程师用户（消息内容：您有新的工单/n 故障类型：***/n 当前待处理数**/n，
            String nowProject = this.project.equals("prod") ? "线上" : "测试";
            System.out.println("==========project: " + nowProject);
            if (nowProject.equals("线上")) {
                String toEmail = "hzhehao@chint.com";
                List<UserPhone> userPhone = userMapper.getUserPhone(jobCard.getDeal());
                if (null != userPhone && 0 != userPhone.size()) {
                    for (UserPhone user : userPhone) {
                        if (null != user.getEmail()) {
                            toEmail = user.getEmail();
                            break;
                        }
                    }
                }

                List<String> toList = new ArrayList<>();
                toList.add(toEmail);
                String subject = nowProject + "-IT服务中心待处理工单通知";
                JobCardDetail jobCardDetail = getJobCardDetail(jobCard);
                String message = "您有新的待处理工单,工单号: " + jobCardDetail.getCardId() + ",工单申请人：" + jobCardDetail.getApplyName()
                        + ",故障类型: " + jobCardDetail.getProblemType() + ",当前总待处理工单数: " + jobCard.getWaitCount() + ",请尽快处理！";
                ExchangeMailUtil.send(subject, toList, message);
            }
//            MailUtil.sendHtmlMail(toEmail, subject, message);

            log.info("openId:{} create card info:{} success", request.getOpenId(), jobCard);
            return 1;
        } catch (Exception ex) {
            log.info("openId:{} create card info:{} failed:{}", request.getOpenId(), jobCard, ex);
            return 0;
        }
    }

    public void setWaitCount(JobCard card) {
        List<JobCard> userCardList = new ArrayList<>();
        if (card.getAssistId() != null) {
            userCardList = cardMapper.getUserCardList(card.getAssistId());
        } else {
            userCardList = cardMapper.getUserCardList(card.getDeal());
        }

        // get enginner all undeal card
        if (null != userCardList && 0 != userCardList.size()) {
            // wait count by unDeal and Deal
            List<JobCard> collect = userCardList.stream().filter(card1 -> card1.getProcessNode() <= ProcessEnum.CARD_DEAL.getProcess()).collect(Collectors.toList());
            if (collect != null && 0 != collect.size()) {
                List<JobCard> collect2 = collect.stream().filter(card2 -> card2.getPriority() == card.getPriority()).collect(Collectors.toList());
                if (null != collect2 && 0 != collect2.size()) {
                    card.setWaitCount(collect2.size() + 1);
                } else {
                    card.setWaitCount(1);
                }
            } else {
                card.setWaitCount(1);
            }

        } else {
            card.setWaitCount(1);
        }
    }

    @Override
    public List<ProblemInfoResponse> getProblemList(CommonRequest request) {
        List<ProblemInfo> problemInfoList = cardMapper.getAllProblemInfo(0);
        List<ProblemInfo> problemInfos = new ArrayList<>();
        // 获取温州区域的故障类型
        if (null != request && null != request.getOpenId()) {
            UserInfo userInfo = userMapper.getUserInfo(request.getOpenId());
            if (null != userInfo) {
                if (null != userInfo.getCompany() && "温州区域".equals(userInfo.getCompany())) {
                    List<ProblemInfo> collect = problemInfoList.stream().filter(problem -> problem.getTypeId() != 15
                            && problem.getTypeId() != 16 && problem.getTypeId() != 20)
                            .collect(Collectors.toList());
                    problemInfos.addAll(collect);
                } else {
                    problemInfos.addAll(problemInfoList);
                }
            } else {
                problemInfos.addAll(problemInfoList);
            }
        } else {
            problemInfos.addAll(problemInfoList);
        }
        log.info("problemList:{}", problemInfos);
        List<ProblemInfoResponse> responseList = new ArrayList<>();
        for (ProblemInfo problemInfo : problemInfos) {
            ProblemInfoResponse response = new ProblemInfoResponse();
            response.setTypeId(problemInfo.getTypeId());
            response.setTypeName(problemInfo.getTypeName());
            response.setParentId(problemInfo.getParentId());
            response.setTypeNo(problemInfo.getTypeNo());
            if (null == response.getProblemList() || 0 == response.getProblemList().size()) {
                List<ProblemInfoResponse> responseList2 = new ArrayList<>();
                response.setProblemList(responseList2);
            }
            responseList.add(response);
        }
        // 循环添加子故障类型
        for (ProblemInfoResponse problem : responseList) {
            for (ProblemInfoResponse problem1 : responseList) {
                if (problem1.getParentId().equals(problem.getTypeId())) {
                    problem.getProblemList().add(problem1);
                }
            }
        }

        List<ProblemInfoResponse> collect = responseList.stream().filter(data -> data.getParentId() == -1).collect(Collectors.toList());
        collect.stream().forEach(card -> {
            if (null == card.getProblemList() || 0 == card.getProblemList().size()) {
                ProblemInfoResponse problemInfoResponse = new ProblemInfoResponse();
                problemInfoResponse.setParentId(card.getParentId());
                problemInfoResponse.setTypeName(card.getTypeName());
                problemInfoResponse.setTypeId(card.getTypeId());
                problemInfoResponse.setTypeNo(1);
                List<ProblemInfoResponse> responses = new ArrayList<>();
                problemInfoResponse.setProblemList(responses);
                card.getProblemList().add(problemInfoResponse);
            } else {
                card.getProblemList().sort((p1, p2) -> p1.getTypeNo().compareTo(p2.getTypeNo()));
            }
        });
        collect.sort((p1, p2) -> p1.getTypeNo().compareTo(p2.getTypeNo()));
        return collect;
    }

    @Override
    public JobCardDetail getJobCardInfo(SeeCardDetailRequest request) {
        JobCard userCardDetail = cardMapper.getUserCardDetail(request.getCardId());
        JobCardDetail jobCardDetail = getJobCardDetail(userCardDetail);
        if (userCardDetail.getProcessNode().equals(ProcessEnum.CARD_COMMENT.getProcess())) {
            jobCardDetail.setStatus(StatusEnum.COMMENT.getStatus());
        } else {
            jobCardDetail.setStatus(StatusEnum.getStatusName(userCardDetail.getStatusId()));
        }
        return jobCardDetail;
    }

    @Override
    public List<JobCardDetail> updateCardInfo(ChangeCardRequest request) {
        JobCard userCardDetail = cardMapper.getUserCardDetail(request.getCardId());
        if (null == userCardDetail) {
            return null;
        }
        int waitCount = userCardDetail.getWaitCount();
        userCardDetail.setReason(Optional.ofNullable(request.getReason()).orElse(""));
        userCardDetail.setSolution(Optional.ofNullable(request.getSolution()).orElse(""));
        //userCardDetail.setAssistId(Optional.ofNullable(request.getAssist()).orElse(""));
        userCardDetail.setProblemType(Optional.ofNullable(request.getProblemType()).orElse(0));
        userCardDetail.setDealWay(Optional.ofNullable(request.getWayId()).orElse(0));
        // update wait_count to zero
        userCardDetail.setWaitCount(0);
        userCardDetail.setEndTime(DateUtil.getNowDate());
        // update card status to deal
        userCardDetail.setStatusId(Integer.valueOf(StatusEnum.WFINISH.getStatus()));
        userCardDetail.setProcessNode(ProcessEnum.CARD_FINISH.getProcess());

        List<JobCardDetail> jobCardDetailList1 = getJobCardDetailList(userCardDetail, request.getOpenId());
        List<JobCardDetail> jobCardDetailList = orderByCreateTime(jobCardDetailList1);
        // set wait_count - 1
        List<JobCard> userCardList = new ArrayList<>();
        if (userCardDetail.getAssistId() != null) {
            userCardList = cardMapper.getUserCardList(userCardDetail.getAssistId());
        } else {
            userCardList = cardMapper.getUserCardList(userCardDetail.getDeal());
        }
        if (userCardList != null && userCardList.size() != 0 && waitCount != 0) {
            List<JobCard> collect = userCardList.stream()
                    .filter(card -> card.getProcessNode().intValue() <= ProcessEnum.CARD_DEAL.getProcess()).collect(Collectors.toList());
            if (collect != null && 0 != collect.size()) {
                List<JobCard> collect1 = collect.stream().filter(card -> card.getPriority().intValue() == userCardDetail.getPriority().intValue())
                        .filter(card1 -> card1.getWaitCount() > waitCount).collect(Collectors.toList());
                if (null != collect1 && 0 != collect1.size()) {
                    collect1.stream().forEach(card -> {
                        log.info("card:{}  waitCount:{}", card.getCardId(), card.getWaitCount());
                        card.setWaitCount(card.getWaitCount() - 1);
                        try {
                            cardMapper.updateCardByEng(card);
                        } catch (Exception ex) {
                            log.info("set card:{} waitCount -1 failed:{}", card.getCardId(), ex);
                        }
                    });
                }
            }
        }
        // set message to user tell card has been deal
        Map<String, TemplateData> map2 = new HashMap<>();
        JobCardDetail jobCardDetail1 = getJobCardDetail(userCardDetail);
        map2.put("time2", new TemplateData(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        map2.put("thing4", new TemplateData("故障类型:" + jobCardDetail1.getProblemType() + "请评价"));
        GetOpenIdUtil.subscribeMessage(userCardDetail.getApplyId(), "/pages/listDetail/listDetail?status=已处理", map2, "B_yjAlQ1rIBlW6i9B2Li_JUjoPgWOs5J4JBgNiF5xhY");
        // send email to user tell card has been deal
        try {
            if (null != userCardDetail.getApplyId()) {
                String toEmail = "";
                List<UserPhone> userPhone = userMapper.getUserPhone(userCardDetail.getApplyId());
                if (null != userPhone && 0 != userPhone.size()) {
                    for (UserPhone user : userPhone) {
                        if (null != user.getEmail()) {
                            toEmail = user.getEmail();
                            break;
                        }
                    }
                }
                if (!"".equals(toEmail)) {
                    String nowProject = this.project.equals("prod") ? "线上" : "测试";
                    List<String> toList = new ArrayList<>();
                    toList.add(toEmail);
                    String subject = nowProject + "-IT服务中心工单已处理通知";
                    JobCardDetail jobCardDetail = getJobCardDetail(userCardDetail);
                    String dealName = "";
                    if (userCardDetail.getAssistId() != null) {
                        if (userCardDetail.getAssistId().equals(request.getOpenId())) {
                            dealName = jobCardDetail.getAssistName();
                        } else {
                            dealName = jobCardDetail.getDeal();
                        }
                    } else {
                        dealName = jobCardDetail.getDeal();
                    }
                    String message = jobCardDetail.getApplyName() + "，您好，您在IT服务中心小程序创建的单号为：" + jobCardDetail.getCardId() + " 的故障单已由" + dealName + "处理，请登录小程序给个好评，感谢您的支持！";
                    ExchangeMailUtil.send(subject, toList, message);
                }
            }
        } catch (Exception ex) {
            log.info("send message to deal:{} failed:{}", userCardDetail.getDeal(), ex);
            System.out.println();
        }

        return jobCardDetailList;
    }

    @Override
    public List<UserInfo> getAssistList(AssistInfoRequest request) {
        // get assist user of point type
        List<UserInfo> assistUser = cardMapper.getAssistUser(new CommonRequest());
        /*List<UserInfo> assistUser = userMapper.getUserInfoByType(request.getProblemType());*/
        if (request.getOpenId() != null) {
            List<UserInfo> collect = assistUser.stream().filter(user -> !user
                    .getOpenId().equals(request.getOpenId())).collect(Collectors.toList());
            return collect;
        } else if (request.getDeal() != null) {
            List<UserInfo> collect = assistUser.stream().filter(user -> !user
                    .getUserName().equals(request.getDeal())).collect(Collectors.toList());
            return collect;
        }
        return assistUser;
    }

    @Override
    public List<JobCardDetail> updateCardByComment(CommentCardRequest request) {
        JobCard userCardDetail = cardMapper.getUserCardDetail(request.getCardId());
        if (null == userCardDetail) {
            return null;
        }
        userCardDetail.setSatisfaction(Optional.ofNullable(request.getSatisfaction()).orElse(""));
        userCardDetail.setComment(Optional.ofNullable(request.getComment()).orElse(""));

        // update card status to finish
        userCardDetail.setStatusId(Integer.valueOf(StatusEnum.WFINISH.getStatus()));
        userCardDetail.setProcessNode(ProcessEnum.CARD_COMMENT.getProcess());

        return getJobCardDetailList(userCardDetail, request.getOpenId());
    }

    @Override
    public List<JobCardDetail> receiveOneCard(ChangeCardRequest request) {
        JobCard userCardDetail = cardMapper.getUserCardDetail(request.getCardId());
        if (null == userCardDetail) {
            return null;
        }

        userCardDetail.setReason(Optional.ofNullable(request.getReason()).orElse(""));
        userCardDetail.setSolution(Optional.ofNullable(request.getSolution()).orElse(""));
        userCardDetail.setAssistId(Optional.ofNullable(request.getAssist()).orElse(""));
        if (request.getAssist() != null) {
            try {
                String nowProject = this.project.equals("prod") ? "线上" : "测试";
                if (nowProject.equals("线上")) {
                    String toEmail = "hzhehao@chint.com";
                    List<UserPhone> userPhone = userMapper.getUserPhone(userCardDetail.getAssistId());
                    if (null != userPhone && 0 != userPhone.size()) {
                        for (UserPhone user : userPhone) {
                            if (null != user.getEmail()) {
                                toEmail = user.getEmail();
                                break;
                            }
                        }
                    }

                    List<String> toList = new ArrayList<>();
                    toList.add(toEmail);
                    String subject = nowProject + "-IT服务中心协办工单通知";
                    JobCardDetail jobCardDetail = getJobCardDetail(userCardDetail);
                    String message = "您有新的未处理协办工单,工单号: " + jobCardDetail.getCardId() + ",原处理工程师：" + jobCardDetail.getDeal()
                            + ",故障类型: " + jobCardDetail.getProblemType() + ",申请人: " + jobCardDetail.getApplyName() + ",请尽快处理！";
                    ExchangeMailUtil.send(subject, toList, message);
                }
            } catch (Exception ex) {
                log.info("send message to deal:{} failed:{}", userCardDetail.getDeal(), ex);
                System.out.println();
            }
        }
        userCardDetail.setProblemType(Optional.ofNullable(request.getProblemType()).orElse(0));
        // update wait_count to zero
        int waitCount = userCardDetail.getWaitCount();
        //userCardDetail.setWaitCount(0);

        userCardDetail.setStatusId(Integer.valueOf(StatusEnum.WDEAL.getStatus()));
        userCardDetail.setProcessNode(ProcessEnum.CARD_DEAL.getProcess());

        if (userCardDetail.getAssistId() != null) {
            // update deal set wait count -1
            List<JobCard> userCardList = cardMapper.getUserCardList(userCardDetail.getDeal());
            if (userCardList != null && userCardList.size() != 0) {
                List<JobCard> collect = userCardList.stream()
                        .filter(card -> card.getProcessNode().intValue() <= ProcessEnum.CARD_DEAL.getProcess()).collect(Collectors.toList());
                if (collect != null && 0 != collect.size()) {
                    List<JobCard> collect1 = collect.stream().filter(card -> card.getPriority().intValue() == userCardDetail.getPriority().intValue())
                            .filter(card1 -> card1.getWaitCount() > waitCount).collect(Collectors.toList());
                    if (null != collect1 && 0 != collect1.size()) {
                        collect1.stream().forEach(card -> {
                            log.info("card:{}  waitCount:{}", card.getCardId(), card.getWaitCount());
                            card.setWaitCount(card.getWaitCount() - 1);
                            try {
                                cardMapper.updateCardByEng(card);
                            } catch (Exception ex) {
                                log.info("set card:{} waitCount -1 failed:{}", card.getCardId(), ex);
                            }
                        });
                    }
                }
            }

            // update assist set wait count
            setWaitCount(userCardDetail);
        }
        List<JobCardDetail> jobCardDetailList = getJobCardDetailList(userCardDetail, request.getOpenId());

        return orderByCreateTime(jobCardDetailList);
    }

    @Override
    public List<ServiceWay> getServiceWayList() {
        try {
            return cardMapper.getServiceWay(0);
        } catch (Exception ex) {
            log.info("get all service way failed:{}", ex);
            return null;
        }
    }

    @Override
    public HandCardListResponse getHandCardList(CommonRequest request) {
        try {
            /**
             * 两个参数，工程师列表和用户状态
             */
            HandCardListResponse response = new HandCardListResponse();
            // set user job status
            UserStatus engStatusInfo = getEngStatusInfo(request);
            if (null != engStatusInfo) {  // 如果是工程师
                response.setUserStatus(engStatusInfo);
            } else {
                response.setUserStatus(new UserStatus(RoleEnum.USER.getName()));  // 如果是普通的工作人员，就创建一个普通的用户的状态。
            }
            List<HandCard> handCardList = cardMapper.getHandCardList(request.getOpenId());
            List<HandCardResponse> cardResponse = new ArrayList<>();
            if (null != handCardList && 0 != handCardList.size()) {
                List<HandCardResponse> collect = handCardList.stream().map(card -> {
                    // 返回请求
                    HandCardResponse handCardResponse = new HandCardResponse();
                    handCardResponse.setCardId(card.getCardId());
                    handCardResponse.setCardCount(card.getCardCount());
                    handCardResponse.setDescription(Optional.ofNullable(card.getDescription()).orElse(""));
                    handCardResponse.setFillName(Optional.ofNullable(card.getFillName()).orElse(""));
                    handCardResponse.setOpenId(Optional.ofNullable(card.getOpenId()).orElse(""));
                    handCardResponse.setFillTime(DateUtil.getNowDateStr2(card.getFillTime()));
                    return handCardResponse;
                }).collect(Collectors.toList());
                if (collect != null && collect.size() != 0) {
                    collect.sort((c1, c2) -> c2.getFillTime().compareTo(c1.getFillTime()));
                    response.setResponseList(collect);
                    return response;
                }
            }
            response.setResponseList(cardResponse);
            return response;
        } catch (Exception ex) {
            log.info("get hand card list info failed:{}", ex);
            return null;
        }
    }

    @Override
    public int createOneHandCard(CreateHandCardRequest handCard) {
        try {
            // get all hand card judge exist
            List<HandCard> handCardList = cardMapper.getHandCardList(handCard.getOpenId());
            if (null != handCardList && 0 != handCardList.size()) {
                for (HandCard card : handCardList) {
                    if (DateUtil.getNowDateStr2(card.getFillTime()).equals(handCard.getFillTime())) {
                        return 2;
                    }
                }
            }
            HandCard card = new HandCard();
            card.setCardId(getHandCardId());
            card.setOpenId(handCard.getOpenId());
            card.setCardCount(handCard.getCardCount());
            card.setDescription(Optional.ofNullable(handCard.getDescription()).orElse(""));
            String userName = getUserName(handCard.getOpenId());
            card.setFillName(Optional.ofNullable(userName).orElse(""));
            if (null != handCard.getFillTime()) {
                card.setFillTime(DateUtil.getTimeStampByStr(handCard.getFillTime()));
            } else {
                card.setFillTime(DateUtil.getTimeStamp());
            }
            cardMapper.createHandCard(card);
            return 1;
        } catch (Exception ex) {
            log.info("open:{} create hand card failed:{}", handCard.getOpenId(), ex);
            return 0;
        }
    }

    /**
     * 设置工程师状态
     *
     * @param request
     * @return
     */
    @Override
    public UserStatus getEngStatusInfo(CommonRequest request) {
        try {
            List<UserStatus> engStatus = cardMapper.getEngStatus(request.getOpenId());
            if (engStatus != null && engStatus.size() != 0) {
                if (engStatus.size() > 1) {
                    boolean flag = false;
                    for (UserStatus status : engStatus) {
                        if (status.getRoleName().equals(RoleEnum.OPERATOR.getName())
                                || status.getRoleName().equals(RoleEnum.ENGINEER.getName())) {
                            flag = true;
                            return status;
                        } else if (status.getRoleName().equals(RoleEnum.ADMINISTRATOR.getName())) {
                            flag = true;
                        }
                    }
                    if (!flag) {  // flag == flase
                        return engStatus.get(0);
                    } else {
                        UserStatus status = new UserStatus();
                        status.setOpenId(request.getOpenId());
                        status.setRoleName(RoleEnum.USER.getName());
                        status.setStatusId(0);
                        status.setStatusName("离线");
                        return status;
                    }
                } else {
                    return engStatus.get(0);
                }
            } else {
                UserStatus status = new UserStatus();
                status.setOpenId(request.getOpenId());
                status.setRoleName(RoleEnum.USER.getName());
                status.setStatusId(0);
                status.setStatusName("离线");
                return status;
            }
        } catch (Exception ex) {
            log.info("get user:{} job status failed:{}", request.getOpenId(), ex);
            return null;
        }
    }

    @Override
    public List<String> getContent(CommonRequest request) {
        List<JobCard> userCardPage = cardMapper.getUserCardPage(request.getOpenId(), 0, 0);
        List<String> str = new ArrayList<>();
        if (userCardPage != null && 0 != userCardPage.size()) {
            List<JobCard> collect = userCardPage.stream()
                    .filter(card -> card.getProcessNode() < ProcessEnum.CARD_DEAL.getProcess()).collect(Collectors.toList());
            if (null != collect && 0 != collect.size()) {
                collect.sort((c1, c2) -> c2.getCreateTime().compareTo(c1.getCreateTime()));
                JobCard jobCard = collect.get(0);
                JobCardDetail jobCardDetail = getJobCardDetail(jobCard);
                //str.add("发布成功：故障类型："+ jobCardDetail.getProblemType() +"，工程师："+ jobCardDetail.getDeal() +" ，待处理数：" + jobCardDetail.getWaitCount());
                str.add("发布成功 处理工程师：" + jobCardDetail.getDeal());
                str.add("/pages/listDetail/listDetail?status=已创建");
                return str;
            }
        }
        str.add("发布成功");
        str.add("/pages/listDetail/listDetail?status=已创建");
        return str;
    }

    @Override
    public int updateCardEng(ChangeCardRequest request) {
        JobCard card = new JobCard();
        card.setCardId(Optional.ofNullable(request.getCardId()).orElse(""));
        card.setDeal(Optional.ofNullable(request.getDeal()).orElse(""));
        try {
            cardMapper.updateCardByEng(card);
            return 1;
        } catch (Exception ex) {
            log.info("update card by info:{} failed:{}", request, ex);
            return 0;
        }
    }

    @Override
    public int createProblem(CreateProblemRequest request) {
        // get all problem type
        List<ProblemInfo> allProblemInfo = cardMapper.getAllProblemInfo(0);
        List<ProblemInfoResponse> problemList = getProblemList(new CommonRequest());
        ProblemInfo problemInfo = new ProblemInfo();
        if (request.getTypeId() == null) {
            // is exist
            for (ProblemInfoResponse problem : problemList) {
                if (problem.getTypeName().equals(request.getTypeName())) {
                    return 2;
                }
            }

            // create level one type
            List<Integer> typeIdList = allProblemInfo.stream().map(type -> {
                return type.getTypeId();
            }).collect(Collectors.toList());
            Integer maxTypeId = Collections.max(typeIdList);
            problemInfo.setTypeId(maxTypeId + 1);

            List<Integer> typeNoList = problemList.stream().map(type -> {
                return type.getTypeNo();
            }).collect(Collectors.toList());
            Integer maxTypeNo = Collections.max(typeNoList);
            problemInfo.setTypeNo(maxTypeNo + 1);
            problemInfo.setServiceId(-1);
            problemInfo.setTypeName(request.getTypeName());
            problemInfo.setParentId(-1);
            // create a default level two
            ProblemInfo problemInfo1 = new ProblemInfo();
            problemInfo1.setServiceId(Optional.ofNullable(request.getServiceId()).orElse(2));
            problemInfo1.setParentId(problemInfo.getTypeId());
            problemInfo1.setTypeName(request.getTypeName());
            problemInfo1.setTypeNo(1);
            problemInfo1.setTypeId(problemInfo.getTypeId() + 1);
            try {
                cardMapper.createProblem(problemInfo1);
            } catch (Exception ex) {
                log.info("create problem type by:{} failed:{}", request, ex);
            }

        } else {
            // create level two type
            for (ProblemInfoResponse problemInfoResponse : problemList) {
                if (problemInfoResponse.getTypeId().equals(request.getTypeId())) {
                    if (null != problemInfoResponse && null != problemInfoResponse.getProblemList()) {
                        // is exist
                        if (null != problemInfoResponse.getProblemList() && 0 != problemInfoResponse.getProblemList().size()) {
                            for (ProblemInfoResponse problem : problemInfoResponse.getProblemList()) {
                                if (problem.getTypeName().equals(request.getTypeName())) {
                                    return 2;
                                }
                            }
                        }

                        List<Integer> typeIdList = allProblemInfo.stream().map(type -> {
                            return type.getTypeId();
                        }).collect(Collectors.toList());
                        Integer maxTypeId = Collections.max(typeIdList);
                        problemInfo.setTypeId(maxTypeId + 1);

                        List<Integer> typeNoList = problemInfoResponse.getProblemList().stream().map(type -> {
                            return type.getTypeNo();
                        }).collect(Collectors.toList());
                        Integer maxTypeNo = Collections.max(typeNoList);
                        problemInfo.setTypeNo(maxTypeNo + 1);
                        problemInfo.setServiceId(Optional.ofNullable(request.getServiceId()).orElse(2));
                        problemInfo.setTypeName(request.getTypeName());
                        problemInfo.setParentId(request.getTypeId());
                    }
                }
            }
        }

        try {
            cardMapper.createProblem(problemInfo);
            return 1;
        } catch (Exception ex) {
            log.info("create problem type by:{} failed:{}", request, ex);
            return 0;
        }
    }

    @Override
    public int deleteProblem(CommonRequest request) {
        try {
            List<ProblemInfo> allProblemInfo = cardMapper.getAllProblemInfo(request.getTypeId());
            if (null != allProblemInfo && 0 != allProblemInfo.size()) {
                ProblemInfo problemInfo = allProblemInfo.get(0);
                if (problemInfo.getParentId() == -1) {
                    // delete one and all level two
                    List<ProblemInfo> allProblemInfo1 = cardMapper.getAllProblemInfo(0);
                    if (null != allProblemInfo1 && 0 != allProblemInfo1.size()) {
                        List<ProblemInfo> collect = allProblemInfo1.stream()
                                .filter(problem -> problem.getParentId().equals(request.getTypeId())).collect(Collectors.toList());
                        if (collect != null && 0 != collect.size()) {
                            for (ProblemInfo problem : collect) {
                                cardMapper.deleteProblem(new ProblemInfo(problem.getTypeId()));
                            }
                            cardMapper.deleteProblem(new ProblemInfo(request.getTypeId()));
                        } else {
                            // delete one level two
                            cardMapper.deleteProblem(new ProblemInfo(request.getTypeId()));
                        }
                    }

                } else {
                    // delete one level two
                    cardMapper.deleteProblem(new ProblemInfo(request.getTypeId()));
                }
            } else {
                // type not exist
                return 2;
            }
            return 1;
        } catch (Exception ex) {
            log.info("delete problem by:{} failed:{}", request, ex);
            return 0;
        }
    }

    @Override
    public UserCardResponse getCardCountInfo(CommonRequest request) {
        if (request.getCountId() == null || request.getCountId().intValue() == 0) {
            request.setCountId(1);
        }
        List<JobCard> allCardCount = cardMapper.getAllCardCount(request);
        UserCardResponse response = new UserCardResponse();
        if (null != allCardCount && allCardCount.size() != 0) {
            response.setTotalCount(allCardCount.size());
            List<JobCard> unDeal = allCardCount.stream().filter(card -> card.getStatusId() <= 3).collect(Collectors.toList());
            response.setUntreateCount(Optional.ofNullable(unDeal.size()).orElse(0));
            List<JobCard> deal = allCardCount.stream().filter(card -> card.getStatusId() > 3).collect(Collectors.toList());
            response.setTreateCount(Optional.ofNullable(deal.size()).orElse(0));
            return response;
        }
        return response;
    }

    @Override
    public UserCardCountResponse getUserCardCount(CommonRequest request) {
        if (request.getCountId() == null || request.getCountId().intValue() == 0) {
            request.setCountId(3);
        }
//        if (null == request.getServiceId()) {
//            // default select eng of message service
//            request.setServiceId(5);
//        }
        // 电源公司所有工程师都是5
        request.setServiceId(5);
        UserCardCountResponse response = new UserCardCountResponse();
        List<UserInfo> assistUser = cardMapper.getAssistUser(request);
        Map<String, UserInfo> userMap = assistUser.stream().collect(Collectors.toMap(UserInfo::getUserName, UserInfo -> UserInfo));
        List<JobCard> allCardCount = cardMapper.getAllCardCount(request);
        List<String> userList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        if (null != allCardCount && 0 != allCardCount.size()) {
            Map<String, Integer> userCountMap = new HashMap<>();
            int totalCount = 0;
            for (JobCard card : allCardCount) {
                JobCardDetail cardDetail = getJobCardDetail(card);
                if (userCountMap.containsKey(cardDetail.getDeal())) {
                    if (card.getAssistId() == null || card.getDeal().equals(card.getAssistId())) {
                        userCountMap.put(cardDetail.getDeal(), userCountMap.get(cardDetail.getDeal()) + 1);
                    }
                } else {
                    if (card.getAssistId() == null || card.getDeal().equals(card.getAssistId())) {
                        userCountMap.put(cardDetail.getDeal(), 1);
                    } else {
                        userCountMap.put(cardDetail.getDeal(), 0);
                    }
                }

                if (null != cardDetail.getAssistName()
                        && userMap.containsKey(cardDetail.getAssistName())) {
                    if (userCountMap.containsKey(cardDetail.getAssistName())) {
                        if (!card.getDeal().equals(card.getAssistId())) {
                            userCountMap.put(cardDetail.getAssistName(), userCountMap.get(cardDetail.getAssistName()) + 1);
                        }
                    } else {
                        if (!card.getDeal().equals(card.getAssistId())) {
                            userCountMap.put(cardDetail.getAssistName(), 1);
                        } else {
                            userCountMap.put(cardDetail.getAssistName(), 0);
                        }
                    }
                }

                totalCount++;
            }
            response.setTotalCount(totalCount);
            List<Map.Entry<String, Integer>> userEntry = new ArrayList<>(userCountMap.entrySet());
            Collections.sort(userEntry, ((u1, u2) -> {
                return u2.getValue().compareTo(u1.getValue());
            }));
            List<Map.Entry<String, Integer>> collect = userEntry.stream().skip(0).limit(5).collect(Collectors.toList());
            Collections.sort(collect, ((u1, u2) -> {
                return u1.getValue().compareTo(u2.getValue());
            }));

            for (Map.Entry<String, Integer> entry : collect) {
                userList.add(entry.getKey());
                //int count = getPart(entry.getValue(), totalCount);
                countList.add(entry.getValue());
            }
            if (userList.size() < 6) {
                // 获取所有工程师
                List<UserInfo> userInfos = new ArrayList<>();
                if (null != assistUser && 0 != assistUser.size()) {
                    for (UserInfo userInfo : assistUser) {
                        boolean flag = true;
                        for (String user : userList) {
                            if (userInfo.getUserName().equals(user)) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            userInfos.add(userInfo);
                        }
                    }
                }
//                if (null != userInfos && 0 != userInfos.size()) {
//                    // 硬编码5是什么意思？
//                    int sum = 5 - userList.size();
//                    for (int i = 0; i < sum; i++) {
//                        userList.add(userInfos.get(i).getUserName());
//                        countList.add(0);
//                    }
//                }
            }
            Collections.sort(countList);
            response.setCountList(countList);
            response.setUserList(userList);
            return response;
        } else {
            if (null != assistUser && 0 != assistUser.size()) {
                int sum = assistUser.size() <= 5 ? assistUser.size() : 5;
                for (int i = 0; i < sum; i++) {
                    userList.add(assistUser.get(i).getUserName());
                    countList.add(0);
                }
                response.setCountList(countList);
                response.setUserList(userList);
                return response;
            }
        }
        response.setUserList(Arrays.asList("黄浙浩", "祁彦锦", "戴友谊", "金秋平"));
        response.setCountList(Arrays.asList(0, 0, 0, 0));
        return response;
    }

    @Override
    public UserCommentCountResponse getUserCommentCount(CommonRequest request) {
        if ((request.getYear() == null || request.getYear().equals(""))
                && (request.getQuarter() == null || request.getQuarter().equals(""))) {
            request.setTimeId(1);
        } else {
            request.setTimeId(0);
        }
        if (request.getQuarter() != null && request.getQuarter() == 5) {
            request.setQuarter(null);
            if (request.getYear() == null || "".equals(request.getYear())) {
                request.setTimeId(2);
            }
        }
        UserCommentCountResponse response = new UserCommentCountResponse();

        int totalCount = 0;
        List<String> timeList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        List<Integer> partList = new ArrayList<>();
        if (request.getTimeId() != null) {
            int allCount = 0;
            int allSum = 0;
            if (request.getTimeId() == 1 || request.getQuarter() != null) {
                int month = 0;
                List<Integer> allMonth = new ArrayList<>();
                if (request.getTimeId() == 1) {
                    Calendar cal = Calendar.getInstance();
                    month = cal.get(Calendar.MONTH) + 1;
                    allMonth.addAll(getAllMonth(month, 0));
                } else {
                    month = request.getQuarter();
                    allMonth.addAll(getAllMonth(0, month));
                }
                for (int mon : allMonth) {
                    request.setMonth(mon);
                    List<JobCard> allCardCount = cardMapper.getAllCardCount(request);
                    if (null != allCardCount && 0 != allCardCount.size()) {
                        totalCount += allCardCount.size();
                        timeList.add(mon + "月");
                        countList.add(allCardCount.size());
                        List<JobCard> collect = allCardCount.stream()
                                .filter(card -> StringUtils.isNotEmpty(card.getSatisfaction())).collect(Collectors.toList());
                        /*int part = collect.size() * 100 / allCardCount.size();
                        double num = collect.size() * 100.0 / totalCount;
                        if (num == 0.0) {
                            part = 0 ;
                        } else if (num < 1) {
                            part = 1;
                        }(*/

                        if (null != collect && 0 != collect.size()) {
                            int count = getCount(collect);
                            int sum = collect.size() * 100;
                            allCount += count;
                            allSum += sum;
                            System.err.println("===============count:" + count + ",sum:" + sum);
                            partList.add(getPart(count, sum));
                        } else {
                            partList.add(0);
                        }

                    } else {
                        timeList.add(mon + "月");
                        countList.add(0);
                        partList.add(0);
                        response.setQuarterPart(0);
                    }
                    // get quarter satisfaction avg
                    int quarterPart = allSum == 0 ? 0 : getPart(allCount, allSum);
                    response.setQuarterPart(quarterPart);
                }
            } else if (request.getTimeId() == 2 || request.getYear() != null) {
                for (int month = 1; month <= 12; month++) {
                    request.setMonth(month);
                    List<JobCard> allCardCount = cardMapper.getAllCardCount(request);
                    if (null != allCardCount && 0 != allCardCount.size()) {
                        totalCount += allCardCount.size();
                        timeList.add(month + "月");
                        countList.add(allCardCount.size());
                        List<JobCard> collect = allCardCount.stream()
                                .filter(card -> StringUtils.isNotEmpty(card.getSatisfaction())).collect(Collectors.toList());
                        if (null != collect && 0 != collect.size()) {
                            int count = getCount(collect);
                            int sum = collect.size() * 100;
                            allCount += count;
                            allSum += sum;
                            System.err.println("===============count:" + count + ",sum:" + sum);
                            partList.add(getPart(count, sum));
                        } else {
                            partList.add(0);
                        }
                    } else {
                        timeList.add(month + "月");
                        countList.add(0);
                        partList.add(0);
                    }
                }
                // get year satisfaction avg
                int yearPart = allSum == 0 ? 0 : getPart(allCount, allSum);
                response.setYearPart(yearPart);
            }
        }

        response.setTimeList(timeList);
        response.setCountList(countList);
        response.setPartList(partList);
        response.setTotalCount(totalCount);
        return response;
    }

    @Override
    public UserCardCountResponse getDeptCommentCount(CommonRequest request) {
        if (request.getCountId() == null || request.getCountId().intValue() == 0) {
            request.setCountId(3);
        }
        UserCardCountResponse response = new UserCardCountResponse();

        List<JobCard> allCardCount = cardMapper.getAllCardCount(request);
        if (null != allCardCount && 0 != allCardCount.size()) {
            Map<String, Integer> userCountMap = new HashMap<>(); // 部门 及其评价的工单数
            Map<String, Integer> userMap = new HashMap<>(); // 部门 及其 总工单数
            Map<String, Integer> userMap1 = new HashMap<>(); // 部门 及其非评价的工单数
            Map<String, List<JobCard>> userCardMap = new HashMap<>();  // 部门 及其评价的工单列表
            int totalCount = 0;
            for (JobCard card : allCardCount) {
                String dept = "";
                List<DeptInfo> allDept = deptMapper.getAllDept(card.getDeptId());
                List<DeptInfo> completeDept = getCompleteDept(allDept);
                if (null != completeDept && 0 != completeDept.size()) {
                    if (completeDept.get(0).getDeptName() != null) {
                        dept = completeDept.get(0).getDeptName();
                    } else {
                        dept = "其他";
                    }

                } else {
                    dept = "其他";
                }

                if (userCountMap.containsKey(dept)) {
                    if (card.getComment() != null) {
                        userCountMap.put(dept, userCountMap.get(dept) + 1);
                        List<JobCard> cardList = userCardMap.get(dept);
                        cardList.add(card);
                        userCardMap.put(dept, cardList);
                    } else {
                        userMap1.put(dept, userMap1.get(dept) + 1);
                    }
                    userMap.put(dept, userMap.get(dept) + 1);
                } else {
                    if (card.getComment() != null) {
                        List<JobCard> cardList = new ArrayList<>();
                        cardList.add(card);
                        userCardMap.put(dept, cardList);
                        userMap1.put(dept, 0);
                        userCountMap.put(dept, 1);
                    } else {
                        userCountMap.put(dept, 0);
                        userMap1.put(dept, 1);
                        userCardMap.put(dept, new ArrayList<>());
                    }
                    userMap.put(dept, 1);
                }
                totalCount++;
            }
            response.setTotalCount(totalCount);
            List<Map.Entry<String, Integer>> userEntry = new ArrayList<>(userCountMap.entrySet());
            Collections.sort(userEntry, ((u1, u2) -> {
                return u2.getValue().compareTo(u1.getValue());
            }));
            List<Map.Entry<String, Integer>> collect = userEntry.stream().skip(0).limit(5).collect(Collectors.toList());
            Collections.sort(collect, ((u1, u2) -> {
                return u2.getValue().compareTo(u1.getValue());
            }));
            List<String> userList = new ArrayList<>();
            List<Integer> countList = new ArrayList<>();
            List<Integer> unCountList = new ArrayList<>();
            List<Integer> commentList = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : collect) {
                // get dept
                userList.add(entry.getKey());
                // get comment part
                countList.add(userCountMap.get(entry.getKey()));
                // get uncomment part
                unCountList.add(userMap1.get(entry.getKey()));
                // get comment part
                List<JobCard> jobCards = userCardMap.get(entry.getKey());
                if (null != jobCards && 0 != jobCards.size()) {
                    int count = getCount(jobCards);
                    int sum = jobCards.size() * 100;
                    System.err.println("===============count:" + count + ",sum:" + sum);
                    commentList.add(getPart(count, sum));
                } else {
                    commentList.add(0);
                }
            }
            if (commentList.size() < 5) {
                List<DeptInfo> allDept = deptMapper.getAllDept(0);
                List<DeptInfo> someDept = allDept.stream().filter(dept -> dept.getParentId().intValue() != 99
                        || dept.getParentId().intValue() != 88).collect(Collectors.toList());
                List<DeptInfo> completeDept = getCompleteDept(someDept);
                List<String> deptNames = new ArrayList<>();
                for (DeptInfo deptInfo : completeDept) {
                    boolean flag = true;
                    for (String dept : userList) {
                        if (dept.equals(deptInfo.getDeptName())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        deptNames.add(deptInfo.getDeptName());
                    }
                }
                if (null != deptNames && 0 != deptNames.size()) {
                    int rest = 5 - commentList.size();
                    for (int i = 0; i < rest; i++) {
                        userList.add(deptNames.get(i));
                        countList.add(0);
                        unCountList.add(0);
                        commentList.add(0);
                    }
                }
            }
            response.setCountList(countList);
            response.setUserList(userList);
            response.setUnCountList(unCountList);
            response.setCommentList(commentList);
            return response;
        }
        response.setUserList(Arrays.asList("客服物流部", "诺雅克-财务部", "塑壳电器研发部", "低压智能电器研究院-人事行政部", "人力资源部"));
        response.setCountList(Arrays.asList(0, 0, 0, 0, 0));
        response.setUnCountList(Arrays.asList(0, 0, 0, 0, 0));
        response.setCommentList(Arrays.asList(0, 0, 0, 0, 0));
        return response;
    }

    @Override
    public ProblemCardCountResponse getProblemCountInfo(CommonRequest request) {
        if (request.getCountId() == null || request.getCountId().intValue() == 0) {
            request.setCountId(3);
        }
        ProblemCardCountResponse response = new ProblemCardCountResponse();

        List<ProblemCardCount> problemCardCount = cardMapper.getProblemCardCount(request);
        if (null != problemCardCount && 0 != problemCardCount.size()) {

            int totalCount = 0;
            for (ProblemCardCount problem : problemCardCount) {
                String problemName = "其他";
                totalCount += problem.getTotalCount();
                List<ProblemInfo> allProblemInfo = cardMapper.getAllProblemInfo(problem.getProblemType());
                if (null != allProblemInfo && 0 != allProblemInfo.size()) {
                    ProblemInfo problemInfo = allProblemInfo.get(0);
                    if (problemInfo.getParentId() == -1 || !"其他".equals(problemInfo.getTypeName())) {
                        problemName = problemInfo.getTypeName();
                    } else {
                        // 判断是否存在相同故障类型名称
                        List<ProblemInfo> problemInfoByAll = cardMapper.getProblemInfoByAll(new ProblemInfo(problemInfo.getTypeName()));
                        if (null != problemInfoByAll && 0 != problemInfoByAll.size()) {
                            if (problemInfoByAll.size() == 1) {
                                problemName = problemInfo.getTypeName();
                            } else {
                                List<ProblemInfo> allProblemInfo1 = cardMapper.getAllProblemInfo(problemInfo.getParentId());
                                if (null != allProblemInfo1 && 0 != allProblemInfo1.size()) {
                                    problemName = allProblemInfo1.get(0).getTypeName() + "-" + problemInfo.getTypeName();
                                }
                            }
                        }
                    }
                }
                problem.setProblemName(problemName);
            }
            for (ProblemCardCount problem : problemCardCount) {
                int count = getPart(problem.getTotalCount(), totalCount);
                problem.setCount(count);
            }

            if (problemCardCount.size() < 6) {
                List<ProblemInfo> allProblemInfo = cardMapper.getAllProblemInfo(0);
                List<ProblemInfo> someProblemInfo = allProblemInfo.stream().filter(problem -> problem.getParentId().intValue() != -1).collect(Collectors.toList());
                List<ProblemInfo> completeProblem = getCompleteProblem(someProblemInfo);
                List<ProblemInfo> problemInfos = new ArrayList<>();
                for (ProblemInfo problemInfo : completeProblem) {
                    boolean flag = true;
                    for (ProblemCardCount problem : problemCardCount) {
                        if (problem.getProblemName().equals(problemInfo.getTypeName())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        problemInfos.add(problemInfo);
                    }
                }
                if (null != problemInfos && 0 != problemInfos.size()) {
                    int sum = 6 - problemCardCount.size();
                    for (int i = 0; i < sum; i++) {
                        ProblemCardCount problem = new ProblemCardCount();
                        problem.setProblemName(problemInfos.get(i).getTypeName());
                        problem.setCount(0);
                        problemCardCount.add(problem);
                    }
                }
            }
            response.setProblemList(problemCardCount);
            response.setTotalCount(totalCount);
            return response;
        }

        ProblemCardCount problem1 = new ProblemCardCount("会议支持", 0);
        ProblemCardCount problem2 = new ProblemCardCount("网络异常", 0);
        ProblemCardCount problem3 = new ProblemCardCount("OA系统", 0);
        ProblemCardCount problem4 = new ProblemCardCount("打印机", 0);
        ProblemCardCount problem5 = new ProblemCardCount("邮箱配置(手机/电脑)", 0);
        ProblemCardCount problem6 = new ProblemCardCount("SAP密码重置", 0);
        List<ProblemCardCount> problemList = new ArrayList<>();
        problemList.add(problem1);
        problemList.add(problem2);
        problemList.add(problem3);
        problemList.add(problem4);
        problemList.add(problem5);
        problemList.add(problem6);
        response.setProblemList(problemList);
        return response;
    }

    @Override
    public DeptProblemCountResponse getDeptCardCount(CommonRequest request) {
        DeptProblemCountResponse response = new DeptProblemCountResponse();
        if (request.getCountId() == null || request.getCountId().intValue() == 0) {
            request.setCountId(3);
        }
        ProblemCardCountResponse problemCountInfo = getProblemCountInfo(request);
        List<String> problems = new ArrayList<>();
        List<ProblemCardCount> problemList = new ArrayList<>();
        if (problemCountInfo != null) {
            if (null != problemCountInfo.getProblemList() && 0 != problemCountInfo.getProblemList().size()) {
                for (ProblemCardCount problem : problemCountInfo.getProblemList()) {
                    problemList.add(problem);
                    problems.add(problem.getProblemName());
                }
            }
        }
        if (problems.size() < 6) {
            List<ProblemInfo> allProblemInfo = cardMapper.getAllProblemInfo(0);
            List<ProblemInfo> someProblemInfo = allProblemInfo.stream().filter(problem -> problem.getParentId().intValue() != -1).collect(Collectors.toList());
            List<ProblemInfo> completeProblem = getCompleteProblem(someProblemInfo);
            List<ProblemInfo> problemInfos = new ArrayList<>();
            for (ProblemInfo problemInfo : completeProblem) {
                for (String problem : problems) {
                    if (!problem.equals(problemInfo.getTypeName())) {
                        problemInfos.add(problemInfo);
                    }
                }
            }

            List<ProblemInfo> collect = problemInfos.stream().skip(0).limit((6 - problems.size())).collect(Collectors.toList());
            for (ProblemInfo problemInfo : collect) {
                problems.add(problemInfo.getTypeName());
                ProblemCardCount cardCount = new ProblemCardCount();
                cardCount.setProblemType(problemInfo.getTypeId());
                problemList.add(cardCount);
            }
        }
        List<DeptProblem> deptProblems = new ArrayList<>();
        List<ProblemCardCount> deptCardCount = cardMapper.getDeptCardCount(request);
        if (null != deptCardCount && 0 != deptCardCount.size()) {
            int totalCount = 0;
            for (ProblemCardCount dept : deptCardCount) {
                DeptProblem deptProblem = new DeptProblem();
                totalCount++;
                // get deptName
                String deptName = "其他";
                List<DeptInfo> allDept = deptMapper.getAllDept(dept.getDeptId());
                if (allDept != null && 0 != allDept.size()) {
                    DeptInfo deptInfo = allDept.get(0);
                    if (deptInfo.getParentId() == 99 || deptInfo.getParentId() == 88) {
                        deptName = deptInfo.getDeptName() + "-其他";
                    } else {
                        // 判断是否存在相同
                        List<DeptInfo> deptByName = deptMapper.getDeptByName(deptInfo.getDeptName());
                        if (deptByName != null && 0 != deptByName.size()) {
                            if (deptByName.size() == 1) {
                                deptName = deptInfo.getDeptName();
                            } else {
                                List<DeptInfo> allDept1 = deptMapper.getAllDept(deptInfo.getParentId());
                                if (allDept1 != null && 0 != allDept1.size()) {
                                    deptName = allDept1.get(0).getDeptName() + "-" + deptInfo.getDeptName();
                                }
                            }
                        }
                    }
                }
                dept.setDeptName(deptName);
                deptProblem.setDeptName(deptName);
                List<Integer> countList = new ArrayList<>();
                request.setDeptId(dept.getDeptId());
                List<JobCard> allCardCount = cardMapper.getAllCardCount(request);
                if (null != allCardCount && 0 != allCardCount.size()) {
                    if (null != problemList && 0 != problemList.size()) {
                        for (ProblemCardCount problem : problemList) {
                            int count = 0;
                            for (JobCard card : allCardCount) {
                                if (card.getProblemType().equals(problem.getProblemType())) {
                                    count++;
                                }
                            }
                            countList.add(count);
                        }
                    } else {
                        countList.add(0);
                    }
                } else {
                    for (ProblemCardCount problem : problemList) {
                        countList.add(0);
                    }
                }
                deptProblem.setCountList(countList);
                deptProblems.add(deptProblem);
            }
            // 获取所有故障类型列表
            if (deptProblems.size() < 6) {
                // 获取所有部门列表
                List<DeptInfo> allDept = deptMapper.getAllDept(0);
                List<DeptInfo> someDept = allDept.stream().filter(dept -> dept.getParentId().intValue() != 99
                        || dept.getParentId().intValue() != 88).collect(Collectors.toList());
                List<DeptInfo> completeDept = getCompleteDept(someDept);
                List<String> deptNames = new ArrayList<>();
                for (DeptInfo deptInfo : completeDept) {
                    boolean flag = true;
                    for (DeptProblem deptProblem : deptProblems) {
                        if (!deptProblem.getDeptName().equals(deptInfo.getDeptName())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        deptNames.add(deptInfo.getDeptName());
                    }
                }
                if (null != deptNames && 0 != deptNames.size()) {
                    List<String> collect1 = deptNames.stream().skip(0).limit((6 - deptProblems.size())).collect(Collectors.toList());
                    for (String problem : collect1) {
                        DeptProblem deptProblem = new DeptProblem();
                        deptProblem.setDeptName(problem);
                        deptProblem.setCountList(Arrays.asList(0, 0, 0, 0, 0, 0));
                        deptProblems.add(deptProblem);
                    }
                }
            }
            // order by countList No1
            Map<DeptProblem, Integer> problemMap = new HashMap<>();
            for (DeptProblem deptProblem : deptProblems) {
                problemMap.put(deptProblem, deptProblem.getCountList().get(0));
            }
            List<Map.Entry<DeptProblem, Integer>> userEntry = new ArrayList<>(problemMap.entrySet());
            Collections.sort(userEntry, ((u1, u2) -> {
                return u1.getValue().compareTo(u2.getValue());
            }));
            List<DeptProblem> collect = userEntry.stream().map(problem -> {
                return problem.getKey();
            }).collect(Collectors.toList());

            response.setDeptProblems(collect);
            response.setProblems(problems);
            return response;
        }
        problems.addAll(Arrays.asList("会议支持", "网络异常", "OA系统", "邮箱配置(手机/电脑)", "打印机", "SAP密码重置"));
        response.setProblems(problems);
        DeptProblem deptProblem = new DeptProblem("客服物流部", Arrays.asList(0, 0, 0, 0, 0, 0));
        DeptProblem deptProblem1 = new DeptProblem("诺雅克-财务部", Arrays.asList(0, 0, 0, 0, 0, 0));
        DeptProblem deptProblem2 = new DeptProblem("塑壳电器研发部", Arrays.asList(0, 0, 0, 0, 0, 0));
        DeptProblem deptProblem3 = new DeptProblem("低压智能电器研究院-人事行政部", Arrays.asList(0, 0, 0, 0, 0, 0));
        DeptProblem deptProblem4 = new DeptProblem("人力资源部", Arrays.asList(0, 0, 0, 0, 0, 0));
        DeptProblem deptProblem5 = new DeptProblem("国内销售部", Arrays.asList(0, 0, 0, 0, 0, 0));
        deptProblems.add(deptProblem);
        deptProblems.add(deptProblem1);
        deptProblems.add(deptProblem2);
        deptProblems.add(deptProblem3);
        deptProblems.add(deptProblem4);
        deptProblems.add(deptProblem5);
        response.setDeptProblems(deptProblems);
        return response;
    }

    private List<ProblemInfo> getCompleteProblem(List<ProblemInfo> problemInfos) {
        for (ProblemInfo problem : problemInfos) {
            String problemName = "其他";
            List<ProblemInfo> allProblemInfo = cardMapper.getAllProblemInfo(problem.getTypeId());
            if (null != allProblemInfo && 0 != allProblemInfo.size()) {
                ProblemInfo problemInfo = allProblemInfo.get(0);
                if (problemInfo.getParentId() == -1 || !"其他".equals(problemInfo.getTypeName())) {
                    problemName = problemInfo.getTypeName();
                } else {
                    // 判断是否存在相同故障类型名称
                    List<ProblemInfo> problemInfoByAll = cardMapper.getProblemInfoByAll(new ProblemInfo(problemInfo.getTypeName()));
                    if (null != problemInfoByAll && 0 != problemInfoByAll.size()) {
                        if (problemInfoByAll.size() == 1) {
                            problemName = problemInfo.getTypeName();
                        } else {
                            List<ProblemInfo> allProblemInfo1 = cardMapper.getAllProblemInfo(problemInfo.getParentId());
                            if (null != allProblemInfo1 && 0 != allProblemInfo1.size()) {
                                problemName = allProblemInfo1.get(0).getTypeName() + "-" + problemInfo.getTypeName();
                            }
                        }
                    }
                }
            }
            problem.setTypeName(problemName);
        }
        return problemInfos;
    }

    private List<DeptInfo> getCompleteDept(List<DeptInfo> deptInfos) {
        for (DeptInfo dept : deptInfos) {
            // get deptName
            String deptName = "其他";
            List<DeptInfo> allDept = deptMapper.getAllDept(dept.getDeptId());
            if (allDept != null && 0 != allDept.size()) {
                DeptInfo deptInfo = allDept.get(0);
                if (deptInfo.getParentId() == 99 || deptInfo.getParentId() == 88) {
                    deptName = deptInfo.getDeptName() + "-其他";
                } else {
                    // 判断是否存在相同
                    List<DeptInfo> deptByName = deptMapper.getDeptByName(deptInfo.getDeptName());
                    if (deptByName != null && 0 != deptByName.size()) {
                        if (deptByName.size() == 1) {
                            deptName = deptInfo.getDeptName();
                        } else {
                            List<DeptInfo> allDept1 = deptMapper.getAllDept(deptInfo.getParentId());
                            if (allDept1 != null && 0 != allDept1.size()) {
                                deptName = allDept1.get(0).getDeptName() + "-" + deptInfo.getDeptName();
                            }
                        }
                    }
                }
            }
            dept.setDeptName(deptName);
        }
        return deptInfos;
    }

    @Override
    public List<ProblemService> getProblemService() {
        try {
            return cardMapper.getProblemService();
        } catch (Exception ex) {
            log.info("get problem service list failed:{}", ex);
            return null;
        }
    }

    @Override
    public List<CommentPart> getCommentPart(CommonRequest request) {
        if (request.getCountId() == null || request.getCountId().intValue() == 0) {
            request.setCountId(3);
        }
        CommentPart manYi = new CommentPart("满意", 0, 0);
        CommentPart yiBan = new CommentPart("一般", 0, 0);
        CommentPart cha = new CommentPart("差", 0, 0);
        CommentPart weiPing = new CommentPart("未评价", 0, 0);
        List<JobCard> allCard = cardMapper.getAllCardCount(request);
        if (null != allCard && 0 != allCard.size()) {
            for (JobCard card : allCard) {
                if (card.getSatisfaction() != null) {
                    if (Integer.valueOf(card.getSatisfaction()) >= 4) {
                        manYi.setCount(manYi.getCount() + 1);
                    } else if (Integer.valueOf(card.getSatisfaction()) >= 2) {
                        yiBan.setCount(yiBan.getCount());
                    } else {
                        cha.setCount(cha.getCount() + 1);
                    }
                } else {
                    weiPing.setCount(weiPing.getCount() + 1);
                }
            }
            manYi.setPart(getPart(manYi.getCount(), allCard.size()));
            yiBan.setPart(getPart(manYi.getCount(), allCard.size()));
        }
        return null;
    }

    @Override
    public int updateProblemTypeNo(ProblemInfo problemInfo) {
        List<ProblemInfo> allProblemInfo = cardMapper.getAllProblemInfo(problemInfo.getTypeId());
        if (null != allProblemInfo && 0 != allProblemInfo.size()) {
            ProblemInfo problem = allProblemInfo.get(0);
            int typeNo = problem.getTypeNo();
            problem.setTypeNo(problemInfo.getTypeNo());
            problem.setTypeName(Optional.ofNullable(problemInfo.getTypeName()).orElse(""));
            try {
                ProblemInfo problem1 = new ProblemInfo();
                problem1.setParentId(problem.getParentId());
                List<ProblemInfo> problemInfoByAll = cardMapper.getProblemInfoByAll(problem1);
                List<ProblemInfo> collect = problemInfoByAll.stream()
                        .filter(data -> !data.getTypeId().equals(problemInfo.getTypeId())).collect(Collectors.toList());
                if (null != collect && collect.size() != 0) {
                    List<ProblemInfo> collect1 = collect.stream().filter(data -> data.getTypeNo().equals(problemInfo.getTypeNo())).collect(Collectors.toList());
                    if (null != collect1 && 0 != collect1.size()) {
                        for (ProblemInfo pro : collect1) {
                            pro.setTypeNo(typeNo);
                            cardMapper.updateProblemNo(pro);
                        }
                    }
                    cardMapper.updateProblemNo(problem);
                } else {
                    cardMapper.updateProblemNo(problem);
                }
                return 1;
            } catch (Exception ex) {
                log.info("update problem:{} type no failed:{}", problemInfo, ex);
                return 0;
            }
        } else {
            return 2;
        }
    }

    @Override
    public List<ServiceCardCountResponse> getServiceCount(CommonRequest request) {
        List<ServiceCardCountResponse> response = new ArrayList<>();
        if ((request.getYear() == null || request.getYear().intValue() == 0)
                && (request.getQuarter() == null || request.getQuarter().intValue() == 0)) {
            request.setCountId(4);
        }
        if (request.getQuarter() != null && request.getQuarter() == 5) {
            request.setQuarter(0);
            if (request.getYear() == null || "".equals(request.getYear())) {
                request.setCountId(5);
            }
            /*Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            log.info("get {} all service way part");
            request.setYear(year);*/
        }
        List<JobCard> allCardCount = cardMapper.getAllCardCount(request);
        if (null != allCardCount && 0 != allCardCount.size()) {
            // 获取现场服务信息
            int xianCount = 0;
            int yuanCount = 0;
            for (JobCard card : allCardCount) {
                if (card.getDealWay() != null) {
                    if (card.getDealWay().intValue() == 2) {
                        xianCount++;
                    } else if (card.getDealWay().intValue() == 1) {
                        yuanCount++;
                    }
                }
            }
            if (xianCount != 0) {
                ServiceCardCountResponse serviceCardCountResponse = new ServiceCardCountResponse("现场服务", xianCount);
                response.add(serviceCardCountResponse);
            } else {
                ServiceCardCountResponse serviceCardCountResponse = new ServiceCardCountResponse("现场服务", 0);
                response.add(serviceCardCountResponse);
            }

            if (yuanCount != 0) {
                ServiceCardCountResponse serviceCardCountResponse = new ServiceCardCountResponse("远程服务", yuanCount);
                response.add(serviceCardCountResponse);
            } else {
                ServiceCardCountResponse serviceCardCountResponse = new ServiceCardCountResponse("远程服务", 0);
                response.add(serviceCardCountResponse);
            }
            return response;
        }
        ServiceCardCountResponse serviceCardCountResponse = new ServiceCardCountResponse("现场服务", 0);
        response.add(serviceCardCountResponse);
        ServiceCardCountResponse serviceCardCountResponse1 = new ServiceCardCountResponse("远程服务", 0);
        response.add(serviceCardCountResponse1);
        return response;
    }

    @Override
    public CardListResponse getUnDealCardList(CommonRequest request) {
        CardListResponse response = new CardListResponse();
        if (request.getCountId() == null || request.getCountId().intValue() == 0) {
            request.setCountId(1);
        }
        List<JobCard> allCardCount = cardMapper.getAllCardCount(request);
        if (allCardCount != null && 0 != allCardCount.size()) {
            List<JobCard> collect = allCardCount.stream().filter(card -> card.getStatusId() <= 3).collect(Collectors.toList());
            if (collect != null && 0 != collect.size()) {
                collect.sort((c1, c2) -> c2.getCreateTime().compareTo(c1.getCreateTime()));
                List<JobCardDetail> collect1 = collect.stream().map(card -> {
                    return getJobCardDetail(card);
                }).collect(Collectors.toList());
                if (collect1 != null && 0 != collect1.size()) {
                    collect1.stream().forEach(card -> {
                        String createTime = card.getCreateTime();
                        String newTime = createTime.substring(createTime.indexOf(" ") + 1, createTime.length());
                        //System.err.println("----newTime: " + newTime);
                        card.setCreateTime(newTime);
                        if (card.getAssistName() != null) {
                            card.setDeal(card.getAssistName());
                        }
                    });
                    response.setCardList(collect1);
                    response.setTotalCount(collect.size());
                }
            }
            return response;
        }
        response.setCardList(new ArrayList<>());
        return response;
    }

    @Override
    public EngCommentResponse getEngComment(CommonRequest request) {
        EngCommentResponse response = new EngCommentResponse();
        List<EngComment> engComments = new ArrayList<>();

        if ((request.getYear() == null || request.getYear().intValue() == 0)
                && (request.getQuarter() == null || request.getQuarter().intValue() == 0)) {
            // default now quarter
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH);
            int quarter = month / 3 + 1;
            request.setQuarter(quarter);
        }
        // 获取所有工程师用户信息
        List<UserInfo> assistList = cardMapper.getAssistUser(request);

        List<JobCard> engComment = cardMapper.getEngComment(request);
        Map<String, List<JobCard>> userMap = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();
        if (null != engComment && 0 != engComment.size()) {
            for (JobCard card : engComment) {
                if (userMap.containsKey(card.getDealName())) {
                    List<JobCard> jobCards = userMap.get(card.getDealName());
                    jobCards.add(card);
                    userMap.put(card.getDealName(), jobCards);
                    countMap.put(card.getDealName(), countMap.get(card.getDealName()) + 1);
                } else {
                    List<JobCard> jobCards = new ArrayList<>();
                    jobCards.add(card);
                    userMap.put(card.getDealName(), jobCards);
                    countMap.put(card.getDealName(), 1);
                }
            }

            for (Map.Entry<String, List<JobCard>> entry : userMap.entrySet()) {
                EngComment comment = new EngComment();
                comment.setEngName(entry.getKey());
                int count = getCount(entry.getValue());
                int sum = entry.getValue().size() * 100;
                int part = getPart(count, sum);
                comment.setPart(part);
                comment.setCount(entry.getValue().size());
                comment.setQuarter(request.getQuarter());
                engComments.add(comment);
            }

            for (UserInfo user : assistList) {
                if (!userMap.containsKey(user.getUserName())) {
                    EngComment comment = new EngComment();
                    comment.setEngName(user.getUserName());
                    comment.setQuarter(request.getQuarter());
                    engComments.add(comment);
                }
            }
            response.setTotalCount(engComments.size());
            engComments.sort((e1, e2) -> e2.getPart().compareTo(e1.getPart()));
        } else {
            for (UserInfo user : assistList) {
                if (!userMap.containsKey(user.getUserName())) {
                    EngComment comment = new EngComment();
                    comment.setEngName(user.getUserName());
                    comment.setQuarter(request.getQuarter());
                    engComments.add(comment);
                }
            }

        }
        int pageStart = (request.getPageNo() - 1) * request.getPageSize();
        List<EngComment> collect = engComments.stream().skip(pageStart).limit(request.getPageSize()).collect(Collectors.toList());
        response.setEngComments(collect);
        response.setTotalCount(assistList.size());
        return response;
    }

    @Override
    public int sendEmailForComment(SendCardRequest request) {
        if (null != request && null != request.getCardList()
                && 0 != request.getCardList().size()) {
            for (String cardId : request.getCardList()) {
                JobCard card = cardMapper.getUserCardDetail(cardId);
                // send email to user give a comment
                try {

                    if (null != card.getApplyId()) {
                        String toEmail = "";
                        List<UserPhone> userPhone = userMapper.getUserPhone(card.getApplyId());
                        if (null != userPhone && 0 != userPhone.size()) {
                            for (UserPhone user : userPhone) {
                                if (null != user.getEmail()) {
                                    toEmail = user.getEmail();
                                    break;
                                }
                            }
                        }
                        if (!"".equals(toEmail)) {
                            String nowProject = this.project.equals("prod") ? "线上" : "测试";
                            List<String> toList = new ArrayList<>();
                            toList.add(toEmail);
                            String subject = nowProject + "-IT服务中心工单已处理通知";
                            JobCardDetail jobCardDetail = getJobCardDetail(card);
                            String message = jobCardDetail.getApplyName() + "，您好，您在IT服务中心小程序创建的单号为：" + jobCardDetail.getCardId() + " 的故障单已由" + jobCardDetail.getDeal() + "处理，请登录小程序给个好评，感谢您的支持！";
                            ExchangeMailUtil.send(subject, toList, message);
                            return 1;
                        } else {
                            return 2;
                        }
                    }
                } catch (Exception ex) {
                    log.info("send message to deal:{} failed:{}", card.getDeal(), ex);
                    return 0;
                }
            }
        }
        return 0;
    }

    // 获取总满意度评分
    private Integer getCount(List<JobCard> jobCards) {
        int count = 0;
        for (JobCard card : jobCards) {
            if (Integer.valueOf(card.getSatisfaction()) == 2) {
                count += 30;
            } else if (Integer.valueOf(card.getSatisfaction()) == 3) {
                count += 60;
            } else if (Integer.valueOf(card.getSatisfaction()) == 4) {
                count += 80;
            } else if (Integer.valueOf(card.getSatisfaction()) == 5) {
                count += 100;
            }
            /*if (card.getSatisfaction() != null) {
                count += Integer.valueOf(card.getSatisfaction()).intValue();
            }*/
        }
        return count;
    }

    // 获取百分比
    private Integer getPart(Integer count, Integer sum) {
        int count1 = count * 100 / sum;
        double num = count * 100.0 / sum;
        if (num == 0.0) {
            count1 = 0;
        } else if (num < 1) {
            count1 = 1;
        }
        return count1;
    }

    private List<Integer> getAllMonth(int month, int quarter) {
        if (month != 0) {
            switch (month) {
                case 1:
                case 2:
                case 3:
                    return new ArrayList<>(Arrays.asList(1, 2, 3));
                case 4:
                case 5:
                case 6:
                    return new ArrayList<>(Arrays.asList(4, 5, 6));
                case 7:
                case 8:
                case 9:
                    return new ArrayList<>(Arrays.asList(7, 8, 9));
                case 10:
                case 11:
                case 12:
                    return new ArrayList<>(Arrays.asList(10, 11, 12));
                default:
                    return new ArrayList<>(Arrays.asList(1, 2, 3));
            }
        } else {
            switch (quarter) {
                case 1:
                    return new ArrayList<>(Arrays.asList(1, 2, 3));
                case 2:
                    return new ArrayList<>(Arrays.asList(4, 5, 6));
                case 3:
                    return new ArrayList<>(Arrays.asList(7, 8, 9));
                case 4:
                    return new ArrayList<>(Arrays.asList(10, 11, 12));
                default:
                    return new ArrayList<>(Arrays.asList(1, 2, 3));
            }
        }
    }

    private List<JobCardDetail> getJobCardDetailList(JobCard userCardDetail, String openId) {
        try {
            cardMapper.updateCardByEng(userCardDetail);

            List<JobCard> userCardList = cardMapper.getUserCardList(openId);
            List<JobCardDetail> jobCardDetailList = new ArrayList<>();
            if (null != userCardList && 0 != userCardList.size()) {
                if (userCardDetail.getStatusId() == 3) {  // deal enginner receive card
                    for (JobCard card : userCardList) {
                        if (userCardDetail.getStatusId() == 3) {
                            JobCardDetail jobCardDetail = getJobCardDetail(card);
                            jobCardDetail.setStatus(StatusEnum.WUNDEAL.getStatus());
                            jobCardDetailList.add(jobCardDetail);
                        }
                    }
                } else {
                    for (JobCard card : userCardList) {
                        if (card.getProcessNode().intValue() <= ProcessEnum.CARD_DEAL.getProcess()) {
                            JobCardDetail jobCardDetail = getJobCardDetail(card);
                            jobCardDetail.setStatus(StatusEnum.WUNDEAL.getStatus());
                            jobCardDetailList.add(jobCardDetail);
                        }
                    }
                }
            }
            jobCardDetailList.sort((c1, c2) -> c2.getCreateTime().compareTo(c1.getCreateTime()));
            // System.out.println("----------------order by createTime" + cardList);
            List<JobCardDetail> collect = jobCardDetailList.stream().limit(10).collect(Collectors.toList());
            collect.forEach(data -> {
                System.out.println(data);
            });
            return collect;
        } catch (Exception ex) {
            log.info("openId:{} update card by enginner failed:{}", openId, ex);
            return null;
        }
    }

    // 获取未处理单数最少的话务员 和 工程师
    private UserInfo getOperator(int typeId, String roleName, String company) {
        // get all card by openId
        Map<UserInfo, Integer> userMap = new HashMap<>();
        Set<Integer> userList = new HashSet<>();
        if (roleName.equals(RoleEnum.ENGINEER.getName())) {
            // get service by type
            List<UserInfo> userInfoByRole = getAssistList(new AssistInfoRequest());
            log.info("userInfoByRole: {}", userInfoByRole);
            for (UserInfo user : userInfoByRole) {
                UserStatus userStatus = userMapper.getUserStatus(user.getOpenId());
                //System.err.println("userStatus:" + userStatus);
                if (null != userStatus && userStatus.getStatusId() == 1) { // 获取在线的话务员或工程师
                    int cardCountById = cardMapper.getCardCountById(user.getOpenId());
                    userList.add(cardCountById);
                    userMap.put(user, cardCountById);
                }
            }
            if (null != userList && 0 != userList.size()) {
                // 获取工作量最少的工程师 或 话务员
                int minNum = Collections.min(new ArrayList<>(userList));
                for (Map.Entry<UserInfo, Integer> entry : userMap.entrySet()) {
                    if (entry.getValue().intValue() == minNum) {
                        return entry.getKey();
                    }
                }
            }
            // 设置默认处理工程师
            return new UserInfo("oa1oF5RP8G_-Rfe9wGCa6pi13hGw");
        }
//        else {
//            // get all operator by role name
//            List<UserInfo> userInfoByRole = userMapper.getUserInfoByRole(roleName);
//            if (null != userInfoByRole && 0 != userInfoByRole.size()) {
//                for (UserInfo user : userInfoByRole) {
//                    int cardCountById = cardMapper.getCardCountById(user.getOpenId());
//                    userList.add(cardCountById);
//                    userMap.put(user, cardCountById);
//                }
//            }
//            if (null != userList && 0 != userList.size()) {
//                // 获取工作量最少的工程师 或 话务员
//                int minNum = (int) Collections.min(new ArrayList<>(userList));
//                for (Map.Entry<UserInfo, Integer> entry : userMap.entrySet()) {
//                    if (entry.getValue().intValue() == minNum) {
//                        return entry.getKey();
//                    }
//                }
//            }
//            // 设置默认处理话务员
//            return new UserInfo("oAh3O4kQm93o0iYBWSWpVdlCMgVU");
//        }
        return new UserInfo("oa1oF5RP8G_-Rfe9wGCa6pi13hGw");
    }

    private String getUserName(String openId) {
        UserInfo userInfo = userMapper.getUserInfo(openId);
        if (userInfo != null) {
            return userInfo.getUserName();
        }
        return "未命名";
    }

    private UserInfo getUserOpenId(String userName) {
        UserInfo userInfo = userMapper.getUserInfoByName(userName);
        if (userInfo != null) {
            return userInfo;
        }
        return new UserInfo();
    }

    private String getProcessName(int nodeId) {
        ProcessInfo processInfo = cardMapper.getProcessInfo(nodeId);
        if (null != processInfo) {
            return processInfo.getNodeName();
        }
        return "其它";
    }

    // 返回一个十五位的唯一工单号
    public String getCardId() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String format = df.format(date);

        Random ne = new Random(); //实例化一个random的对象ne
        int x = ne.nextInt(999 - 100 + 1) + 100; //为变量赋随机值1000-9999
        String random_order = String.valueOf(x);
        String cardId = format + random_order;
        // if cardId exist then create again
        List<JobCard> userCardList = cardMapper.getUserCardList("");
        if (null != userCardList && userCardList.size() != 0) {
            List<JobCard> collect = new ArrayList<>();
            for (JobCard card : userCardList) {
                if (card.getCardId().equals(cardId)) {
                    collect.add(card);
                }
            }
            if (collect == null || collect.size() == 0) {
                return cardId;
            } else {
                cardId = getCardId();
            }
        } else {
            return cardId;
        }
        return cardId;
    }

    private String getHandCardId() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String format = df.format(date);

        Random ne = new Random(); //实例化一个random的对象ne
        int x = ne.nextInt(999 - 100 + 1) + 100; //为变量赋随机值1000-9999
        String random_order = String.valueOf(x);
        String cardId = format + random_order;
        // if cardId exist then create again
        List<HandCard> userCardList = cardMapper.getHandCardList("");
        if (null != userCardList && userCardList.size() != 0) {
            List<HandCard> collect = new ArrayList<>();
            for (HandCard card : userCardList) {
                if (card.getCardId().equals(cardId)) {
                    collect.add(card);
                }
            }
            if (collect == null || collect.size() == 0) {
                return cardId;
            } else {
                cardId = getCardId();
            }
        } else {
            return cardId;
        }
        return cardId;
    }
}

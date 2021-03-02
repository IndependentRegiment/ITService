package com.zt.util;

import com.zt.dao.CardMapper;
import com.zt.dao.UserMapper;
import com.zt.entity.common.JobCard;
import com.zt.entity.common.JobCardDetail;
import com.zt.entity.common.UserPhone;
import com.zt.entity.request.SeeCardRequest;
import com.zt.service.impl.CardServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class QuartzConfig {

    private static Map<String, Integer> countMap = new LinkedHashMap<>();

    private static Logger log = LoggerFactory.getLogger(CardServiceImpl.class);

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CardServiceImpl cardService;

    @Value("${spring.profiles.active}")
    private String project;

    public static QuartzConfig quartzConfig;
    @PostConstruct
    public void init() {
        quartzConfig = this;
    }

    @Scheduled(cron = "0 0/1 6-21 * * ?")   // 每天的6点到21每隔一分钟触发一次 cron = "0 0/1 6-21 * * ?"
    public void noticeDeal() {
        List<JobCard> allCard = quartzConfig.cardMapper.getAllCard(new SeeCardRequest());
        if (null != allCard && 0 != allCard.size()) {
            List<JobCard> collect = allCard.stream().filter(card -> card.getProcessNode().intValue() < 3).collect(Collectors.toList());
            if (null != collect && 0 != collect.size()) {
                List<JobCard> engList = new ArrayList<>();
                for (JobCard card : collect) {
                    String createTime = card.getCreateTime();
                    Timestamp timeStampByStr = DateUtil.getTimeStampByStr(createTime);
                    long time = timeStampByStr.getTime();
                    long nowTime = System.currentTimeMillis();
                    long hourTime = 3600 * 1000;
                    if (nowTime - time >= hourTime) {
                        engList.add(card);
                    }
                }


                String nowProject = quartzConfig.project.equals("prod") ? "线上" : "测试";
                // send email to eng
                if (null != engList && 0 != engList.size()) {
                    for (JobCard card : engList) {
                        if (nowProject.equals("测试")) {
                            break;
                        }
                        if (countMap.containsKey(card.getCardId())) {
                            if (countMap.get(card.getCardId()) >= 1) {
                                continue;
                            }
                            countMap.put(card.getCardId(), countMap.get(card.getCardId()) + 1);
                        } else {
                            countMap.put(card.getCardId(), 1);
                        }
                        System.err.println("===card:" + card);
                        String toEmail = "hzhehao@chint.com";

                        List<UserPhone> userPhone = quartzConfig.userMapper.getUserPhone(card.getDeal());
                        if (null != userPhone && 0 != userPhone.size()) {
                            for(UserPhone user : userPhone) {
                                if (null != user.getEmail()) {
                                    toEmail = user.getEmail();
                                    break;
                                }
                            }
                        }

                        List<String> toList = new ArrayList<>();
                        toList.add(toEmail);
                        String subject = project + "-IT服务中心待处理工单超时通知";
                        JobCardDetail jobCardDetail = quartzConfig.cardService.getJobCardDetail(card);
                        String message = "工单号: "+ jobCardDetail.getCardId() +"已超出1小时尚未处理,工单申请人：" + jobCardDetail.getApplyName()
                                + ",故障类型: " + jobCardDetail.getProblemType() +",当前总待处理工单数: " + card.getWaitCount() + ",请尽快处理！";
                        try {
                            log.info("{} start send email to:{}", project, toEmail);
                            ExchangeMailUtil.send(subject, toList, message);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.info("send email to {} failed:{}", toEmail, e);
                        }
                    }
                }
            }
        }
    }

    //@Scheduled(cron = "0 0 6-21 * * ?")   // 每天的6点到21每隔一分钟触发一次 cron = "0 0/1 6-21 * * ?"
    public void noticeDeal2() {
        List<JobCard> allCard = quartzConfig.cardMapper.getAllCard(new SeeCardRequest());
        if (null != allCard && 0 != allCard.size()) {
            List<JobCard> collect = allCard.stream().filter(card -> card.getProcessNode().intValue() < 3).collect(Collectors.toList());
            if (null != collect && 0 != collect.size()) {
                List<JobCard> engList = new ArrayList<>();
                for (JobCard card : collect) {
                    String createTime = card.getCreateTime();
                    Timestamp timeStampByStr = DateUtil.getTimeStampByStr(createTime);
                    long time = timeStampByStr.getTime();
                    long nowTime = System.currentTimeMillis();
                    long hourTime = 3600 * 1000;
                    if (nowTime - time >= hourTime) {
                        engList.add(card);
                    }
                }


                String nowProject = quartzConfig.project.equals("prod") ? "线上" : "测试";
                // send email to eng
                if (null != engList && 0 != engList.size()) {
                    for (JobCard card : engList) {
                        if (nowProject.equals("测试")) {
                            break;
                        }
                        if (countMap.containsKey(card.getCardId())) {
                            if (countMap.get(card.getCardId()) >= 1) {
                                continue;
                            }
                            countMap.put(card.getCardId(), countMap.get(card.getCardId()) + 1);
                        } else {
                            countMap.put(card.getCardId(), 1);
                        }
                        System.err.println("===card:" + card);
                        String toEmail = "hzhehao@chint.com";

                        List<UserPhone> userPhone = quartzConfig.userMapper.getUserPhone(card.getDeal());
                        if (null != userPhone && 0 != userPhone.size()) {
                            for(UserPhone user : userPhone) {
                                if (null != user.getEmail()) {
                                    toEmail = user.getEmail();
                                }
                            }
                        }

                        List<String> toList = new ArrayList<>();
                        toList.add(toEmail);
                        String subject = project + "-IT服务中心待处理工单超时通知";
                        JobCardDetail jobCardDetail = quartzConfig.cardService.getJobCardDetail(card);
                        String message = "工单号: "+ jobCardDetail.getCardId() +"已超出1小时尚未处理,工单申请人：" + jobCardDetail.getApplyName()
                                + ",故障类型: " + jobCardDetail.getProblemType() +",当前总待处理工单数: " + card.getWaitCount() + ",请尽快处理！";
                        try {
                            log.info("{} start send email to:{}", project, toEmail);
                            ExchangeMailUtil.send(subject, toList, message);
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.info("send email to {} failed:{}", toEmail, e);
                        }
                    }
                }
            }
        }
    }
}

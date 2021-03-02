package com.zt.controller;

import com.zt.config.CommonResult;
import com.zt.entity.common.*;
import com.zt.entity.request.*;
import com.zt.entity.response.CardListResponse;
import com.zt.entity.response.HandCardListResponse;
import com.zt.entity.response.HandCardResponse;
import com.zt.entity.response.ProblemInfoResponse;
import com.zt.service.BackCardService;
import com.zt.service.CardService;
import com.zt.util.GetOpenIdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 功能描述: <br>
 * 〈工单接口〉
 * @Author:
 * @Date:
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/api1.0/card", method = RequestMethod.POST)
@Api(value = "工单信息管理")
public class CardController {

    private static Logger log = LoggerFactory.getLogger(CardController.class);

    @Autowired
    private BackCardService backCardService;

    @Autowired
    private CardService cardService;

    @ApiOperation(value = "查看个人工单列表", notes = "openId：用户openId(必传)，" +
            "userRole：用户角色 值为话务员、普通用户、工程师(必传)，cardType：列表类型 值位已代建、已处理、已评价等等(必传)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "userRole", value = "用户角色 值为话务员、普通用户、工程师", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "cardType", value = "列表类型 值位已代建、已处理、已评价等等", required = true, dataType = "string", paramType = "body")
    })*/
    @RequestMapping("/list")
    public CommonResult getUserCardList(@RequestBody SeeCardRequest request) {
        log.info("start /card/list get openId:{} user card list", request.getOpenId());
        if (null == request.getOpenId() || 0 == request.getPageSize()) {
            return CommonResult.failed("openId 或 pageNo不能为空！");
        }
        CardListResponse cardList = cardService.getCardList(request);
        if (null != cardList) {
            log.info("get user card list info:{} success", cardList);
            return CommonResult.success(cardList);
        }
        return CommonResult.failed("获取列表信息失败!");
    }

    @ApiOperation(value = "创建工单", notes = "openId：用户openId(必传)")
    @RequestMapping("/create")
    public CommonResult createCard(@RequestBody CreateCardRequest request) {
        log.info("start /card/create create openId:{} card", request.getOpenId());
        int jobCard = cardService.createJobCard(request);

        if (jobCard == 1) {
            log.info("openId:{} create card success", request.getOpenId());
            // add log
            LogInfo logInfo = new LogInfo(request.getOpenId(), "新增", "创建故障单", "操作成功");
            backCardService.addLog(logInfo);
            return CommonResult.success(jobCard);
        } else {
            String message = "创建故障单失败！";
            if (jobCard ==2) {
                message = "用户名不存在！";
            } else if (jobCard == 3) {
                message = "1小时内相同故障工单只能创建三个,请更改故障类型！";
            } else if (jobCard == 4) {
                message = "不能为非用户代建工单！";
            } else if (jobCard == 5) {
                message = "请填写公司手机号或座机号！";
            } else if (jobCard == 6) {
                message = "请填写正确的公司手机号或座机号！";
            }
            LogInfo logInfo = new LogInfo(request.getOpenId(), "新增", "创建故障单：" + message , "操作失败");
            backCardService.addLog(logInfo);
            return CommonResult.failed(message);
        }
    }

    @ApiOperation(value = "获取故障类型列表", notes = "openId：用户openId(必传)")
    @RequestMapping("/problem/list")
    public CommonResult getProblems(@RequestBody CommonRequest request) {
        log.info("start /card/problem/list get openId:{} problem list", request.getOpenId());
        List<ProblemInfoResponse> problemList = cardService.getProblemList(request);
        if (null != problemList && 0 != problemList.size()) {
            log.info("get openId:{} problem list:{} success", request.getOpenId(), problemList);
            return CommonResult.success(problemList);
        }
        log.error("get openId:{} problem list failed", request.getOpenId());
        return CommonResult.failed("获取故障类型列表失败！");
    }

    @ApiOperation(value = "获取工单详情", notes = "openId：用户openId(必传)")
    @RequestMapping("/detail")
    public CommonResult getCardDetail(@RequestBody SeeCardDetailRequest request) {
        log.info("start /card/detail get openId:{} card:{} detail", request.getOpenId(), request.getCardId());
        JobCardDetail jobCardInfo = cardService.getJobCardInfo(request);
        if (null != jobCardInfo) {
            log.info("get openId:{} card detail:{} success", request.getOpenId(), jobCardInfo);
            return CommonResult.success(jobCardInfo);
        }
        log.error("start get openId:{} card detail failed", request.getOpenId());
        return CommonResult.failed("获取故障单详情失败！");
    }

    @ApiOperation(value = "工程师处理工单", notes = "openId：用户openId(必传)")
    @RequestMapping("/update")
    public CommonResult updateCard(@RequestBody ChangeCardRequest request) {
        log.info("start /card/update openId:{} update card by info:{}", request.getOpenId(), request);
        List<JobCardDetail> jobCardDetailList = cardService.updateCardInfo(request);
        if (null != jobCardDetailList) {
            log.info("openId:{} update card by info:{} success", request.getOpenId(), request);
            // add log
            LogInfo logInfo = new LogInfo(request.getOpenId(), "修改", "工程师处理工单", "操作成功");
            backCardService.addLog(logInfo);
            return CommonResult.success(jobCardDetailList);
        }
        LogInfo logInfo = new LogInfo(request.getOpenId(), "修改", "工程师处理工单", "操作失败");
        backCardService.addLog(logInfo);
        return CommonResult.failed("修改故障单失败！");
    }

    @ApiOperation(value = "获取工程师列表", notes = "openId：用户openId(必传)")
    @RequestMapping("/assist/list")
    public CommonResult getAssistInfo(@RequestBody AssistInfoRequest request) {
        log.info("start /card/assist/list openId get assist list");
        List<UserInfo> assistList = cardService.getAssistList(request);
        if (null != assistList && 0 != assistList.size()) {
            log.info("openId:{} get assist list:{} success", request.getOpenId(), assistList);
            return CommonResult.success(assistList);
        }
        return CommonResult.failed("获取协办人列表失败");
    }

    @ApiOperation(value = "获取工程师列表", notes = "openId：用户openId(必传)")
    @RequestMapping("/comment")
    public CommonResult updateCard2(@RequestBody CommentCardRequest request) {
        log.info("start /card/comment openId:{} change card by info:{}", request.getOpenId(), request);
        List<JobCardDetail> jobCardDetailList = cardService.updateCardByComment(request);
            if (null != jobCardDetailList) {
            log.info("openId:{} change card by info:{} success", request.getOpenId(), request);
            return CommonResult.success(jobCardDetailList);
        }
        return CommonResult.failed("用户评价失败！");
    }

    @ApiOperation(value = "工程师接单或协办工单", notes = "openId：用户openId(必传)")
    @RequestMapping("/deal")
    public CommonResult receiveCard(@RequestBody ChangeCardRequest request) {
        log.info("start /card/deal openId:{} receive card by info:{}", request.getOpenId(), request);
        List<JobCardDetail> jobCardDetailList = cardService.receiveOneCard(request);
        if (null != jobCardDetailList) {
            log.info("openId:{} receive card by info:{} success", request.getOpenId(), request);

            if (null != request.getAssist()) {
                LogInfo logInfo = new LogInfo(request.getOpenId(), "修改", "工程师协办->" + request.getAssist(), "操作成功");
                backCardService.addLog(logInfo);
            } else {
                LogInfo logInfo = new LogInfo(request.getOpenId(), "修改", "工程师接单", "操作成功");
                backCardService.addLog(logInfo);
            }
            return CommonResult.success(jobCardDetailList);
        }
        if (null != request.getAssist()) {
            LogInfo logInfo = new LogInfo(request.getOpenId(), "修改", "工程师协办->" + request.getAssist(), "操作失败");
            backCardService.addLog(logInfo);
        } else {
            LogInfo logInfo = new LogInfo(request.getOpenId(), "修改", "工程师接单", "操作失败");
            backCardService.addLog(logInfo);
        }
        return CommonResult.failed();
    }

    /*@RequestMapping("/assist")
    public CommonResult assistCard(@RequestBody ChangeCardRequest request) {
        log.info("start openId:{} assist card by info:{}", request.getOpenId(), request);
        return null;
    }*/

    @ApiOperation(value = "获取服务方式列表", notes = "openId：用户openId(必传)")
    @RequestMapping("/way")
    public CommonResult getWayList(@RequestBody CommonRequest request) {
        log.info("start /card/way openId:{} get service list", request);
        List<ServiceWay> serviceWayList = cardService.getServiceWayList();
        if (null != serviceWayList) {
            log.info("get service way list:{} success", serviceWayList);
            return CommonResult.success(serviceWayList);
        }
        return CommonResult.failed("获取服务方式列表失败!");
    }

    @ApiOperation(value = "获取手工单列表", notes = "openId：用户openId(必传)")
    @RequestMapping("/hand/list")
    public CommonResult getHandList(@RequestBody CommonRequest request) {
        log.info("start /card/hand/list openId:{} get hand card list", request.getOpenId());
        HandCardListResponse handCardList = cardService.getHandCardList(request);
        if (null != handCardList) {
            log.info("get openId:{} hand card list:{} success", request.getOpenId(), handCardList);
            return CommonResult.success(handCardList);
        }
        return CommonResult.failed("获取手工单信息失败!");
    }

    @ApiOperation(value = "创建手工单", notes = "openId：用户openId(必传)")
    @RequestMapping("/hand/create")
    public CommonResult createHand(@RequestBody CreateHandCardRequest request) {
        log.info("start /card/hand/create open:{} create a hand card by info:{}", request.getOpenId(), request);
        int oneHandCard = cardService.createOneHandCard(request);
        if (oneHandCard == 1) {
            log.info("openId:{} create a hand card success", request.getOpenId());
            return CommonResult.success(oneHandCard);
        } else if (oneHandCard == 2) {
            return CommonResult.failed("已存在" + request.getFillTime() + "当天工单,请更改日期!");
        }
        return CommonResult.failed("创建手工单失败!");
    }

    @ApiOperation(value = "发送邮件", notes = "openId：用户openId(必传)")
    @RequestMapping("/send")
    public void sendMessage(@RequestBody CommonRequest request) {
        log.info("start /card/send send message to openId:{}", request.getOpenId());
        Map<String, TemplateData> map2 = new HashMap<>();
        List<String> content = cardService.getContent(request);
        content.forEach(System.out::println);

        map2.put("thing1", new TemplateData(content.get(0)));
        map2.put("time2", new TemplateData(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        GetOpenIdUtil.subscribeMessage(request.getOpenId(), content.get(1), map2, "Veu_p4T8OH54zqjXWllvNrveWEnpIbHlz2HI9MePPfM");
    }
}

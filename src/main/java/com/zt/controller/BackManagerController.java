package com.zt.controller;

import com.zt.config.CommonResult;
import com.zt.entity.common.LogInfo;
import com.zt.entity.common.ProblemInfo;
import com.zt.entity.common.ProblemService;
import com.zt.entity.common.UserRole;
import com.zt.entity.request.*;
import com.zt.entity.response.*;
import com.zt.service.BackCardService;
import com.zt.service.CardService;
import com.zt.service.UserService;
import com.zt.util.CreateReportUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/api1.0/manage", method = RequestMethod.POST)
@Api(value = "后台信息管理")
public class BackManagerController {

    private static Logger log = LoggerFactory.getLogger(CardController.class);

    private static CreateReportUtil reportUtil = new CreateReportUtil();

    @Autowired
    private BackCardService backCardService;

    @Autowired
    private CardService cardService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "获取全部工单", notes = "pageNo：当前页数(不传默认为1), pageSize：每页显示数量(不传默认为10)，" +
            "applyName：按姓名模糊查询模糊查询，status: 按工单状态筛选(未处理、处理中、已处理、未评价、已评价)，createTime：" +
            "筛选开始时间，endTime：筛选结束时间")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "当前页数(不传默认为1)", dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量(不传默认为10)", dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "applyName", value = "按工单申请人模糊查询", dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/card/list")
    public CommonResult AllCard(@RequestBody SeeCardRequest request) {
        log.info("start /manage/card/list get all card by:{}", request);
        CardListResponse allCard = cardService.getAllCard(request);
        if (null != allCard) {
            log.info("get all card:{} success", allCard);
            return CommonResult.success(allCard);
        }
        return CommonResult.failed("获取工单列表失败！");
    }

    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "cardId", value = "工单id", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "deal", value = "修改后的工程师openId", required = true, dataType = "string", paramType = "body")
    })*/
    @ApiOperation(value = "修改工单工程师", notes = "cardId：工单id(必传), deal：修改后的工程师openId(必传)")
    @RequestMapping("/update/card")
    public CommonResult<Object> updateCard(@RequestBody ChangeCardRequest request) {
        log.info("start /manage/update/card update card eng by:{}", request);
        int count = cardService.updateCardEng(request);
        if (count == 1) {
            log.info("update card by info:{} success", request);
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改工程师失败！");
    }

    @ApiOperation(value = "新增故障类型", notes = "typeId：一级类型id(不传则为新增一级类型), typeName：新增类型名称(必传), serviceId：故障服务类型id(必传)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "typeId", value = "一级类型id(不传则为新增一级类型)", dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "typeName", value = "新增类型名称", required = true, dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "serviceId", value = "故障服务类型id", required = true, dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/create/problem")
    public CommonResult createType(@RequestBody CreateProblemRequest request) {
        log.info("start /manage/create/type create problem type by:{}", request);
        int problem = cardService.createProblem(request);
        if (problem == 1) {
            log.info("create type by:{} success", request);
            return CommonResult.success("新增成功!");
        } else if (problem == 2) {
            return CommonResult.success("类型已存在，请重新输入！");
        }
        return CommonResult.failed("新增类型失败!");
    }

    @ApiOperation(value = "删除故障类型", notes = "typeId：需要删除的故障类型id(必传)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "typeId", value = "需要删除的故障类型id", required = true, dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/delete/problem")
    public CommonResult deleteType(@RequestBody CommonRequest request) {
        log.info("start /manage/delete/problem delete problem type by:{}", request);
        int problem = cardService.deleteProblem(request);
        if (problem == 1) {
            log.info("delete a problem by:{} success", request);

            return CommonResult.success("删除成功!");
        }
        return CommonResult.success("删除类型失败!");
    }

    @ApiOperation(value = "获取用户角色列表", notes = "pageNo：当前页数(不传默认为1)，pageSize：每页显示数量(不传默认为10)，" +
            "roleName：按角色名称模糊筛选(普通用户、工程师、话务员、管理员)，userName：按用户名称模糊筛选")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "pageNo", value = "当前页数(不传默认为1)", dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量(不传默认为10)", dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "roleName", value = "可选 按角色名称模糊筛选(普通用户、工程师、话务员、管理员)", dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "userName", value = "可选 按用户名称模糊筛选", dataType = "string", paramType = "body")
    })*/
    @RequestMapping("/user/list")
    public CommonResult getUserRoleList(@RequestBody UserListRequest request) {
        log.info("start /manage/user/list get user list by:{}", request);
        UserAndRoleResponse userAndRoleInfo = userService.getUserAndRoleInfo(request);
        if (null != userAndRoleInfo) {
            log.info("get user list info:{} success", userAndRoleInfo);
            return CommonResult.success(userAndRoleInfo);
        }
        return CommonResult.failed("获取用户列表失败！");
    }

    @ApiOperation(value = "修改用户角色", notes = "openId：用户openId(必传)，oldRoleId：用户之前的角色id，roleId：修改后的用户角色id")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "oldRoleId", value = "用户之前的角色id", required = true, dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "roleId", value = "修改后的用户角色id", required = true, dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/update/user")
    public CommonResult updateUserRole(@RequestBody CommonRequest request) {
        log.info("start /manage/update/user update user by:{}", request);
        int updateUser = userService.updateUserRole(request);
        if (updateUser == 1) {
            log.info("update user by:{} success", request);
            return CommonResult.success("修改成功!");
        }
        return CommonResult.failed("修改角色失败！");
    }

    @ApiOperation(value = "获取当日总工单数，待处理数，已处理数", notes = "countId：按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "countId", value = "按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)", dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/card/count")
    public CommonResult getCardCount(@RequestBody CommonRequest request) {
        log.info("start /manage/card/count get card count by:{}", request);
        UserCardResponse cardCountInfo = cardService.getCardCountInfo(request);
        if (null != cardCountInfo) {
            log.info("get card count info:{} success", cardCountInfo);
            return CommonResult.success(cardCountInfo);
        }
        return CommonResult.failed("获取工单总数失败!");
    }

    @ApiOperation(value = "TOP5工程师工作量（手工单，系统单）", notes = "countId：按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年), " +
            "serviceId：按服务组筛选(1、系统实施) 2、信息服务 3、系统开发 4、系统运维")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "countId", value = "按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)", dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/user/count")
    public CommonResult getUserCount(@RequestBody CommonRequest request) {
        log.info("start /manage/user/count get user card count by:{}", request);
        UserCardCountResponse userCardCount = cardService.getUserCardCount(request);
        if (null != userCardCount) {
            log.info("get user card count info:{} success", userCardCount);
            return CommonResult.success(userCardCount);
        }
        return CommonResult.failed("获取用户工单量失败！");
    }

    @ApiOperation(value = "月度满意度及任务量统计表", notes = "countId：按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)，" +
            "quarter：按季度筛选(1、2、3、4、5：全年)，year：按年份筛选(例如：2020)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "timeId", value = "按时间筛选(1、本季度 2、今年)", dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "quarter", value = "按季度筛选(1、2、3、4、5)", dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "year", value = "按年份筛选(例如：2020)", dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/user/comment")
    public CommonResult getUserComment(@RequestBody CommonRequest request) {
        log.info("start /manage/user/comment get user comment card count by:{}", request);
        UserCommentCountResponse userCommentCount = cardService.getUserCommentCount(request);
        if (userCommentCount != null) {
            log.info("get user comment card count info:{} success", userCommentCount);
            return CommonResult.success(userCommentCount);
        }
        return CommonResult.failed("获取用户评价工单量失败！");
    }

    @ApiOperation(value = "用户满意度，各部门是否评价占比", notes = "countId：按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "countId", value = "按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)", dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/dept/comment")
    public CommonResult getDeptComment(@RequestBody CommonRequest request) {
        log.info("start /manage/dept/comment get dept comment card count by:{}", request);
        UserCardCountResponse deptCommentCount = cardService.getDeptCommentCount(request);
        if (deptCommentCount != null) {
            log.info("get dept comment card count info:{} success", deptCommentCount);
            return CommonResult.success(deptCommentCount);
        }
        return CommonResult.failed("获取部门评价工单量失败！");
    }

    @ApiOperation(value = "故障类型占比(饼状图)", notes = "countId：按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "countId", value = "按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)", dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/problem/count")
    public CommonResult getProblemCount(@RequestBody CommonRequest request) {
        log.info("start /manage/problem/count get problem card count by:{}", request);
        ProblemCardCountResponse problemCountInfo = cardService.getProblemCountInfo(request);
        if (null != problemCountInfo) {
            log.info("get problem card count info:{} success", problemCountInfo);
            return CommonResult.success(problemCountInfo);
        }
        return CommonResult.failed("获取故障类型占比失败！");
    }

    @ApiOperation(value = "统计各部门中工单数量最多的故障类型", notes = "countId：按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "countId", value = "按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)", dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/dept/count")
    public CommonResult getDeptProblemCount(@RequestBody CommonRequest request) {
        log.info("start /manage/dept/count get dept card count by:{}", request);
        DeptProblemCountResponse deptCardCount = cardService.getDeptCardCount(request);
        if (deptCardCount != null) {
            log.info("get dept problem card count info:{}", deptCardCount);
            return CommonResult.success(deptCardCount);
        }
        return CommonResult.failed("获取各部门工单里最多的故障类型失败！");
    }

    @ApiOperation(value = "获取问题服务类型列表")
    @RequestMapping("/problem/service")
    public CommonResult getProblemService() {
        log.info("start /manage/problem/service get problem service list");
        List<ProblemService> problemService = cardService.getProblemService();
        if (null != problemService) {
            log.info("get problem service list info:{} success", problemService);
            return CommonResult.success(problemService);
        }
        return CommonResult.failed("获取问题服务类型列表失败！");
    }

    @ApiOperation(value = "获取用户角色列表")
    @RequestMapping("/user/role")
    public CommonResult getUserRole() {
        log.info("start /manage/user/role get user role list");
        List<UserRole> userRole = userService.getUserRole();
        if (null != userRole) {
            log.info("get user role list info:{} success", userRole);
            return CommonResult.success(userRole);
        }
        return CommonResult.failed("获取用户角色列表失败！");
    }

    @ApiOperation(value = "生成月度满意度及任务量统计表实时更新", notes = "countId：按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "countId", value = "按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)", dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/create/comment/excel")
    public CommonResult exportCommentExcel(@RequestBody CommonRequest request) {
        log.info("start /manage/create/comment/excel get create comment excel by:{}", request);
        try {
            UserCommentCountResponse userCommentCount = cardService.getUserCommentCount(request);
            reportUtil.createReport(userCommentCount);
            log.info("get create comment excel success");
            return CommonResult.success("生成excel成功！");
        } catch (Exception ex) {
            log.info("get create comment excel failed:{}", ex);
            return CommonResult.failed("生成excel失败！");
        }
    }

    @ApiOperation(value = "修改故障类排序", notes = "typeId：故障类型id(必传)，typeName：故障类型名称，typeNo：故障类型排序位置(必传)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "typeId", value = "故障类型id", required = true, dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "typeName", value = "可传 故障类型名称", dataType = "string", paramType = "body"),
            @ApiImplicitParam(name = "typeNo", value = "故障类型排序位置", required = true, dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/update/problem")
    public CommonResult updateProblem(@RequestBody ProblemInfo problemInfo) {
        log.info("start /manage/update/problem update problem type no by:{}", problemInfo);
        int problem = cardService.updateProblemTypeNo(problemInfo);
        if (problem == 1) {
            log.info("update problem type no success");
            return CommonResult.success("修改成功!");
        } else if (problem == 2) {
            log.info("update problem is not exist");
            return CommonResult.success("故障类型不存在！");
        }
        return CommonResult.failed("修改故障类型排序失败！");
    }

    @ApiOperation(value = "获取服务方式工单占比", notes = "countId：按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)，" +
            "quarter：按季度筛选(1、2、3、4、5：全年)，year：按年份筛选(例如：2020)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "countId", value = "按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)", dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "quarter", value = "按季度筛选(1、2、3、4、5)", dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "year", value = "按年份筛选(例如：2020)", dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/service/count")
    public CommonResult getServiceCountInfo(@RequestBody CommonRequest request) {
        log.info("start /manage/service/count get service card count by:{}", request);
        List<ServiceCardCountResponse> serviceCount = cardService.getServiceCount(request);
        if (null != serviceCount && 0 != serviceCount.size()) {
            log.info("get service card count info:{} success", serviceCount);
            return CommonResult.success(serviceCount);
        }
        return CommonResult.failed("获取服务方式工单占比失败!");
    }

    @ApiOperation(value = "获取待处理工单列表", notes = "countId：按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)，" +
            "pageNo：当前页数(不传默认为1)，pageSize：每页显示数量(不传默认为10)")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "countId", value = "按时间筛选(1、当天 2、本周 3、本月 4、本季度 5、今年)", dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "pageNo", value = "当前页数(不传默认为1)", dataType = "int", paramType = "body"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示数量(不传默认为10)", dataType = "int", paramType = "body")
    })*/
    @RequestMapping("/unDeal/card")
    public CommonResult getUnDealCard(@RequestBody CommonRequest request) {
        log.info("start /manage/unDeal/card get unDeal card list by:{}", request);
        CardListResponse unDealCardList = cardService.getUnDealCardList(request);
        if (unDealCardList != null) {
            log.info("get unDeal card list info:{} success", unDealCardList);
            return CommonResult.success(unDealCardList);
        }
        return CommonResult.failed("获取待处理工单列表失败！");
   }

    @ApiOperation(value = "导入线下填写工单Excel", notes = "需要传入文件路径")
    @RequestMapping("/import/card")
    public CommonResult importCardList(@RequestBody CommonRequest request) {
        log.info("start /manage/import/card import card unOnLine by:{}", request);
        try {
            reportUtil.importHandCard("116", new ArrayList<>());
            log.info("import card success");
            return CommonResult.success("导入线下工单成功！");
        } catch (Exception ex) {
            log.info("import card failed:{}", ex);
            return CommonResult.failed("导入线下工单失败！");
        }
    }

    @ApiOperation(value = "导出工单列表", notes = "需要传入文件路径")
    @RequestMapping("/export/card")
    public CommonResult exportCard(@RequestBody CommonRequest request) {
        log.info("start /manage/export/card export card list excel by:{}", request);
        try {
            SeeCardRequest seeCardRequest = new SeeCardRequest();
            seeCardRequest.setPageSize(10000);
            CardListResponse allCard = cardService.getAllCard(seeCardRequest);
            reportUtil.exportCard(allCard);
            log.info("export card list excel success");
            return CommonResult.success("导出工单列表excel成功！");
        } catch (Exception ex) {
            log.info("export card list excel failed:{}", ex);
            return CommonResult.failed("导出工单列表失败！");
        }
    }

    @ApiOperation(value = "按季度统计工程师满意度", notes = "pageNo：当前页数(不传默认为1)，pageSize：每页显示数量(不传默认为10)，" +
            "quarter: 第几季度(1、2、3、4)")
    @RequestMapping("/eng/comment")
    public CommonResult getEngComment(@RequestBody CommonRequest request) {
        log.info("start /eng/comment get eng comment by quarter:{}", request);
        EngCommentResponse engComment = cardService.getEngComment(request);
        if (null != engComment) {
            log.info("get eng comment info:{} success", engComment);
            return CommonResult.success(engComment);
        }
        return CommonResult.failed("获取工程师满意度列表失败！");
    }

    @ApiOperation(value = "批量发送邮件通知用户评价", notes = "cardList：工单列表中选中的工单的cardId集合")
    @RequestMapping("/send/email")
    public CommonResult sendEmail(@RequestBody SendCardRequest request) {
        log.info("start /send/email send email to notice user:{} to give a comment", request);
        int count = cardService.sendEmailForComment(request);
        if (count == 1) {
            log.info("start /send/email send email success");
            return CommonResult.success("邮件发送成功！");
        } else if (count == 2) {
            log.info("user have not fill email");
            return CommonResult.success("用户邮箱未填写！");
        }
        return CommonResult.failed("邮件发送失败！");
    }

    @ApiOperation(value = "获取日志信息列表", notes = "pageNo：当前页数(不传默认为1)，pageSize：每页显示数量(不传默认为10)，"
            + "createTime：筛选开始时间，endTime：筛选结束时间，operatorName：按操作人模糊筛选，operatorType：按操作类型筛选，"
            + "operatorResult：按操作结果筛选")
    @RequestMapping("/log/list")
    public CommonResult getLogList(@RequestBody LogInfoRequest request) {
        log.info("start /manage/log/list get log list by:{}", request);
        LogInfoResponse logInfos = backCardService.getLogInfos(request);
        if (null != logInfos) {
            log.info("get log list info:{} success", logInfos);
            return CommonResult.success(logInfos);
        }
        return CommonResult.failed("获取日志信息列表失败!");
    }

    @ApiOperation(value = "后台登录", notes = "")
    @RequestMapping("/user/login")
    public CommonResult userLogin() {

        return null;
    }
}

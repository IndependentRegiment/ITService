package com.zt.controller;

import com.zt.config.CommonResult;
import com.zt.config.UserEnum;
import com.zt.entity.common.*;
import com.zt.entity.request.*;
import com.zt.entity.response.CardCountResponse;
import com.zt.entity.response.LoginResponse;
import com.zt.entity.response.UserCardResponse;
import com.zt.service.BackCardService;
import com.zt.service.UserService;
import com.zt.util.GetOpenIdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/*
 * 功能描述: <br>
 * 〈用户接口〉
 * @Author:
 * @Date:
 */
@Api(value = "用户信息管理")
@CrossOrigin
@RestController
@RequestMapping(value = "/api1.0/user", method = RequestMethod.POST)
public class UserController {

    private static Logger log = LoggerFactory.getLogger(UserController.class);
    // 统计在线人数
    //private static Set<String> set = new HashSet<>();

    @Autowired
    private UserService userService;

    @Autowired
    private BackCardService backCardService;

    /*获取openid*/
    @ApiOperation(value = "获取用户openId", notes = "根据前端code获取用户openId")
    /*@ApiImplicitParam(name = "card", value = "用户card", required = true, dataType = "string", paramType = "path")*/
    @RequestMapping("/login")
    public CommonResult getOpenId(@RequestBody CodeRequest request, HttpServletRequest servletRequest) {
        log.info("start /user/login get openId, sessionKey, unionId by code:{}", request.getCode());
        if (request.getCode() == null || request.getCode().length() == 0) {
            return CommonResult.failed("code不能为空！");
        }
        Map<String, Object> stringObjectMap = GetOpenIdUtil.oauth2GetOpenid(request.getCode());
        String openId = stringObjectMap.get("openId").toString();
        UserInfo user = userService.getUserInfoById(openId);
        if (null == user) {
            ChangeUserRequest userInfo = new ChangeUserRequest();
            userInfo.setOpenId(openId);
            int user1 = userService.createUser(userInfo);
            if (user1 == 1) {
                RoleInfo roleInfo = userService.getRoleInfo(openId);
                if (roleInfo == null) {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(openId);
                    userRole.setRoleId(5);
                    userService.createUserRole(userRole);
                }
            }
        } else {
            RoleInfo roleInfo = userService.getRoleInfo(openId);
            if (roleInfo == null) {
                UserRole userRole = new UserRole();
                userRole.setUserId(openId);
                userRole.setRoleId(5);
                userService.createUserRole(userRole);
            }
        }

        String sessionKey = stringObjectMap.get("sessionKey").toString();
        String unionId = stringObjectMap.get("unionId").toString();
        LoginResponse loginResponse = new LoginResponse(openId, sessionKey, unionId);
        String message = stringObjectMap.get("errMsg").toString();
        log.info("get openId:{}, sessionKey:{}, unionId:{}, success", openId, sessionKey, unionId);
        // add log
        LogInfo logInfo = new LogInfo(openId, "其它", "登录IT服务中心小程序！", "操作成功");
        backCardService.addLog(logInfo);

        return CommonResult.success(loginResponse, message);
    }

    @ApiOperation(value = "获取用户信息及工单统计", notes = "根据用户openId获取用户信息")
    /*@ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "用户openId", required = true, dataType = "string", paramType = "path")
    })*/
    @RequestMapping("/count")
    public CommonResult getCardCount(@RequestBody RoleCardRequest request) {
        log.info("start /user/count openId():{} get card count", request.getOpenId());
        CardCountResponse roleCardCount = userService.getRoleCardCount(request);
        if (null != roleCardCount) {
            if (roleCardCount.getRoleName().equals(UserEnum.NEW.getName())) {
                log.info("openId:{} info not exist", request.getOpenId());
                return CommonResult.success(roleCardCount.getRoleName());
            }
            log.info("openId:{} get card count:{} success", request.getOpenId(), roleCardCount);
            return CommonResult.success(roleCardCount);
        }
        return CommonResult.failed("获取工单目录数失败！");
    }

    /*查看个人信息及工单统计*/
    @ApiOperation(value = "获取用户信息", notes = "openId：用户openId(必传)")
    @RequestMapping("/info")
    public CommonResult getUserCardInfo(@RequestBody CommonRequest request) {
        log.info("start /user/info get user and card info");
        if (request.getOpenId() == null || request.getOpenId().length() == 0) {
            return  CommonResult.failed("openId不能为空！");
        }
        UserCardResponse userCard = userService.getUserCard(request.getOpenId());
        return CommonResult.success(userCard);
    }

    @ApiOperation(value = "创建用户角色", notes = "openId：用户openId(必传)")
    @RequestMapping("/role")
    public CommonResult createRole(@RequestBody UserRole userRole) {
        int userRole1 = userService.createUserRole(userRole);
        if (userRole1 == 1) {
            return CommonResult.success("创建角色成功");
        }
        return CommonResult.failed("创建角色失败");
    }

    @ApiOperation(value = "创建用户", notes = "openId：用户openId(必传)")
    @RequestMapping("/create")
    public CommonResult createUser(@RequestBody ChangeUserRequest request) {
        log.info("start /user/create create user by openId:{}", request.getOpenId());
        if (request.getOpenId() == null || request.getOpenId().length() == 0) {
            return CommonResult.failed("openId不能为空！");
        }
        int user = userService.createUser(request);
        if (user == 1) {
            log.info("create user by openId:{} success", request.getOpenId());
            return CommonResult.success("创建成功");
        }
        return CommonResult.failed("创建失败");
    }

    @ApiOperation(value = "获取工作状态列表", notes = "根据用户openId获取")
    @RequestMapping("/status/list")
    public CommonResult statusList(@RequestBody CommonRequest request) {
        log.info("start /user/status/list get status list by openId:{}", request.getOpenId());
        List<JobStatus> jobStatus = userService.getJobStatus();
        if (null != jobStatus && 0 != jobStatus.size()) {
            log.info("openId:{} get status list:{} success", request.getOpenId(), jobStatus);
            return CommonResult.success(jobStatus);
        }
        return CommonResult.failed("获取状态列表失败！");
    }

    @ApiOperation(value = "修改工程师工作状态", notes = "根据用户openId及工作状态id")
    @RequestMapping("/update/status")
    public CommonResult updateStatus(@RequestBody ChangeStatusRequest request) {
        log.info("start /user/update/status openId:{} update status by info:{}", request.getOpenId(), request);
        int i = userService.updateUserStatus(request);
        if (i == 1) {
            log.info("openId:{} update status by info:{} success", request.getOpenId(), request);
            return CommonResult.success("修改成功");
        }
        return CommonResult.failed("修改状态失败");
    }

    @ApiOperation(value = "登记用户信息", notes = "openId：用户openId(必传), userName：用户姓名(必传), " +
            "deptId：部门id(必传)，phone：用户电话(必传)，email：用户邮箱(必传)， userId：用户工号(可传)")
    @RequestMapping("/register")
    public CommonResult updateUser(@RequestBody ChangeUserRequest userInfo) {
        log.info("start /user/register openId:{} update user by info:{}", userInfo.getOpenId(), userInfo);
        CardCountResponse cardCountResponse = userService.updateUserInfoById(userInfo);
        if (cardCountResponse != null) {
            if (cardCountResponse.getId() == -1) {
                return CommonResult.failed("请输入正确的手机号或电话!");
            } else if (cardCountResponse.getId() == -2) {
                return CommonResult.failed("请输入正确的邮箱!");
            }
            log.info("openId:{} update user by info:{} success", userInfo.getOpenId(), userInfo);
            return CommonResult.success(cardCountResponse);
        }
        return CommonResult.failed("登记信息失败");
    }

    @ApiOperation(value = "获取用户信息详情", notes = "openId：用户openId(必传)")
    @RequestMapping("/detail")
    public CommonResult getUserDetail(@RequestBody ChangeUserRequest request) {
        log.info("start /user/detail get openId:{} info", request.getOpenId());
        UserInfo userInfoById = userService.getUserInfoById(request.getOpenId());
        if (userInfoById != null) {
            log.info("get openId:{} info:{} success", request.getOpenId(), userInfoById);
            return CommonResult.success(userInfoById);
        }
        return CommonResult.failed("获取信息失败");
    }

    @ApiOperation(value = "获取工程师列表", notes = "openId：用户openId(必传)")
    @RequestMapping("/eng/list")
    public CommonResult getEngById(@RequestBody RoleCardRequest request) {
        log.info("start /user/eng/list get eng list by:{}", request);
        List<UserInfo> userByType = userService.getUserByType(request);
        if (null != userByType) {
            log.info("get eng list info:{} success", userByType);
            return CommonResult.success(userByType);
        }
        return CommonResult.failed("获取工程师列表失败");
    }

    /*@ApiOperation(value = "获取工程师工作状态", notes = "根据用户openId获取")
    @RequestMapping("/status/detail")
    public CommonResult getStatusDetail(@RequestBody CommonRequest request) {
        log.info("start user:{} /status/detail get eng job status", request.getOpenId());
        UserStatus engStatusInfo = userService.getEngStatusInfo(request);
        if (null != engStatusInfo) {
            log.info("get user job status success:{}", engStatusInfo);
            return CommonResult.success(engStatusInfo);
        }
        return CommonResult.failed("获取工作状态失败");
    }*/

    @ApiOperation(value = "获取用户手机号", notes = "openId：用户openId(必传)")
    @RequestMapping("/phone")
    public CommonResult getPhone(@RequestBody CommonRequest request) {
        log.info("start /user/phone get user phone by:{}", request);
        UserPhone userPhone = userService.getUserPhone(request);
        if (userPhone != null) {
            log.info("get user phone info:{} success", userPhone);
            return CommonResult.success(userPhone);
        }
        return CommonResult.failed("获取用户手机号失败！");
    }

    @RequestMapping("/use/count")
    public CommonResult number(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        log.info("start /user/use/count get using count~~~~~~~~~~~~~~");
        try{  //把sessionId记录在浏览器
            Cookie c = new Cookie("JSESSIONID", URLEncoder.encode(httpServletRequest.getSession().getId(), "utf-8"));
            c.setPath("/");
            //先设置cookie有效期为2天，不用担心，session不会保存2天
            c.setMaxAge( 48*60 * 60);
            httpServletResponse.addCookie(c);
        }catch (Exception e){
            e.printStackTrace();
        }

        HttpSession session = httpServletRequest.getSession();
        Object count=session.getServletContext().getAttribute("count");
        return CommonResult.success(count);
    }

}
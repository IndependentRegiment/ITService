package com.zt.controller;

import com.zt.config.CommonResult;
import com.zt.entity.request.CommonRequest;
import com.zt.entity.response.DeptInfoResponse;
import com.zt.service.DeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "api1.0/dept", method = RequestMethod.POST)
@Api(value = "部门信息管理")
public class DeptController {

    private Logger log = LoggerFactory.getLogger(DeptController.class);

    @Autowired
    private DeptService deptService;

    @ApiOperation(value = "获取部门列表", notes = "openId：用户openId(必传)")
    @RequestMapping("/list")
    public CommonResult getAllDeptList(@RequestBody CommonRequest request) {
        log.info("start /dept/list openId:{} get dept list", request.getOpenId());
        List<DeptInfoResponse> deptInfo = deptService.getDeptInfo();
        if (null != deptInfo && 0 != deptInfo.size()) {
            log.info("openId:{} get dept list:{} success", request.getOpenId(), deptInfo);
            return CommonResult.success(deptInfo);
        }
        return CommonResult.failed("获取部门列表失败!");
    }

    @ApiOperation(value = "获取公司列表", notes = "openId：用户openId(必传)")
    @RequestMapping("/company")
    public CommonResult getCompanyList(@RequestBody CommonRequest request) {
        log.info("start /dept/company openId:{} get company list", request.getOpenId());
        List<DeptInfoResponse> companyList = deptService.getCompanyOrDept("公司");
        if (null != companyList && companyList.size() != 0) {
            log.info("openId:{} get company list:{} success", request.getOpenId(), companyList);
            return CommonResult.success(companyList);
        }
        return CommonResult.failed("获取公司列表失败!");
    }

    @ApiOperation(value = "获取部门信息", notes = "openId：用户openId(必传)")
    @RequestMapping("/info")
    public CommonResult getDeptList(@RequestBody CommonRequest request) {
        log.info("start /dept/info openId:{} get some dept list", request.getOpenId());
        List<DeptInfoResponse> deptList = deptService.getCompanyOrDept("部门");
        if (null != deptList && deptList.size() != 0) {
            log.info("openId:{} get company list:{} success", request.getOpenId(), deptList);
            return CommonResult.success(deptList);
        }
        return CommonResult.failed("获取部门列表失败!");
    }
}

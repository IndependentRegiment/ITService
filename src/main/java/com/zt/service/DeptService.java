package com.zt.service;

import com.zt.entity.response.DeptInfoResponse;

import java.util.List;

public interface DeptService {

    // 获取部门信息
    List<DeptInfoResponse> getDeptInfo();

    // 获取公司或部门列表
    List<DeptInfoResponse> getCompanyOrDept(String type);
}

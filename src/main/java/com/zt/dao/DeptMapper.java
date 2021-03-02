package com.zt.dao;

import com.zt.entity.common.DeptInfo;

import java.util.List;

public interface DeptMapper {

    // 获取部门信息
    List<DeptInfo> getAllDept(int deptId);

    // 获取部门信息 by name
    List<DeptInfo> getDeptByName(String typeName);
}

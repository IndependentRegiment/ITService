package com.zt.service.impl;

import com.zt.dao.DeptMapper;
import com.zt.entity.common.DeptInfo;
import com.zt.entity.response.DeptInfoResponse;
import com.zt.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    // 获得所有的部门的信息
    @Override
    public List<DeptInfoResponse> getDeptInfo() {
        List<DeptInfo> deptInfos = deptMapper.getAllDept(0);
        List<DeptInfoResponse> responseList = new ArrayList<>();
        for (DeptInfo problemInfo : deptInfos) {
            DeptInfoResponse response = new DeptInfoResponse();
            response.setDeptId(problemInfo.getDeptId());
            response.setDeptName(problemInfo.getDeptName());
            response.setParentId(problemInfo.getParentId());
            if (null == response.getDeptList() || 0 == response.getDeptList().size()) {
                List<DeptInfoResponse> responseList2 = new ArrayList<>();
                response.setDeptList(responseList2);
            }
            responseList.add(response);
        }
        // 循环添加子部门
        for (DeptInfoResponse dept : responseList) {
            for (DeptInfoResponse dept1 : responseList) {
                if (dept1.getParentId() == dept.getDeptId()) {
                    dept.getDeptList().add(dept1);
                }
            }
        }

        List<DeptInfoResponse> collect = responseList.stream().filter(data -> data.getParentId() == 99
                || data.getParentId() == 88).collect(Collectors.toList());
        collect.stream().forEach(dept -> {
            if (null == dept.getDeptList() || 0 == dept.getDeptList().size()) {
                DeptInfoResponse deptInfoResponse = new DeptInfoResponse();
                deptInfoResponse.setDeptId(dept.getDeptId());
                deptInfoResponse.setDeptName(dept.getDeptName());
                deptInfoResponse.setParentId(dept.getParentId());
                List<DeptInfoResponse> responses = new ArrayList<>();
                deptInfoResponse.setDeptList(responses);
                dept.getDeptList().add(deptInfoResponse);
            }
        });
        return collect;
    }

    @Override
    public List<DeptInfoResponse> getCompanyOrDept(String type) {
        List<DeptInfoResponse> deptInfo = getDeptInfo();
        if ("公司".equals(type)) {
            deptInfo.stream().filter(dept -> dept.getParentId() == 99)
                    .forEach(dept -> {
                        dept.setDeptList(new ArrayList<>());
                    });
            return deptInfo.stream().filter(dept -> dept.getParentId() == 99).collect(Collectors.toList());
        } else {
            return deptInfo.stream().filter(dept -> dept.getParentId() != 99).collect(Collectors.toList());
        }
    }
}

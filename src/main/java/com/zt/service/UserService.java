package com.zt.service;

import com.zt.entity.common.*;
import com.zt.entity.request.*;
import com.zt.entity.response.CardCountResponse;
import com.zt.entity.response.UserAndRoleResponse;
import com.zt.entity.response.UserCardResponse;

import java.util.List;

public interface UserService {

    // 查看个人信息及获取工单数量
    UserCardResponse getUserCard(String openId);

    // 获取工单数量
   CardCountResponse getRoleCardCount(RoleCardRequest request);

   // 获取用户信息
   UserInfo getUserInfoById(String openId);

    // 创建用户
    int createUser(ChangeUserRequest userInfo);

    // 添加用户角色表信息
    int createUserRole(UserRole userRole);

    // 获取工作状态列表
    List<JobStatus> getJobStatus();

    // 修改用户状态
    int updateUserStatus(ChangeStatusRequest request);

    // 根据openId获取用户角色
    RoleInfo getRoleInfo(String openId);

    // 维护用户信息
    CardCountResponse updateUserInfoById(ChangeUserRequest userInfo);

    // 获取该服务类型的工程师列表
    List<UserInfo> getUserByType(RoleCardRequest request);

    // 获取用户及其角色信息
    UserAndRoleResponse getUserAndRoleInfo(UserListRequest request);

    // 修改用户角色
    int updateUserRole(CommonRequest request);

    // 获取用户角色列表
    List<UserRole> getUserRole();

    // 获取用户电话号码
    UserPhone getUserPhone(CommonRequest request);
}

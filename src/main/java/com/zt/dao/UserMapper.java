package com.zt.dao;

import com.zt.entity.common.*;
import com.zt.entity.request.ChangeStatusRequest;
import com.zt.entity.request.CommonRequest;
import com.zt.entity.request.UserListRequest;

import java.util.List;

public interface UserMapper {

    // 根据openId获取用户信息
    UserInfo getUserInfo(String openId);

    // 根据userName获取用户信息
    UserInfo getUserInfoByName(String userName);

    // 根据openId获取用户角色
    List<RoleInfo> getRoleInfo(String openId);

    // 获取拥有某个角色的所有用户
    List<UserInfo> getUserInfoByRole(String roleName);

    // 创建用户
    void createUser(UserInfo userInfo);

    // 添加用户角色表信息
    void createUserRole(UserRole userRole);

    // 获取用户状态
    UserStatus getUserStatus(String openId);

    // 获取工作状态列表
    List<JobStatus> getStatusList(int id);

    // 修改用户状态
    void updateStatus(ChangeStatusRequest request);

    // 获取该服务类型的工程师列表
    List<UserInfo> getUserInfoByType(int typeId);

    // 维护用户信息
    void updateUserInfo(UserInfo userInfo);

    // 获取用户信息
    List<UserInfo> getUserByName(String userName);

    // 获取用户手机号
    List<UserPhone> getUserPhone(String openId);

    // 新增用户手机号
    void addUserPhone(UserPhone userPhone);

    // 判断用户手机号是否存在
    List<UserInfo> getUserPhoneByName(String userName);

    // 修改用户手机号
    void updatePhoneByName(UserPhone userPhone);

    // 获取用户及其角色信息
    List<UserInfo> getUserAndRole(UserListRequest request);

    // 修改用户角色
    void updateUserRole(CommonRequest request);

    // 获取用户角色列表
    List<UserRole> getUserRole();

    // 添加用户手机信息
    void createUserPhone(UserPhone userPhone);

    // 判断用户手机邮箱信息是否存在
    List<UserPhone> getPhoneInfoByName(String userName);
}

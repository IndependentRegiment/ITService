package com.zt.service.impl;

import com.zt.config.ProcessEnum;
import com.zt.config.RoleEnum;
import com.zt.config.StatusEnum;
import com.zt.config.UserEnum;
import com.zt.dao.CardMapper;
import com.zt.dao.DeptMapper;
import com.zt.dao.UserMapper;
import com.zt.entity.common.*;
import com.zt.entity.request.*;
import com.zt.entity.response.CardCountResponse;
import com.zt.entity.response.RoleCardResponse;
import com.zt.entity.response.UserAndRoleResponse;
import com.zt.entity.response.UserCardResponse;
import com.zt.service.UserService;
import com.zt.util.DateUtil;
import com.zt.util.RegUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public UserCardResponse getUserCard(String openId) {

        // get user info
        log.info("start get openId:{} info", openId);
        UserInfo userInfo = userMapper.getUserInfo(openId);
        String userName = "";
        if (userInfo != null) {
            log.info("get openId:{} info:{} success", openId, userInfo);
            userName = Optional.ofNullable(userInfo.getUserName()).orElse("");
            // register user
            if (null == userInfo.getUserName() || userInfo.getUserName().equals("")
                    || userInfo.getDepartment() == null || userInfo.getDepartment().equals("")) {
                log.info("openId:{} info not exist", openId);
                UserCardResponse userCardResponse = new UserCardResponse(UserEnum.NEW.getName());
                return userCardResponse;
            }
        } else {
            UserCardResponse userCardResponse = new UserCardResponse(UserEnum.NEW.getName());
            return userCardResponse;
        }

        // get role info
        log.info("start get openId:{} info", openId);
        List<RoleInfo> roleInfo = userMapper.getRoleInfo(openId);
        System.err.println("-------------" + roleInfo);
        String roleName = "";
        if (null != roleInfo && roleInfo.size() != 0) {
            for(RoleInfo role : roleInfo) {
                if (role.getRoleName().equals(RoleEnum.USER.getName())) {
                    roleName = role.getRoleName();
                    break;
                } else if(role.getRoleName().equals(RoleEnum.ENGINEER.getName())) {
                    roleName = role.getRoleName();
                    break;
                } else if (role.getRoleName().equals(RoleEnum.OPERATOR.getName())) {
                    roleName = role.getRoleName();
                    break;
                }
            }
        }
        log.info("get openId:{} role info:() success", openId, roleName);

        // get user card info
        log.info("start get openId:{} card info", openId);
        List<JobCard> userCardList1 = cardMapper.getUserCardList(openId);
        List<JobCard> userCardList = new ArrayList<>();
        if ("普通用户".equals(roleName)) {
            for (JobCard card : userCardList1) {
                if (openId.equals(card.getApplyId())) {
                    userCardList.add(card);
                }
            }
        } else if ("工程师".equals(roleName)) {
            for (JobCard card : userCardList1) {
                if (openId.equals(card.getDeal()) || openId.equals(card.getAssistId())) {
                    userCardList.add(card);
                }
            }
        } else if ("话务员".equals(roleName)) {
            for (JobCard card : userCardList1) {
                if (openId.equals(card.getConstruction())) {
                    userCardList.add(card);
                }
            }
        }

        log.info("all card:info:{}", userCardList);
        int totalCount = 0; // 工单总数
        int createCount = 0; // 代建或申请创建的工单数
        int updateCount = 0; // 修改的工单数
        int untreateCount = 0; // 未处理的工单数
        int treateCount = 0; // 处理的工单数
        int assistCount = 0; // 协办的工单数
        int finishCount = 0; // 已完成的工单数
        int evaluationCount = 0; // 评价的工单数
        if (null != userCardList && userCardList.size() != 0) {
            log.info("get openId:{} card info:{} success", openId, userCardList);
            totalCount = userCardList.size();
            for (JobCard card : userCardList) {
                if (roleName.equals(RoleEnum.OPERATOR.getName())) {
                    if (card.getProcessNode().intValue() < ProcessEnum.CARD_FINISH.getProcess()
                            && card.getConstruction() != null) {
                        createCount ++;
                    } else if (card.getProcessNode().intValue() == ProcessEnum.CARD_FINISH.getProcess()){
                        finishCount ++;
                    } else if(card.getProcessNode().intValue() == ProcessEnum.CARD_COMMENT.getProcess()
                            && (card.getComment() != null || card.getSatisfaction() != null)) {
                        evaluationCount ++;
                    }
                } else if (roleName.equals(RoleEnum.ENGINEER.getName())) {
                    if (card.getProcessNode().intValue() < ProcessEnum.CARD_DEAL.getProcess()) {
                        untreateCount ++;
                    } else if (card.getProcessNode().intValue() < ProcessEnum.CARD_FINISH.getProcess()
                            && card.getProcessNode().intValue() >= ProcessEnum.CARD_DEAL.getProcess()) {
                        if (card.getAssistId() != null) {
                            assistCount ++;
                        } else {
                            treateCount ++;
                        }
                    } else if (card.getProcessNode().intValue() == ProcessEnum.CARD_FINISH.getProcess()){
                        finishCount ++;
                    } else if(card.getProcessNode().intValue() == ProcessEnum.CARD_COMMENT.getProcess()
                            && (card.getComment() != null || card.getSatisfaction() != null)) {
                        evaluationCount ++;
                    }
                } else if (roleName.equals(RoleEnum.USER.getName())) {
                    if (card.getProcessNode() < ProcessEnum.CARD_FINISH.getProcess()) {
                        createCount ++;
                    } else if (card.getProcessNode() < ProcessEnum.CARD_COMMENT.getProcess()
                            && card.getProcessNode() >= ProcessEnum.CARD_FINISH.getProcess()) {
                        finishCount ++;
                    } else if(card.getProcessNode().intValue() == ProcessEnum.CARD_COMMENT.getProcess()
                            && (card.getComment() != null || card.getSatisfaction() != null)) {
                        evaluationCount ++;
                    }
                }
            }
        }

        UserCardResponse userCardResponse = new UserCardResponse(openId, userName, roleName,
                totalCount, createCount, finishCount, updateCount, evaluationCount,
                untreateCount, treateCount, assistCount);
        log.info("get openId:{} all info:{} success", openId, userCardResponse);
        return userCardResponse;
    }

    @Override
    public CardCountResponse getRoleCardCount(RoleCardRequest request) {
        UserCardResponse userCard = getUserCard(request.getOpenId());
        if (userCard != null) {
            if (userCard.getUserName() == UserEnum.NEW.getName()) {
                return new CardCountResponse(UserEnum.NEW.getName());
            }
        }
        List<RoleCardResponse> cardList = new ArrayList<>();
        if (userCard.getUserRole().equals(RoleEnum.OPERATOR.getName())) {
            RoleCardResponse create = new RoleCardResponse(StatusEnum.CREATE.getStatus(), userCard.getCreateCount());
            cardList.add(create);
        } else if (userCard.getUserRole().equals(RoleEnum.USER.getName())) {
            RoleCardResponse create = new RoleCardResponse(StatusEnum.CREATE.getStatus(), userCard.getCreateCount());
            cardList.add(create);
        } else if (userCard.getUserRole().equals(RoleEnum.ENGINEER.getName())) {
            RoleCardResponse unDeal = new RoleCardResponse(StatusEnum.UNDEAL.getStatus(), userCard.getUntreateCount());
            RoleCardResponse deal = new RoleCardResponse(StatusEnum.DEAL.getStatus(), userCard.getTreateCount());
            RoleCardResponse assist = new RoleCardResponse(StatusEnum.ASSIST.getStatus(), userCard.getAssistCount());
            cardList.add(unDeal);
            cardList.add(deal);
            cardList.add(assist);
        }
        RoleCardResponse finish = new RoleCardResponse(StatusEnum.FINISH.getStatus(), userCard.getFinishCount());
        RoleCardResponse comment = new RoleCardResponse(StatusEnum.COMMENT.getStatus(), userCard.getEvaluationCount());
        cardList.add(finish);
        cardList.add(comment);
        CardCountResponse cardCountResponse = new CardCountResponse();
        cardCountResponse.setRoleList(cardList);
        cardCountResponse.setRoleName(userCard.getUserRole());
        cardCountResponse.setUserName(userCard.getUserName());
        UserStatus userStatus = userMapper.getUserStatus(userCard.getOpenId());
        if (null != userStatus) {
            if (null != userStatus.getStatusId() || 0 != userStatus.getStatusId()) {
                cardCountResponse.setId(userStatus.getStatusId());
            }

            if (null != userStatus.getStatusName()) {
                cardCountResponse.setStatusName(userStatus.getStatusName());
            }
        }
        return cardCountResponse;
    }

    @Override
    public UserInfo getUserInfoById(String openId) {
        return userMapper.getUserInfo(openId);
    }

    @Override
    public int createUser(ChangeUserRequest userInfo) {
        try {
            UserInfo userInfo1 = new UserInfo();
            userInfo1.setOpenId(Optional.ofNullable(userInfo.getOpenId()).orElse(""));
            userInfo1.setUserName(Optional.ofNullable(userInfo.getUserName()).orElse(""));
            userInfo1.setRemark(Optional.ofNullable(userInfo.getRemark()).orElse("IT服务中心信息登记"));
            userInfo1.setUserId(Optional.ofNullable(userInfo.getUserId()).orElse(""));
            if (null != userInfo.getDeptId()) {
                List<DeptInfo> allDept = deptMapper.getAllDept(userInfo.getDeptId());
                if (allDept != null && 0 != allDept.size()) {
                    userInfo1.setDepartment(allDept.get(0).getDeptName());
                }
            }
            if (null != userInfo.getCompanyId()) {
                List<DeptInfo> allCompany = deptMapper.getAllDept(userInfo.getCompanyId());
                if (allCompany != null && allCompany.size() != 0) {
                    userInfo1.setCompany(allCompany.get(0).getDeptName());
                }
            }
            userInfo1.setAuthentication(DateUtil.getTimeStamp());
            userMapper.createUser(userInfo1);
            return 1;
        } catch (Exception ex) {
            log.info("openId:{} create user failed:{}", userInfo.getOpenId(), ex);
            return 0;
        }
    }

    @Override
    public int createUserRole(UserRole userRole) {
        try {
            log.info("start create role by info:{}", userRole);
            userMapper.createUserRole(userRole);
            return 1;
        } catch (Exception ex) {
            log.info("openId:{} create user role failed:{}", userRole.getUserId(), ex);
            return 0;
        }
    }

    @Override
    public List<JobStatus> getJobStatus() {
        return userMapper.getStatusList(0);
    }

    @Override
    public int updateUserStatus(ChangeStatusRequest request) {
        try{
            userMapper.updateStatus(request);
            return 1;
        } catch (Exception ex) {
            log.info("openId:{} update user status by info:{} failed", request.getOpenId(), ex);
            return 0;
        }
    }

    @Override
    public RoleInfo getRoleInfo(String openId) {
        List<RoleInfo> roleInfo = userMapper.getRoleInfo(openId);
        if (null != roleInfo && 0 != roleInfo.size()) {
            return roleInfo.get(0);
        }
        return null;
    }

    @Override
    public CardCountResponse updateUserInfoById(ChangeUserRequest userRequest) {
        try {
            UserInfo userInfo1 = userMapper.getUserInfo(userRequest.getOpenId());
            if (!RegUtil.IsMobilePhone(userRequest.getPhone()) && !RegUtil.IsPhone(userRequest.getPhone())) {
                return new CardCountResponse(-1);
            }
            if (!RegUtil.IsEmail(userRequest.getEmail())) {
                return new CardCountResponse(-2);
            }
            UserInfo userInfo = new UserInfo();
            userInfo.setOpenId(userRequest.getOpenId());
            userInfo.setUserName(userRequest.getUserName());
            // 获取用户部门
            List<DeptInfo> allDept1 = deptMapper.getAllDept(userRequest.getDeptId());
            if (null != allDept1 && allDept1.size() != 0) {
                userInfo.setDepartment(allDept1.get(0).getDeptName());
                // 获取用户公司
                userInfo.setCompany(getCompany(allDept1.get(0).getParentId(), allDept1.get(0).getDeptId()));
            }
            userInfo.setAuthentication(DateUtil.getTimeStamp());
            userInfo.setRemark("IT服务中心信息登记");
            userInfo.setUserId(Optional.ofNullable(userRequest.getUserId()).orElse(""));
            // 添加用户手机邮箱信息
            List<UserPhone> userPhone1 = userMapper.getUserPhone(userRequest.getOpenId());
            if (null == userPhone1) {
                UserPhone userPhone = new UserPhone();
                userPhone.setOpenId(userInfo.getOpenId());
                userPhone.setMobilePhone(userRequest.getPhone());
                userPhone.setEmail(userRequest.getEmail());
                userPhone.setUserName(userRequest.getUserName());
                userMapper.addUserPhone(userPhone);
            }

            if (userInfo1 != null) {
                // 修改用户
                userMapper.updateUserInfo(userInfo);
            } else {
                // 新增用户
                userMapper.createUser(userInfo);
                // 新增普通权限
                UserRole userRole = new UserRole();
                userRole.setUserId(userInfo.getOpenId());
                userRole.setRoleId(5);
                userMapper.createUserRole(userRole);
            }

            RoleCardRequest request = new RoleCardRequest();
            request.setOpenId(userInfo.getOpenId());
            return getRoleCardCount(request);
        } catch (Exception ex) {
            log.info("update openId:{} info failed", userRequest.getOpenId());
            return null;

        }
    }

    private String getCompany(Integer parentId, Integer deptId) {
        List<DeptInfo> allDept = deptMapper.getAllDept(parentId);
        List<DeptInfo> allDept1 = deptMapper.getAllDept(deptId);
        String deptName = allDept1.get(0).getDeptName();
        if (allDept != null && allDept.size() != 0) {
            deptName = getCompany(allDept.get(0).getParentId(), allDept.get(0).getDeptId());
        } else {
            return deptName;
        }
        return deptName;
    }

    @Override
    public List<UserInfo> getUserByType(RoleCardRequest request) {
        try {
            return userMapper.getUserInfoByType(0);
        } catch (Exception ex) {
            log.info("get user info by type:{} failed:{}", request, ex);
            return null;
        }
    }

    @Override
    public UserAndRoleResponse getUserAndRoleInfo(UserListRequest request) {
        UserAndRoleResponse response = new UserAndRoleResponse();
        List<UserInfo> userAndRole = userMapper.getUserAndRole(request);
        if (userAndRole != null && 0 != userAndRole.size()) {
            int startNo = (request.getPageNo() - 1) * request.getPageSize();
            List<UserInfo> collect = userAndRole.stream().skip(startNo)
                    .limit(request.getPageSize()).collect(Collectors.toList());
            // 获取用户状态
            for(UserInfo user: collect) {
                UserStatus userStatus = userMapper.getUserStatus(user.getOpenId());
                if (null != userStatus) {
                    user.setStatusName(userStatus.getStatusName());
                    user.setStatusId(userStatus.getStatusId());
                } else {  // default， 普通用户的情况
                    user.setStatusName("在线");
                    user.setStatusId(1);
                }
            }

            response.setUserList(collect);
            response.setTotalCount(userAndRole.size());
            return response;
        }

        response.setUserList(new ArrayList<>());
        return response;
    }

    @Override
    public int updateUserRole(CommonRequest request) {
        try {
            userMapper.updateUserRole(request);
            return 1;
        } catch (Exception ex) {
            log.info("update user role by:{} failed:{}", request, ex);
            return 0;
        }
    }

    @Override
    public List<UserRole> getUserRole() {
        try {
            return userMapper.getUserRole();
        } catch (Exception ex) {
            log.info("get user role list failed:{}", ex);
            return null;
        }
    }

    @Override
    public UserPhone getUserPhone(CommonRequest request) {
        UserPhone users = new UserPhone();
        if (null != request.getOpenId()) {
            List<UserPhone> userPhone = userMapper.getUserPhone(request.getOpenId());
            if (null != userPhone && 0 != userPhone.size()) {
                for (UserPhone user : userPhone) {
                    if (null != user.getMobilePhone()) {
                        return user;
                    }
                }
            }
        }
        return users;
    }
}

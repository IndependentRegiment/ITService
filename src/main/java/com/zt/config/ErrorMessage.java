package com.zt.config;

import org.springframework.context.annotation.Configuration;
/*
 * 功能描述: <br>
 * 〈统一返回message〉
 * @Param:
 * @Return:
 * @Author:
 * @Date:
 */
@Configuration
public class ErrorMessage {

    public static String ADD_SUCCESS = "新增成功";
    public static String ADD_FAILED = "新增失败";
    public static String DEL_SUCCESS = "删除成功";
    public static String DEL_FAILED = "删除失败";
    public static String UPDATE_SUCCESS = "修改成功";
    public static String UPDATE_FAILED = "修改失败";

    // user
    public static String COUNT_FAILED = "获取工单目录数失败！";
    public static String ROLE_SUCCESS = "创建角色成功";
    public static String ROLE_FAILED = "创建角色失败!";
    public static String USER_CREATE_SUCCESS = "创建成功";
    public static String USER_CREATE_FAILED = "创建失败!";
    public static String STATUS_LIST_FAILED = "获取状态列表失败！";
    public static String UPDATE_STATUS_FAILED = "修改状态失败!";
    public static String REGISTER_FAILED = "注册信息失败!";
    public static String USER_DETAIL_FAILED = "获取信息失败!";
    public static String ENG_LIST_FAILED = "获取工程师列表失败!";
    public static String PHONE_FAILED = "获取用户手机号失败！";
    public static String USER_NOT_EXIST = "用户不存在！";
    public static String USER_EXIST = "用户已存在！";

    // card
    public static String LIST_FAILED = "获取列表信息失败!";
    public static String CARD_CREATE_FAILED = "创建工单失败!";
    public static String PROBLEM_LIST_FAILED = "获取故障类型列表失败！";
    public static String CARD_DETAIL_FAILED = "获取故障单详情失败！";
    public static String CARD_UPDATE_FAILED = "修改故障单失败！";
    public static String ASSIST_LIST_FAILED = "获取协办人列表失败!";
    public static String CARD_COMMENT_FAILED = "用户评价失败！";
    public static String CARD_WAY_FAILED = "获取服务方式列表失败!";
    public static String HAND_LIST_FAILED = "获取手工单信息失败!";
    public static String HAND_CARD_EXIST = "当天手工单已存在！";
    public static String HAND_CARD_CREATE = "创建手工单失败!";
    // dept
    public static String DEPT_LIST_FAILED = "获取部门列表失败!";
    public static String DEPT_COMPANY_FAILED = "获取公司列表失败!";
    public static String DEPT_INFO_FAILED = "获取部门列表失败!";

    // manage
    public static String MANAGE_CARD_LIST_FAILED = "获取工单列表失败！";
    public static String MANAGE_UPDATE_CARD_FAILED = "修改工程师失败！";
    public static String CREATE_PROBLEM_FAILED = "新增类型失败!";
    public static String CARD_PROBLEM_EXIST = "类型已存在，请重新输入！";
    public static String MANAGE_USER_LIST_FAILED = "获取用户列表失败！";
    public static String MANAGE_COUNT_FAILED = "获取工单总数失败!";
    public static String MANAGE_USER_COUNT_FAILED = "获取用户工单量失败！";
    public static String MANAGE_USER_COMMENT_FAILED = "获取用户评价工单量失败！";
    public static String MANAGE_DEPT_COMMENT_FAILED = "获取部门评价工单量失败！";
    public static String MANAGE_PROBLEM_COUNT_FAILED = "获取故障类型占比失败！";
    public static String MANAGE_DEPT_COUNT_FAILED = "获取各部门工单里最多的故障类型失败！";
    public static String MANAGE_PROBLEM_SERVICE_FAILED = "获取问题服务类型列表失败！";
    public static String MANAGE_USER_ROLE_FAILED = "获取用户角色列表失败！";
    public static String CREATE_COMMENT_EXCEL_SUCCESS = "生成excel成功！";
    public static String CREATE_COMMENT_EXCEL_FAILED = "生成excel失败！";
    public static String MANAGE_UPDATE_PROBLEM_FAILED = "修改故障类型排序失败！";
    public static String MANAGE_SERVICE_COUNT_FAILED = "获取服务方式工单占比失败!";
    public static String MANAGE_UNDEAL_CARD_FAILED = "获取待处理工单列表失败！";
    public static String IMPORT_CARD_SUCCESS = "导入线下工单成功！";
    public static String IMPORT_CARD_FAILED = "导入线下工单失败！";
    public static String EXPORT_CARD_SUCCESS = "导出工单列表excel成功！";
    public static String EXPORT_CARD_FAILED = "导出工单列表excel失败！";
    public static String MANAGE_ENG_COMMENT_FAILED = "获取工程师满意度列表失败！";
    public static String SEND_EMAIL_SUCCESS = "邮件发送成功！";
    public static String SEND_EMAIL_FAILED = "邮件发送失败！";
    public static String LOG_LIST_FAILED = "获取日志信息列表失败!";

}

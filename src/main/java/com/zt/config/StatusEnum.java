package com.zt.config;

import org.springframework.stereotype.Component;

/*
 * 功能描述: <br>
 * 〈工单状态〉
 * @Author:
 * @Date:
 */

public enum StatusEnum {
    CREATE("已创建"),
    CONSTRUCTION("已代建"),
    UNDEAL("待处理"),
    DEAL("处理中"),
    ASSIST("协办中"),
    COMMENT("已评价"),
    FINISH("已处理"),
    WAPPLY("1"),  // 已申请
    WUNDEAL("2"),  // 未处理
    WDEAL("3"),    // 处理中
    WFINISH("4");  // 已完成

    private String name;
    StatusEnum(String name) {
        this.name = name;
    }

    public String getStatus() {
        return name;
    }

    public static String getStatusName(int statusId) {
        String name = "";
        switch (statusId) {
            case 1:
                name = "已创建";
                break;
            case 2:
                name = "待处理";
                break;
            case 3:
                name = "处理中";
                break;
            case 4:
                name = "已处理";
                break;
            default:
                name = "待定";
                break;
        }
        return name;
    }

    public static int getRealStatus(String status) {
        switch (status) {
            case "已创建":
            case "已代建":
            case "待处理":
                return 1;
            case "处理中":
                return 3;
            case "已处理":
                return 4;
            default:
                return 0;
        }
    }
}

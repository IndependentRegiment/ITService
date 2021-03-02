package com.zt.config;

import org.springframework.stereotype.Component;

/*
 * 功能描述: <br>
 * 〈流程节点〉
 * @Author:
 * @Date:
 */

public enum ProcessEnum {
    CARD_APPLY(1),          // 用户登记
    CARD_DISTRIBUTION(2),   // 故障分配
    CARD_DEAL(3),           // 故障处理
    CARD_FINISH(4),         // 工单结算
    CARD_COMMENT(5);        // 服务评价


    private int process;
    ProcessEnum(int process) {
        this.process = process;
    }

    public int getProcess() {
        return process;
    }

    public static int getProcessNode(String nodeName) {
        int nodeId = 0;
        switch (nodeName) {
            case "用户登记":
                nodeId = 1;
                break;
            case "故障分配":
                nodeId = 2;
                break;
            case "故障处理":
                nodeId = 3;
                break;
            case "工单结算":
                nodeId = 4;
                break;
            case "服务评价":
                nodeId = 5;
                break;
            default:
                nodeId = 0;
                break;
        }
        return nodeId;
    }
}

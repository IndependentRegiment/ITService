package com.zt.entity.common;

import lombok.*;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/*
 * 功能描述: <br>
 * 〈流程节点〉
 * @Author:
 * @Date:
 */
@Repository
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessInfo implements Serializable {

    private int nodeId;
    private String nodeName;
}

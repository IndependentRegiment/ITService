package com.zt.entity.common;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Repository
@ApiModel
@ToString
public class SubscribeMessage {
    //接口调用凭证
    private String access_token;
    //接收者（用户）的 openid
    private String touser;
    //所需下发的订阅模板id
    private String template_id;
    //点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。
    private String page;
    // 版本 默认为 正式版：trial  体验版：formal
    private String miniprogram_state;
    //用户提交的form_id
    //private String form_id;
    //模板内容，格式形如 { "key1": { "value": any }, "key2": { "value": any } }
    private Map<String, TemplateData> data = new HashMap<>();
}

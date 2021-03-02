package com.zt.controller;

import com.zt.config.CommonResult;
import com.zt.entity.common.JobCardDetail;
import com.zt.entity.common.TemplateData;
import com.zt.entity.request.CommonRequest;
import com.zt.util.GetOpenIdUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.print.DocFlavor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class TestController {

    @RequestMapping("/test")
    public CommonResult testIP() {
        //System.err.println("start test: " + request);
        return CommonResult.success("又一位帅哥进来了,欢迎！欢迎！");
    }

    @RequestMapping("/send")
    public void sendMessage(@RequestBody CommonRequest request) {
        /*System.out.println("消息一--------------------------------------");
        Map<String, TemplateData> map1 = new HashMap<>();
        map1.put("time3", new TemplateData(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())));
        map1.put("character_string5", new TemplateData("161868"));
        map1.put("thing4", new TemplateData("提示"));
        GetOpenIdUtil.subscribeMessage(request.getOpenId(), "index", map1, "djUO_W_b_hm2yMT7-VHlt31PHE6pEfb-ENhQfge3xvI");*/

        /*System.out.println("消息二--------------------------------------");
        Map<String, TemplateData> map = new HashMap<>();
        map.put("time2", new TemplateData(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())));
        map.put("thing1", new TemplateData("您有新的工单，请及时处理！"));
        GetOpenIdUtil.subscribeMessage(request.getOpenId(), "index", map, "Veu_p4T8OH54zqjXWllvNrveWEnpIbHlz2HI9MePPfM");*/

        /*System.out.println("消息三--------------------------------------");
        Map<String, TemplateData> map2 = new HashMap<>();
        map2.put("date2", new TemplateData(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())));
        map2.put("thing1", new TemplateData("您有新的工单，请及时处理！"));
        GetOpenIdUtil.subscribeMessage(request.getOpenId(), "index", map2, "ovdH0bBoqKQq0YWFoq8Z7UtTsWpAvdcE-WxIie2WeRc");*/

        /*System.out.println("消息四--------------------------------------");
        Map<String, TemplateData> map2 = new HashMap<>();
        map2.put("time3", new TemplateData(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())));
        map2.put("thing4", new TemplateData("您有新的工单，请及时处理！"));
        GetOpenIdUtil.subscribeMessage(request.getOpenId(), "index", map2, "djUO_W_b_hm2yMT7-VHltySXkzVvK-pWJHeuKUnevgc");*/

        Map<String, TemplateData> map3 = new HashMap<>();
        //JobCardDetail jobCardDetail1 = getJobCardDetail(userCardDetail);
        map3.put("time3", new TemplateData(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date())));
        map3.put("thing4", new TemplateData("故障类型: OA系统 请评价"));
        GetOpenIdUtil.subscribeMessage(request.getOpenId(), "itPersonInfo", map3, "djUO_W_b_hm2yMT7-VHltySXkzVvK-pWJHeuKUnevgc");
    }
}

package com.zt.util;

import com.zt.entity.common.SubscribeMessage;
import com.zt.entity.common.TemplateData;
import net.sf.json.JSONObject;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
/*
 * 功能描述: <br>
 * 〈从微信获取openId、sessionKey以及unionId〉
 * @Author:
 * @Date:
 */
@Configuration
public class GetOpenIdUtil {

    public static String appId= "wxb55f4592caf030ed";
    public static String secret = "940b0c7eb36a5b231449959b140219f3";
    public  static Map<String,Object> oauth2GetOpenid(String code) {
                 String requestUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=wxb55f4592caf030ed&secret=" +
                         "940b0c7eb36a5b231449959b140219f3&js_code="+code+"&grant_type=authorization_code";
                 HttpClient client = null;
                 Map<String,Object> result =new HashMap<String,Object>();
                 try {
                         client = new DefaultHttpClient();
                         HttpGet httpget = new HttpGet(requestUrl);
                         ResponseHandler<String> responseHandler = new BasicResponseHandler();
                         String response = client.execute(httpget, responseHandler);
                         JSONObject OpenidJSONO=JSONObject.fromObject(response);
                         String openid =String.valueOf(OpenidJSONO.get("openid"));
                         String session_key=String.valueOf(OpenidJSONO.get("session_key"));
                         String unionid=String.valueOf(OpenidJSONO.get("unionid"));
                         String errcode=String.valueOf(OpenidJSONO.get("errcode"));
                         String errmsg=String.valueOf(OpenidJSONO.get("errmsg"));

                         result.put("openId", openid);
                         result.put("sessionKey", session_key);
                         result.put("unionId", unionid);
                         result.put("errCode", errcode);
                         result.put("errMsg", errmsg);
                     } catch (Exception e) {
                         e.printStackTrace();
                     } finally {
                         client.getConnectionManager().shutdown();
                     }
                 return result;
    }

    public static String getAccessToken() throws Exception {
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId
                + "&secret=" + secret;
        HttpClient client = null;
        String accessToken = "";
        try {
            client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(requestUrl);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = client.execute(httpget, responseHandler);
            JSONObject OpenidJSONO=JSONObject.fromObject(response);
            accessToken =String.valueOf(OpenidJSONO.get("access_token"));
            System.err.println("======================access_token" + accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //client.getConnectionManager().shutdown();
        }
        return accessToken;
    }

    public static void subscribeMessage(String openId, String page, Map<String, TemplateData> map, String templateId) {
        try {
            String accessToken = getAccessToken();
            RestTemplate restTemplate = new RestTemplate();
            System.err.println("======================access_token" + accessToken);
            SubscribeMessage subscribeMessage = new SubscribeMessage();
            // 拼接数据
            subscribeMessage.setAccess_token(accessToken);
            subscribeMessage.setTouser(openId);
            subscribeMessage.setTemplate_id(templateId);//事先定义好的模板id
            subscribeMessage.setPage(page);
            subscribeMessage.setMiniprogram_state("formal");
            subscribeMessage.setData(map);
            //HttpClient client = null;
            String requestUrl = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
            try {
                /*client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(requestUrl);
                ObjectMapper objectMapper = new ObjectMapper();
                String zTreeJson = objectMapper.writeValueAsString(subscribeMessage);
                StringEntity entity = new StringEntity(zTreeJson,"UTF-8");
                httpPost.setEntity(entity);
                HttpResponse execute = client.execute(httpPost);
                if (execute.getStatusLine().getStatusCode() == 200) {
                    System.err.println("--------------------发送成功！---------------------");
                }*/
                ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(requestUrl, subscribeMessage, String.class);
                System.err.println("================message: " + stringResponseEntity.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //client.getConnectionManager().shutdown();
            }

            //System.out.println(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

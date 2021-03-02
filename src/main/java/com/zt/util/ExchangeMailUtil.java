package com.zt.util;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/*
 * 功能描述: <br>
 * 〈使用Exchange协议向邮箱服务器发送邮件〉
 * @Param:
 * @Return:
 * @Author:
 * @Date:
 */
public class ExchangeMailUtil {

    // webmail.chint.com   mailb.noark.cn
    //邮件服务器地址 // Outlook Web Access路径通常为 https://要发送的邮件服务器域名/EWS/exchange.asmx
    private static String mailServer = "https://webmail.chint.com/EWS/exchange.asmx";
    //用户名
    private static String user = "itservice_cps@chint.com";    // ITService@noark.cn
    //密码
    private static String password = "itservice";   // 123456!c
    //使用的方式，默认可用不填
    private static String domain;

    /**
     * 构造方法
     */
    public ExchangeMailUtil(String mailServer, String user, String password) {
        this.mailServer = mailServer;
        this.user = user;
        this.password = password;
    }

    /**
     * 构造方法
     */
    public ExchangeMailUtil(String mailServer, String user, String password, String domain) {
        this.mailServer = mailServer;
        this.user = user;
        this.password = password;
        this.domain = domain;
    }

    /**
     * 创建邮件服务
     *
     * @return 邮件服务
     */
    private static ExchangeService getExchangeService() {
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
        //用户认证信息
        ExchangeCredentials credentials;
        if (domain == null) {
            credentials = new WebCredentials(user, password);
        } else {
            credentials = new WebCredentials(user, password, domain);
        }
        service.setCredentials(credentials);
        try {
            service.setUrl(new URI(mailServer));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return service;
    }

    /**
     * 收取邮件
     *
     * @param max 最大收取邮件数
     * @throws Exception
     */
    public ArrayList<EmailMessage> receive(int max) throws Exception {
        ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2007_SP1);
        ExchangeCredentials credentials = new WebCredentials(user, password);
        service.setCredentials(credentials);
        service.setUrl(new URI(mailServer));
        //绑定收件箱,同样可以绑定发件箱
        Folder inbox = Folder.bind(service, WellKnownFolderName.Inbox);
        //获取文件总数量
        int count = inbox.getTotalCount();
        if (max > 0) {
            count = count > max ? max : count;
        }
        //循环获取邮箱邮件
        ItemView view = new ItemView(count);
        FindItemsResults<Item> findResults = service.findItems(inbox.getId(), view);
        ArrayList<EmailMessage> result = new ArrayList<>();
        for (Item item : findResults.getItems()) {
            EmailMessage message = EmailMessage.bind(service, item.getId());
            result.add(message);
        }
        return result;
    }

    /**
     * 收取所有邮件
     * @throws Exception
     */
    public ArrayList<EmailMessage> receive() throws Exception {
        return receive(0);
    }

    /**
     * 发送带附件的mail
     * @param subject 邮件标题
     * @param to 收件人列表
     * @param cc 抄送人列表
     * @param bodyText 邮件内容
     * @param attachmentPaths 附件地址列表
     * @throws Exception
     */
    public static void send(String subject, List<String> to, List<String>  cc, String bodyText, List<String> attachmentPaths) {
        ExchangeService service = getExchangeService();
        try {
            EmailMessage msg = new EmailMessage(service);
            msg.setSubject(subject);
            MessageBody body = MessageBody.getMessageBodyFromText(bodyText);
            body.setBodyType(BodyType.HTML);
            msg.setBody(body);
            for (String toPerson : to) {
                msg.getToRecipients().add(toPerson);
            }
            if (cc != null) {
                for (String ccPerson : cc) {
                    msg.getCcRecipients().add(ccPerson);
                }
            }
            if (attachmentPaths != null) {
                for (String attachmentPath : attachmentPaths) {
                    msg.getAttachments().addFileAttachment(attachmentPath);
                }
            }
            msg.send();
            System.err.println("--------------------发送成功---------------------" + to);
        } catch (Exception ex) {
            System.err.println("--------------------发送失败---------------------" + ex);
        }

    }

    /**
     * 发送不带抄送人的邮件
     * @param subject 标题
     * @param to 收件人列表
     * @param bodyText 邮件内容
     * @throws Exception
     */
    public static void send(String subject,List<String> to,String bodyText) throws Exception {
        send(subject, to, null, bodyText);
    }

    /**
     * 发送不带附件的mail
     * @param subject 邮件标题
     * @param to 收件人列表
     * @param cc 抄送人列表
     * @param bodyText 邮件内容
     * @throws Exception
     */
    public static void send(String subject, List<String> to, List<String> cc, String bodyText) throws Exception {
        send(subject, to, cc, bodyText, null);
    }

    public static void main(String[] args) {
        try {
            List<String> to = Arrays.asList("hzhehao@chint.com");
            send("测试", to, "测试内容");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

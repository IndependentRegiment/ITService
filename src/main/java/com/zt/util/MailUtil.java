package com.zt.util;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
/*
 * 功能描述: <br>
 * 〈使用javaMail采用smtp/pop3协议向邮件服务器发送消息〉
 * @Param:
 * @Return:
 * @Author:
 * @Date:
 */

public class MailUtil {
    private static final String HOST = "jt-smtp-01.chint.com";//对应邮箱类型 smtp.126.com
    private static final Integer PORT = 25;
    private static final String USERNAME = "itservice_cps@chint.com";
    private static final String PASSWORD = "itservice";//QQ的授权码
    private static final String EMAILFORM = "itservice_cps@chint.com";
    private static final String timeout="25000";//超时时间
    private static JavaMailSenderImpl mailSender = createMailSender();
    /**
     * 邮件发送器
     *
     * @return 配置好的工具
     */
    private static JavaMailSenderImpl createMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(HOST);
        sender.setPort(PORT);
        sender.setUsername(USERNAME);
        sender.setPassword(PASSWORD);
        sender.setDefaultEncoding("Utf-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.timeout", timeout);
        p.setProperty("mail.smtp.auth", "false");
        sender.setJavaMailProperties(p);
        return sender;
    }

    /**
     * 发送邮件
     *
     * @param to 接受人
     * @param subject 主题
     * @param message 发送内容
     * @throws MessagingException 异常
     * @throws UnsupportedEncodingException 异常
     */
    public static void sendHtmlMail(String to, String subject, String message) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 设置utf-8或GBK编码，否则邮件会有乱码
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        messageHelper.setFrom(EMAILFORM, "IT服务中心");
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(message, true);
        mailSender.send(mimeMessage);
    }
    /**
     * 发送邮件
     *
     * @param tos 接受人多个
     * @param subject 主题
     * @param message 发送内容
     * @throws MessagingException 异常
     * @throws UnsupportedEncodingException 异常
     */
    public static void sendHtmlMails(String[] tos, String subject, String message) throws MessagingException,UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 设置utf-8或GBK编码，否则邮件会有乱码
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        messageHelper.setFrom(EMAILFORM, "系统名称");
        messageHelper.setTo(tos);
        messageHelper.setSubject(subject);
        messageHelper.setText(message, true);
        mailSender.send(mimeMessage);
    }

    public static void main(String[] args) {
        try{
            sendHtmlMail("hzhehao@chint.com","工单通知","测试内容");
            System.err.println("-------------------发送成功！--------------------");
        }catch (Exception e){
            System.out.println(e);
        }
    }
}

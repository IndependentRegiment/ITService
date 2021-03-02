package com.zt.util;

import org.springframework.context.annotation.Configuration;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class DateUtil {

    // 返回时间类型 yyyy-MM-dd HH:mm:ss
    public static String getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        //ParsePosition pos = new ParsePosition(8);
        //Date currentTime_2 = formatter.parse(dateString, pos);
        return dateString;
    }

    // 返回字符串类型 yyyy-MM-dd HH:mm:ss
    public static String getNowDateStr(Timestamp timestamp) {
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        /*ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(date1, pos);*/
        String format = formatter.format(date);
        return format;
    }

    public static String getNowDateStr2(Timestamp timestamp) {
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        /*ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(date1, pos);*/
        String format = formatter.format(date);
        return format;
    }

    // 返回timestamp类型 yyyy-MM-dd HH:mm:ss
    public static Timestamp getTimeStamp() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        System.out.println("dat");
        //System.err.println("========================date: " + currentTime_2 + ",time:" + currentTime_2.getTime());
        return new Timestamp(currentTime.getTime());
    }

    // 返回timestamp类型 yyyy-MM-dd HH:mm:ss
    public static Timestamp getTimeStampByDate(Date date) {
        return new Timestamp(date.getTime());
    }

    public static Timestamp getTimeStampByStr(String date) {
        if (date.length() < 6) {
            String nowDate = getNowDate();
            String substring = nowDate.substring(0, nowDate.indexOf(" "));
            date = substring + " " + date + ":00";
            System.err.println("-----------------------datetime: " + date);
        } else if (date.length() < 11) {
            String nowDate = getNowDate();
            date = date + nowDate.substring(nowDate.indexOf(" "), nowDate.length());
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = formatter.parse(date);
            Timestamp timeStampByDate = getTimeStampByDate(parse);
            return timeStampByDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getTimeStamp();
    }
}

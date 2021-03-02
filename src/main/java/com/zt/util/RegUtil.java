package com.zt.util;

import org.springframework.context.annotation.Configuration;

import java.util.regex.Pattern;

@Configuration
public class RegUtil {
    /**
     * //验证数据的正则表达式
     检查是否非负浮点数
     /// <param name="InPut">要检查的字串</param>
     /// <returns>bool</returns>
     * @param InPut
     * @return
     */
    public static boolean IsNumeric(String InPut)
    {
        String reg = "^\\d+(\\.\\d+)?$";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 检察是否正确的Email格式
    /// </summary>
    /// <remarks>修改时间：2012-11-01</remarks>
    /// <param name="str">要检查的字串</param>
    /// <returns>bool</returns>
    public static boolean IsEmail(String InPut)
    {
        String reg = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 检查是否正确的座机号码
    /// </summary>
    /// <remarks>修改时间：2012-11-01</remarks>
    /// <param name="InPut">要检查的字符串</param>
    /// <returns>bool</returns>
    public static boolean IsPhone(String InPut)
    {
        // return Regex.IsMatch(InPut, @"(\d3,4|\d{3,4}-|\s)?\d{8}");
        //String reg = "^(\\(\\d{3,4}\\)|\\d{3,4}-|\\s)?\\d{8}$";
        String reg1 = "^(8|7)\\d{5}$";
        return Pattern.matches(reg1, InPut);
    }

    /// <summary>
    /// 检查是否正确的手机号码
    /// </summary>
    /// <remarks>修改时间：2012-11-01</remarks>
    /// <param name="InPut">要检查的字符串</param>
    /// <returns>bool</returns>
    public static boolean IsMobilePhone(String InPut)
    {
        String reg = "^(((13[0-9])|(14[5,7])|(15([0-3]|[5-9]))|(18[0-9])|(17[0,6-8])|(19[0-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 检查是否正确的传真号码
    /// </summary>
    /// <remarks>修改时间：2012-11-01</remarks>
    /// <param name="InPut">要检查的字符串</param>
    /// <returns>bool</returns>
    public static boolean IsFax(String InPut)
    {
        //return Regex.IsMatch(InPut, @"^[+]{0,1}(\d){1,3}[ ]?([-]?((\d)|[ ]){1,12})+$");
        String reg = "^[+]{0,1}(\\d){1,3}[ ]?([-]?((\\d)|[ ]){1,12})+$";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 检查是否正确的邮编号码
    /// </summary>
    /// <param name="InPut">要检查的字符串</param>
    /// <returns>bool</returns>
    public static boolean IsCode(String InPut)
    {
        //return Regex.IsMatch(InPut, @"^\d{6}$");
        String reg = "^\\d{6}$";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 检查是否正确的网络地址
    /// </summary>
    /// <param name="InPut">要检查的字符串</param>
    /// <returns>bool</returns>
    public static boolean IsInternetUrl(String InPut)
    {
        //return Regex.IsMatch(InPut, @"[a-zA-z]+://[^\s]*");
        String reg = "[a-zA-z]+://[^\\s]*";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 检查是否正确的姓名
    /// </summary>
    /// <param name="InPut">要检查的字符串</param>
    /// <returns>bool</returns>
    public static boolean IsName(String InPut)
    {
        // return Regex.IsMatch(InPut, @"[a-zA-Z]{1,20}|[\u4e00-\u9fa5]{1,10}");
        String reg = "[a-zA-Z]{1,20}|[\u4e00-\u9fa5]{1,10}";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 检查是否正确的英文名
    /// </summary>
    /// <param name="InPut">要检查的字符串</param>
    /// <returns>bool</returns>
    public static boolean IsEName(String InPut)
    {
        //return Regex.IsMatch(InPut, @"[a-zA-Z]{1,20}");
        String reg = "[a-zA-Z]{1,20}";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 检察是否正确的日期格式
    /// </summary>
    /// <param name="str">要检查的字串</param>
    /// <returns>bool</returns>
    public static boolean IsDate(String InPut)
    {
        //考虑到了4年一度的366天，还有特殊的2月的日期
        /*Regex reg = new Regex(@"^((((1[6-9]|[2-9]\d)\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\d|3[01]))|(((1[6-9]|[2-9]\d)\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\d|30))|(((1[6-9]|[2-9]\d)\d{2})-0?2-(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$");
        return reg.IsMatch(InPut);*/
        String reg = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 是否是SQL语句
    /// </summary>
    /// <param name="str">要检查的字串</param>
    /// <returns>bool</returns>
    public static boolean IsSQL(String InPut)
    {
       /* Regex reg = new Regex(@"\?|Truncate%20|Truncate\s+|ALTER%20|ALTER\s+|select%20|select\s+|insert%20|insert\s+|delete%20|delete\s+|count\(|drop%20|drop\s+|update%20|update\s+", RegexOptions.IgnoreCase);

        return reg.IsMatch(InPut);*/
        String reg = "\\?|Truncate%20|Truncate\\s+|ALTER%20|ALTER\\s+|select%20|select\\s+|insert%20|insert\\s+|delete%20|delete\\s+|count\\(|drop%20|drop\\s+|update%20|update\\s+";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 是否为IP
    /// </summary>
    /// <param name="InPut">要检查的字符串</param>
    /// <returns>bool</returns>
    public static boolean IsIP(String InPut)
    {
        //return Regex.IsMatch(InPut, @"^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$");
        String reg = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 是否为URL
    /// </summary>
    /// <param name="InPut">要检查的字符串</param>
    /// <returns>bool</returns>
    public static boolean IsURL(String InPut)
    {
        //return Regex.IsMatch(InPut, @"^(http|ftp|file)://.*");
        // return Regex.IsMatch(InPut, @"^(http|ftp|file)://.*");
        String reg ="^(http|ftp|file)://.*";
        return Pattern.matches(reg, InPut);
    }

    /// <summary>
    /// 是否为正确的机构编码
    /// </summary>
    /// <param name="InPut">要检查的字符串</param>
    /// <param name="IsParent">是否为根节点</param>
    /// <returns>bool</returns>
    public static boolean IsUnitCode(String InPut, Boolean IsParent)
    {
        if (IsParent)
            return Pattern.matches("([a-zA-Z0-9]{3}).([a-zA-Z0-9]{3}).([a-zA-Z0-9]{3})", InPut);
        else
            return Pattern.matches("[a-zA-Z0-9]{3}", InPut);
    }


    //验证身份证号码
    /// <summary>
    /// 验证身份证号码
    /// </summary>
    /// <param name="Id">身份证号码</param>
    /// <returns>验证成功为True，否则为False</returns>
    /*public static boolean CheckIDCard(String Id)
    {
        if (Id.length() == 18)
        {
            boolean check = CheckIDCard18(Id);
            return check;
        }
        else if (Id.length() == 15)
        {
            boolean check = CheckIDCard15(Id);
            return check;
        }
        else
        {
            return false;
        }
    }*/

    public static void main(String[] args) {
        String phone = "618182";
        System.out.println(IsMobilePhone(phone));
        System.out.println(IsPhone(phone));
        System.out.println(IsEmail("ccc@sss.com"));
    }
}

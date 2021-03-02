package com.zt.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class MD5Util {

    public static String encrypt(String strSrc) {
        try {
            char hexChars[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            byte[] bytes = strSrc.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            bytes = md.digest();
            int j = bytes.length;
            char[] chars = new char[j * 2];
            int k = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                chars[k++] = hexChars[b >>> 4 & 0xf];
                chars[k++] = hexChars[b & 0xf];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("MD5加密出错！！+" + e);
        }
    }

    public static String Encrypt(String str){
        try {
            MessageDigest md=MessageDigest.getInstance("MD5");
            byte[] s=md.digest(str.getBytes());
            String ss="";
            String result="";
            for(int i=0;i<s.length;i++){
                ss=Integer.toHexString(s[i] & 0xff);
                if(ss.length()==1){
                    result+="0"+ss;
                }else{
                    result+=ss;
                }
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 调用一次加密，调用两次解密convertMD5(convertMD5(内容))
    public static String convertMD5(String inStr){

        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++){
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;

    }

    public static void main(String[] args) {
        System.out.println(MD5Util.encrypt("111111"));
        System.out.println(convertMD5(MD5Util.encrypt("111111")));
        System.out.println(convertMD5(convertMD5(MD5Util.encrypt("111111"))));
        System.out.println(Encrypt("111111"));
        System.out.println(convertMD5("111111"));
        System.out.println(convertMD5(convertMD5("111111")));
    }

}

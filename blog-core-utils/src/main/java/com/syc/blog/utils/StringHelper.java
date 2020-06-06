package com.syc.blog.utils;

import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

public class StringHelper {

    public static String getEncodedStr(String origin, Charset byteEncode, Charset strEncode){
        byte[] bytes = origin.getBytes(byteEncode);
        return new String(bytes,strEncode);
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 验证不合适的词语
     * */
   /* public static boolean hasIllegal(String content) {
        Set<String> list = IllegalWordsHelper.getIllegalWord(content, IllegalWordsHelper.maxMatchType);
        return list.size() != 0;
    }*/

    public static boolean isEmpty(String str) {
        return str == null || str.equals("") || str.trim().length() == 0;
    }


    private final static String[] AGENT = { "Android", "iPhone", "iPod","iPad", "Windows Phone", "MQQBrowser" }; //定义移动端请求的所有可能类型
    /**
     * 判断User-Agent 是不是来自于手机
     * @param ua
     * @return
     */
    public static boolean checkAgentIsMobile(String ua) {
        boolean flag = false;
        if (!ua.contains("Windows NT") || (ua.contains("Windows NT") && ua.contains("compatible; MSIE 9.0;"))) {
            // 排除 苹果桌面系统
            if (!ua.contains("Windows NT") && !ua.contains("Macintosh")) {
                for (String item : AGENT) {
                    if (ua.contains(item)) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }

}

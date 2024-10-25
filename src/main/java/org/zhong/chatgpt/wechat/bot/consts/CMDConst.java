package org.zhong.chatgpt.wechat.bot.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * 指令集合
 */
public class CMDConst {
    public static String HELP = "help";

    public static String PIC = "pic";


    public static Map<String,String> getAllCmd(){
        Map<String,String> result = new HashMap<>();
        result.put(HELP,"获取所有指令");
        result.put(PIC,"后面接搜索字段，随机爬取一张图片");

        return result;
    }
}

package org.zhong.chatgpt.wechat.bot.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * 指令集合
 */
public class CMDConst {
    public static String HELP = "指令";

    public static String PIC = "搜图";

    public static String TIANGOU = "舔狗";

    public static String NEWS = "新闻";

    public static String TWOONE = "21点";

    public static String JIELONG = "成语接龙";


    public static Map<String,String> getAllCmd(){
        Map<String,String> result = new HashMap<>();
        result.put(HELP,"获取所有指令");
        result.put(PIC,"后面接搜索字段，随机爬取一张图片");
        result.put(TIANGOU,"随机返回一句舔狗日记");
        result.put(NEWS,"获取今日新闻简报");
        result.put(JIELONG,"成语接龙");
        return result;
    }
}

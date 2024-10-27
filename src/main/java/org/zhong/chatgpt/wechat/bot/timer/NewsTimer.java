package org.zhong.chatgpt.wechat.bot.timer;

import cn.hutool.extra.spring.SpringUtil;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import freemarker.template.utility.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;
import org.zhong.chatgpt.wechat.bot.util.NewsProcessor;

import java.util.List;

@Component
public class NewsTimer {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private NewsProcessor newsProcessor;
    private static Logger LOG = LoggerFactory.getLogger(NewsTimer.class);
    @Scheduled(cron = "0 6 * * * *")
    public void run(){
        LOG.info("每日新闻线程启动");
        //获取白名单群组
        List<String> whiteList =  botConfig.getGroupWhiteList();
        String content = newsProcessor.getNewsContent();

        //循环100次获取数据
        for(int i = 0;i< 100;i++){
            // 如果没数据，继续获取
            if(!StringUtils.isEmpty(content)){
                break;
            } else {
                content = newsProcessor.getNewsContent();
            }
            try {
                Thread.sleep(600000);
            } catch (Exception e){
                e.printStackTrace();
            }
            if(i==99){
                return;
            }
        }

        for(String o: whiteList){
            MessageTools.sendMsgById(content, WechatTools.getGroupIdByNickName(o));
        }
    }
}

package org.zhong.chatgpt.wechat.bot.timer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.Log;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import com.alibaba.fastjson.JSONObject;
import freemarker.template.utility.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;
import org.zhong.chatgpt.wechat.bot.service.MpWechatService;
import org.zhong.chatgpt.wechat.bot.util.NewsProcessor;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class NewsTimer {

    @Autowired
    private BotConfig botConfig;

    @Autowired
    private NewsProcessor newsProcessor;

    @Autowired
    private MpWechatService mpWechatService;
    private static Logger LOG = LoggerFactory.getLogger(NewsTimer.class);
    @Scheduled(cron = "0 0 6 * * ?")
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


    @Scheduled(cron = "0 0 6 * * ?")
    public void privateTalk(){
        LOG.info("每日通知微信用户线程启动");
        //获取白名单群组
        List<String> whiteList =  botConfig.getUserWhiteList();
        String path = botConfig.getWorkspace() + "/config/temp.txt";
        File file = new File(path);
        if(!file.exists()){
            LOG.info("未配置发送消息");
            return;
        }
        String content = FileUtil.readString(file,"utf-8");
        if(StringUtils.isEmpty(content)){
            LOG.info("发送消息为空");
            return;
        }
        for(String o: whiteList){
            MessageTools.sendMsgById(content,WechatTools.getUserNameByNickName(o));
        }
    }

    @Scheduled(cron = "0 20 6 * * ?")
    public void startGetNews() throws IOException {
        LOG.info("每日爬取公众号新闻线程启动");
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd");
        String date = today.format(dateTimeFormatter);
        String text = "";
        for(int i = 0;i< 100;i++){
            LOG.info("第" + i + "次爬取新闻");
            JSONObject json = mpWechatService.getMpUrlJSON();
            text = mpWechatService.parseMpJson(json);
            if(text.contains(date)){
                break;
            }
            try {
                Thread.sleep(300000);
            } catch (Exception e){
                e.printStackTrace();
            }
            if(i==99){
                return;
            }
            LOG.info("爬取到的数据为" + text);
        }
        mpWechatService.saveMpText(text);
    }

}

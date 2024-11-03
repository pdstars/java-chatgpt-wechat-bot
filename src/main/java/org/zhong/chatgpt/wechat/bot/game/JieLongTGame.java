package org.zhong.chatgpt.wechat.bot.game;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.zhong.chatgpt.wechat.bot.Repository.IdiomRepository;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;
import org.zhong.chatgpt.wechat.bot.entity.Idiom;
import org.zhong.chatgpt.wechat.bot.model.BotMsg;
import org.zhong.chatgpt.wechat.bot.model.BotMsgLinkedList;
import org.zhong.chatgpt.wechat.bot.model.WehchatMsgQueue;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class JieLongTGame implements Runnable {
    //记录成员的积分
    private Map<String,Integer> gameData = new HashMap();

    //记录游戏成语接龙队列
    private List<String> gameWorkList = new ArrayList();

    //接龙持续多少次就结束
    private int workCount = 10;

    //群组名称
    private String groupName;

    private BotMsgLinkedList botQueue = new BotMsgLinkedList();

    //当前状态  0-结束了  1-游戏中
    private String flag = "0";

    //超时时间
    private int TimeOut = 60000;

    private IdiomRepository idiomRepository;

    public JieLongTGame(String groupName){
        this.groupName = groupName;
        this.idiomRepository = SpringUtil.getBean(IdiomRepository.class);
    }



    public void startGame(BotMsg botMsg) {
        this.flag = "1";
        BaseMsg baseMsg = botMsg.getBaseMsg();
        String content = "";
        MessageTools.sendMsgById(content, WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
    }

    public void endGame(BaseMsg baseMsg) {
        //游戏结束逻辑
        this.flag = "0";
    }



    /**
     * 判断游戏是否在进行
     * @param groupName
     * @return
     */
    public boolean checkGame(String groupName){
        String flag = this.flag;
        if(flag == "0"){
            return false;
        }
        return true;
    }

    //消息预处理
    public void setGameMagLinkList(BotMsg botMsg) throws InterruptedException {
        BaseMsg baseMsg = botMsg.getBaseMsg();
        this.botQueue.blockPush(botMsg);
    }

    @Override
    public void run() {
        while(true){
            try{
                BotMsg botMsg = botQueue.blockPop();
                BaseMsg baseMsg = botMsg.getBaseMsg();

                // 获取文本
                String text = baseMsg.getContent();
                text = text.replaceAll(" ","").replaceAll(" ","");
                Idiom idiom = new Idiom();
                idiom.setWord(text);
                Example<Idiom> example = Example.of(idiom);
                Optional<Idiom> result = idiomRepository.findOne(example);
                System.out.println(result);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

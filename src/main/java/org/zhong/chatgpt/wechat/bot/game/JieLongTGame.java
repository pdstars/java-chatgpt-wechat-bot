package org.zhong.chatgpt.wechat.bot.game;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.controller.ApiController;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger LOG = LoggerFactory.getLogger(JieLongTGame.class);
    //记录成员的积分
    private Map<String,Integer> gameData = new HashMap();

    //记录游戏成语接龙队列
    private List<Idiom> gameWorkList = new ArrayList();

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
        String content = "欢迎来玩成语接龙，你说成语我来接，同音字亦可，每题80秒时间；30秒后玩不下去【求助】【退出接龙】，请发一个4个字的成语";
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
                if(gameWorkList.size() != 0){
                    idiom.setFirst(gameWorkList.get(gameWorkList.size() -1).getLast());
                }
                Example<Idiom> example = Example.of(idiom);

                Optional<Idiom> result = idiomRepository.findOne(example);
                if(!result.isPresent()){
                    LOG.info(String.format("找不到word为[%s]的成语",text));
                    continue;
                }
                Idiom relidiom = result.get();
                String relMsg = "成语：%s\n" +
                        "拼音：%s\n" +
                        "解释：%s\n" +
                        "例子：%s";
                MessageTools.sendMsgById(String.format(relMsg,relidiom.getWord(),relidiom.getPinyin(),relidiom.getExplanation(),relidiom.getExample()), WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

package org.zhong.chatgpt.wechat.bot.game;

import cn.hutool.core.io.FileUtil;
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

@Component
public class JieLongTGame{
    private Map<String, Map<String,Integer>> gameData = new HashMap();

    /**
     * 记录每个群的状态 1是游戏中，0是准备中
     */
    private Map<String,String> flagMap = new HashMap<>();

    private JSONObject works = new JSONObject();

    @Autowired
    BotConfig botConfig;

    @Autowired
    IdiomRepository idiomRepository;



    public void startGame(BotMsg botMsg) {
        BaseMsg baseMsg = botMsg.getBaseMsg();
        //开始游戏
        flagMap.put(baseMsg.getGroupName(),"1");
    }

    public void endGame(BaseMsg baseMsg) {
        //游戏结束逻辑
        this.gameData.remove(baseMsg.getGroupName());
        this.flagMap.remove(baseMsg.getGroupName());
    }

    @PostConstruct
    public void process(BotMsg botMsg) {
        for(;;){
            try{
                BaseMsg baseMsg = botMsg.getBaseMsg();
                String groupName = baseMsg.getGroupName();

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

    /**
     * 判断游戏是否在进行
     * @param groupName
     * @return
     */
    public boolean checkGame(String groupName){
        String flag = flagMap.get(groupName);
        if(flag == null){
            return false;
        }
        return true;
    }

    public void setGameMagLinkList(BotMsg botMsg) throws InterruptedException {
        BaseMsg baseMsg = botMsg.getBaseMsg();
        String text = baseMsg.getContent();
        text = text.replaceAll(" ","").replaceAll(" ","");
        if("成语接龙".equals(text)){
            this.startGame(botMsg);
        } else if(!checkGame(baseMsg.getGroupName())){
            return;
        }

        //判断此群组名的
        this.process(botMsg);
    }
}

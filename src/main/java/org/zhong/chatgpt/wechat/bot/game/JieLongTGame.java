package org.zhong.chatgpt.wechat.bot.game;

import cn.hutool.core.io.FileUtil;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;
import org.zhong.chatgpt.wechat.bot.model.BotMsg;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class JieLongTGame extends GameAbstrat{
    private Map<String, Map<String,Integer>> gameData = new HashMap();

    /**
     * 记录每个群的状态 1是游戏中，0是准备中
     */
    private Map<String,String> flagMap = new HashMap<>();

    private JSONObject works = new JSONObject();
    @Autowired
    BotConfig botConfig;



    @Override
    public void startGame(BotMsg botMsg) {
        BaseMsg baseMsg = botMsg.getBaseMsg();
        //开始游戏
        flagMap.put(baseMsg.getGroupName(),"1");
    }

    @Override
    public void endGame(BaseMsg baseMsg) {
        //游戏结束逻辑
        this.gameData.remove(baseMsg.getGroupName());
        this.flagMap.remove(baseMsg.getGroupName());
    }

    @Override
    public void process(BotMsg botMsg) {
        BaseMsg baseMsg = botMsg.getBaseMsg();
        if(!this.checkGame(baseMsg.getGroupName())){
            return;
        }
        String text = baseMsg.getText();
        if((botConfig.getAtBotName() +" 发牌").equals(text) || (botConfig.getAtBotName() +" 发牌").equals(text)){
            //发牌逻辑


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
}

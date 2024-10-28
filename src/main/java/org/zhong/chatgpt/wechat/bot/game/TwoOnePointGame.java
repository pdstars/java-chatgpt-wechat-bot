package org.zhong.chatgpt.wechat.bot.game;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;
import org.zhong.chatgpt.wechat.bot.model.BotMsg;

import java.util.*;

@Component
public class TwoOnePointGame extends  GameAbstrat{
    private Map<String, Map<String, List<Integer>>> gameData = new HashMap();

    /**
     * 记录每个群的状态 1是游戏中，0是准备中
     */
    private Map<String,String> flagMap = new HashMap<>();

    String temp = "";

    @Autowired
    BotConfig botConfig;


    @Override
    public void startGame(BotMsg botMsg) {
        BaseMsg baseMsg = botMsg.getBaseMsg();
        Map<String,List<Integer>> map = gameData.get(baseMsg.getGroupName());
        if(map.size() == 0){
            String content = "当前玩家数量少于1，不可开始游戏";
            MessageTools.sendMsgById(content,WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            return;
        }
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
        //如果不在游戏状态，直接退出
        if(!this.checkGame(baseMsg.getGroupName())){
            return;
        }

        String text = baseMsg.getText();
        this.ready(botMsg);
        if((botConfig.getAtBotName() +" 开始").equals(text)){
            String flag = flagMap.get(baseMsg.getGroupName());
            if("0".equals(flag)){
                this.startGame(botMsg);
            } else {
                String content = "只有准备状态才能开始游戏";
                MessageTools.sendMsgById(content,WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            }

        } else if((botConfig.getAtBotName() +" 发牌").equals(text)){
            //发牌逻辑
            Random random = new Random();
            Map<String,List<Integer>> map = gameData.get(baseMsg.getGroupName());
            Set<String> keys = map.keySet();
            for(String key: keys){
                Integer integer = random.nextInt(11);
                List<Integer> list = map.get(key);
                list.add(integer);
            }
            this.isFinsh(baseMsg);

        }else if((botConfig.getAtBotName() +" 加入").equals(text)){
            String flag = flagMap.get(baseMsg.getGroupName());
            //准备阶段才能加入
            if("0".equals(flag)){
                Map<String,List<Integer>> map = gameData.get(baseMsg.getGroupName());
                map.put(baseMsg.getGroupUserNickName(),new ArrayList<>());
                String content = "加入成功";
                MessageTools.sendMsgById(content,WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            } else {
                String content = "当前阶段不允许加入";
                MessageTools.sendMsgById(content,WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            }

        }else if((botConfig.getAtBotName() +" 退出").equals(text)){
            this.endGame(baseMsg);
            String content = "退出成功";
            MessageTools.sendMsgById(content,WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
        }


    }

    public void isFinsh(BaseMsg baseMsg){
        Map<String,List<Integer>> map = gameData.get(baseMsg.getGroupName());
        Set<String> keys = map.keySet();
        String content = "============\n";
        for(String key: keys){
            List<Integer> list = map.get(key);
            Integer result = 0;
            content = content + key + ": ,";
            for(Integer i: list){
                result = result + i;
                content = content + "," + i;
            }
            content = content + "\n";

        }
        MessageTools.sendMsgById(content,WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
    }

    public void ready(BotMsg botMsg){
        BaseMsg baseMsg = botMsg.getBaseMsg();
        String flag = flagMap.get(baseMsg.getGroupName());

        if(!this.checkGame(baseMsg.getGroupName())){
            flag = "0";
        }
        if(!"0".equals(flag)){
            return;
        }
        if(flagMap.get(baseMsg.getGroupName()) == null){
            flagMap.put(baseMsg.getGroupName(),"0");
        }
        String groupName = baseMsg.getGroupName();
        if(gameData.get(groupName) == null){
            gameData.put(groupName,new HashMap<>());
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

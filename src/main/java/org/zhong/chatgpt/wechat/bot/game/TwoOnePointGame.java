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

    private Map<String,String> userData = new HashMap<>();
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
        this.ready(botMsg);
        Map<String,List<Integer>> map = gameData.get(baseMsg.getGroupName());
        map.put(botMsg.getBaseMsg().getGroupUserNickName(),new ArrayList<>());
        map.put("庄家",new ArrayList<>());
        flagMap.put(baseMsg.getGroupName(),"1");
        userData.put(baseMsg.getGroupName(),botMsg.getBaseMsg().getGroupUserNickName());
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
        if((botConfig.getAtBotName() +" 发牌").equals(text) || (botConfig.getAtBotName() +" 发牌").equals(text)){
            //发牌逻辑
            Random random = new Random();
            Map<String,List<Integer>> map = gameData.get(baseMsg.getGroupName());
            String user = userData.get(baseMsg.getGroupName());
            map.get(user).add(random.nextInt(10) + 1);
            map.get("庄家").add(random.nextInt(10) + 1);
            this.sendNowData(baseMsg);

        }else if((botConfig.getAtBotName() +" 退出").equals(text) || (botConfig.getAtBotName() +" 退出").equals(text)){
            this.endGame(baseMsg);
            String content = "退出成功";
            MessageTools.sendMsgById(content,WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            return;
        } else if((botConfig.getAtBotName() +" 结算").equals(text) || (botConfig.getAtBotName() +" 结算").equals(text)){

            //这里判断胜利条件
            this.isFinsh(baseMsg);
        }


    }

    /**
     * 判断胜利条件
     * @param baseMsg
     */
    public void isFinsh(BaseMsg baseMsg){
        Map<String,List<Integer>> map = gameData.get(baseMsg.getGroupName());
        List<Integer> zj = map.get("庄家");
        String user = userData.get(baseMsg.getGroupName());
        List<Integer> wj = map.get(user);
        if(getSum(wj) == 21){
            MessageTools.sendMsgById("你赢了",WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            this.endGame(baseMsg);
            return;
        }
        if(getSum(wj) > 21){
            MessageTools.sendMsgById("你输了",WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            this.endGame(baseMsg);
            return;
        }

        if(21 - getSum(zj) > 5){
            while(true){
                Random random = new Random();
                map.get("庄家").add(random.nextInt(10) + 1);
                zj = map.get("庄家");
                this.sendNowData(baseMsg);
                if((getSum(zj) > 21) && (21 - getSum(zj)) < 5){
                    break;
                }
            }
        }
        if(getSum(zj) > 21){
            MessageTools.sendMsgById("你赢了",WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            this.endGame(baseMsg);
            return;
        }
        //如果都没超过21
        Integer wj21 = 21 - getSum(wj);
        Integer zj21 = 21 - getSum(zj);
        if(wj21 < zj21){
            MessageTools.sendMsgById("你赢了",WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            this.endGame(baseMsg);
            return;
        } else {
            MessageTools.sendMsgById("你输了",WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            this.endGame(baseMsg);
            return;
        }
    }

    /**
     * 打印当前群组的牌点
     * @param baseMsg
     */
    public void sendNowData(BaseMsg baseMsg){
        StringBuilder sb = new StringBuilder();
        Map<String,List<Integer>> map = gameData.get(baseMsg.getGroupName());
        sb.append( "============\n");
        sb.append("庄家手牌: " + map.get("庄家").toString() + "\n");
        String user = userData.get(baseMsg.getGroupName());
        sb.append(user + "手牌" + map.get(user).toString() + "\n");
        sb.append("请选择是发牌还是结算\n");
        sb.append( "============\n");
        MessageTools.sendMsgById(sb.toString(),WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
    }
    public void ready(BotMsg botMsg){
        BaseMsg baseMsg = botMsg.getBaseMsg();
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

    public Integer getSum(List<Integer> list){
        Integer sum = 0;
        for(Integer i: list){
            sum = sum + i;
        }
        return sum;
    }
}

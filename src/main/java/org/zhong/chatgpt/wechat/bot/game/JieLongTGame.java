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
import java.util.stream.Collectors;

/**
 * 成语接龙小游戏
 */
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

    //当前空闲时间
    private int idleTime = 0;

    //定时器
    private Timer timer;

    private IdiomRepository idiomRepository;

    public JieLongTGame(String groupName){
        this.groupName = groupName;
        this.idiomRepository = SpringUtil.getBean(IdiomRepository.class);
    }



    public void startGame(BotMsg botMsg) {
        this.flag = "1";
        this.idleTime = 0;
        this.startMonitoring();
        BaseMsg baseMsg = botMsg.getBaseMsg();
        String content = "欢迎来玩成语接龙，你说成语我来接，同音字亦可，每题80秒时间；玩不下去【退出成语接龙】，请发一个4个字的成语";
        MessageTools.sendMsgById(content, WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
    }

    public void endGame() {
        if(this.flag != "1"){
            return;
        }
        //游戏结束逻辑
        this.flag = "0";
        StringBuilder sb = new StringBuilder();
        sb.append("成语接龙排行榜\n");

        Map<String, Integer> sortedMap = this.gameData.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e2,
                        LinkedHashMap::new
                ));
        Set<String> userSet = sortedMap.keySet();
        List<String> userList = new ArrayList<>(userSet);
        for(int i = 0;i<userSet.size();i++){
            String userName = userList.get(i);
            sb.append(String.format("第%s名：%s次，%s\n",i + 1,this.gameData.get(userName),userName));
        }
        MessageTools.sendMsgById(sb.toString(), WechatTools.getGroupIdByNickName(groupName));
        this.gameData.clear();
        this.gameWorkList.clear();
        this.timer.cancel();
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
        // 判断当前是否在游戏中
        if(!this.flag.equals("1")){
           return;
        }
        this.botQueue.blockPush(botMsg);
    }

    public void startMonitoring() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                idleTime += 1000; // 每秒增加一次空闲时间
                if (idleTime >= TimeOut) {
                    String content = "游戏超时，提前结束";
                    MessageTools.sendMsgById(content, WechatTools.getGroupIdByNickName(groupName));
                    endGame();
                    timer.cancel();
                    return;
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);

    }

    private void resetIdleTime(){
        this.idleTime = 0;
    }


    @Override
    public void run() {
        while(true){
            try{
                BotMsg botMsg = botQueue.blockPop();
                BaseMsg baseMsg = botMsg.getBaseMsg();
                this.resetIdleTime();
                // 获取文本
                String text = baseMsg.getContent();
                text = text.replaceAll(" ","").replaceAll(" ","");
                Idiom idiom = new Idiom();
                idiom.setWord(text);
                if(gameWorkList.size() != 0){
                    boolean f = false;
                    //判断列表是否出现过这个单词
                    for(Idiom item: gameWorkList){
                        if(item.getWord().equals(text)){
                            MessageTools.sendMsgById("已经用过的成语不可再使用", WechatTools.getGroupIdByNickName(groupName));
                            f=true;
                        }
                    }
                    if(f){
                        continue;
                    }
                    idiom.setFirst(gameWorkList.get(gameWorkList.size() -1).getLast());
                }
                Example<Idiom> example = Example.of(idiom);
                Optional<Idiom> result = idiomRepository.findOne(example);
                if(!result.isPresent()){
                    LOG.info(String.format("找不到word为[%s]的成语",text));
                    continue;
                }
                Idiom relidiom = result.get();
                String relMsg = "恭喜，接龙成功\n" +
                        "成语：%s\n" +
                        "拼音：%s\n" +
                        "解释：%s\n" +
                        "奖励：一积分";
                this.gameWorkList.add(relidiom);
                //获取玩家积分
                Integer userScore = this.gameData.get(baseMsg.getGroupUserNickName());
                if(userScore == null){
                    userScore = 0;
                }
                userScore = userScore + 1;
                this.gameData.put(baseMsg.getGroupUserNickName(),userScore);
                MessageTools.sendMsgById(String.format(relMsg,relidiom.getWord(),relidiom.getPinyin(),relidiom.getExplanation()), WechatTools.getGroupIdByNickName(baseMsg.getGroupName()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

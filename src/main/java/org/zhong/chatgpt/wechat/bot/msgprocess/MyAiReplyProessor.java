package org.zhong.chatgpt.wechat.bot.msgprocess;

import cn.hutool.extra.spring.SpringUtil;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;
import org.zhong.chatgpt.wechat.bot.consts.CMDConst;
import org.zhong.chatgpt.wechat.bot.game.JieLongTGame;
import org.zhong.chatgpt.wechat.bot.game.TwoOnePointGame;
import org.zhong.chatgpt.wechat.bot.model.BotMsg;
import org.zhong.chatgpt.wechat.bot.util.NewsProcessor;
import org.zhong.chatgpt.wechat.bot.util.SougouImgProcessor;
import org.zhong.chatgpt.wechat.bot.util.TianGProcessor;

import java.io.File;
import java.util.Map;

public class MyAiReplyProessor implements MsgProcessor{
    @Override
    public void process(BotMsg botMsg) {
        try{
            BaseMsg baseMsg = botMsg.getBaseMsg();
            String userName = botMsg.getUserName();
            // 自己的机器人
            String text = botMsg.getBaseMsg().getContent();
            Map<String,String> cmdKey = CMDConst.getAllCmd();
            BotConfig botConfig = SpringUtil.getBean(BotConfig.class);
            String result = "";

            for(String cmd : cmdKey.keySet()) {
                if ((text.contains(cmd))) {
                    if (cmd.equals(CMDConst.HELP)) {
                        result = "=====指令大全=====\n";
                        ;
                        for (String cmd2 : cmdKey.keySet()) {
                            result = result + cmd2 + "\n";
                        }
                        result = result + "=================";
                        MessageTools.sendMsgById(result, botMsg.getBaseMsg().getFromUserName());
                    }
                    if (cmd.contains(CMDConst.PIC)) {
                        String query = text.split(" ")[1];
                        SougouImgProcessor processor = new SougouImgProcessor();
                        processor.process(0, 50, query);
                        processor.pipelineData();
                        String basePath = botConfig.getWorkspace() + "/pipeline/sougou" + "/" + query;
                        File dic = new File(basePath);
                        File[] files = dic.listFiles();
                        //随机数
                        int random = (int) (Math.random() * files.length);
                        MessageTools.sendPicMsgByUserId(botMsg.getBaseMsg().getFromUserName(), files[random].getPath());
                    }
                    if (cmd.equals(CMDConst.TIANGOU)) {
                        TianGProcessor tianGProcessor = SpringUtil.getBean(TianGProcessor.class);
                        String replyText = tianGProcessor.process();
                        MessageTools.sendMsgById(replyText, botMsg.getBaseMsg().getFromUserName());
                    }
                    if (cmd.equals(CMDConst.NEWS)) {
                        NewsProcessor newsProcessor = SpringUtil.getBean(NewsProcessor.class);
                        String content = newsProcessor.getNewsContent();
                        MessageTools.sendMsgById(content, botMsg.getBaseMsg().getFromUserName());
                    }
                    if (cmd.equals(CMDConst.TWOONE)) {
                        TwoOnePointGame game = SpringUtil.getBean(TwoOnePointGame.class);
                        game.startGame(botMsg);
                        String content = "游戏开始，请选择发牌或者结算";
                        MessageTools.sendMsgById(content, botMsg.getBaseMsg().getFromUserName());
                    }
                }
            }

            if(baseMsg.isGroupMsg()){
                JieLongTGame jieLongTGame = SpringUtil.getBean(JieLongTGame.class);
                jieLongTGame.setGameMagLinkList(botMsg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

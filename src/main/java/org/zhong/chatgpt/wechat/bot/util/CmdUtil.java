package org.zhong.chatgpt.wechat.bot.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;
import org.zhong.chatgpt.wechat.bot.consts.BotConst;
import org.zhong.chatgpt.wechat.bot.consts.CMDConst;
import org.zhong.chatgpt.wechat.bot.game.TwoOnePointGame;
import org.zhong.chatgpt.wechat.bot.model.BotMsg;
import org.zhong.chatgpt.wechat.bot.model.WehchatMsgQueue;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Map;

public class CmdUtil {


    /**
     * 处理指令
     * @param botMsg 消息上下文
     * @param type 类型 1.私聊 0.群聊
     */
    public static void cmd(BotMsg botMsg,String type){
        String text = botMsg.getBaseMsg().getText();
        Map<String,String> cmdKey = CMDConst.getAllCmd();
        BotConfig botConfig = SpringUtil.getBean(BotConfig.class);
        String result = "";


        for(String cmd : cmdKey.keySet()){
            if((type.equals("0") && text.contains(botConfig.getAtBotName() + " " +cmd)) || (type.equals("1") &&text.contains(cmd))){
                if(cmd.equals(CMDConst.HELP)){
                    result = "============\n";
                    for(String cmd2 : cmdKey.keySet()){
                        result = result + cmd2 + "\n";
                    }
                    result = result + "============";
                    MessageTools.sendMsgById(result,botMsg.getBaseMsg().getFromUserName());
                }
                if(cmd.equals(CMDConst.PIC)){
                    String query = text.split(" ")[1];
                    SougouImgProcessor processor = new SougouImgProcessor();
                    processor.process(0, 50,query);
                    processor.pipelineData();
                    String basePath = botConfig.getWorkspace() + "/pipeline/sougou" + "/" + query;
                    File dic = new File(basePath);
                    File[] files = dic.listFiles();
                    //随机数
                    int random = (int)(Math.random() * files.length);
                    MessageTools.sendPicMsgByUserId(botMsg.getBaseMsg().getFromUserName(),files[random].getPath());
                }
                if(cmd.equals(CMDConst.TIANGOU)){
                    TianGProcessor tianGProcessor = SpringUtil.getBean(TianGProcessor.class);
                    String replyText = tianGProcessor.process();
                    MessageTools.sendMsgById(replyText,botMsg.getBaseMsg().getFromUserName());
                }
                if(cmd.equals(CMDConst.NEWS)){
                    NewsProcessor newsProcessor = SpringUtil.getBean(NewsProcessor.class);
                    String content = newsProcessor.getNewsContent();
                    MessageTools.sendMsgById(content,botMsg.getBaseMsg().getFromUserName());
                }
                if(cmd.equals(CMDConst.TWOONE)){
                    TwoOnePointGame game = SpringUtil.getBean(TwoOnePointGame.class);
                    game.startGame(botMsg);
                    String content = "游戏开始，请选择发牌或者结算";
                    MessageTools.sendMsgById(content,botMsg.getBaseMsg().getFromUserName());
                }
            }
        }
    }



    public static void main(String[] args) {
    }
}

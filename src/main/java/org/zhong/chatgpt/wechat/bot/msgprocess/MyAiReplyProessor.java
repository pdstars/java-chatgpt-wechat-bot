package org.zhong.chatgpt.wechat.bot.msgprocess;

import cn.hutool.extra.spring.SpringUtil;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import freemarker.template.utility.StringUtil;
import org.springframework.util.StringUtils;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;
import org.zhong.chatgpt.wechat.bot.consts.CMDConst;
import org.zhong.chatgpt.wechat.bot.game.JieLongTGame;
import org.zhong.chatgpt.wechat.bot.game.TwoOnePointGame;
import org.zhong.chatgpt.wechat.bot.model.BotMsg;
import org.zhong.chatgpt.wechat.bot.util.NewsProcessor;
import org.zhong.chatgpt.wechat.bot.util.SougouImgProcessor;
import org.zhong.chatgpt.wechat.bot.util.TianGProcessor;
import org.zhong.chatgpt.wechat.bot.util.ViliImgPipeline;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyAiReplyProessor implements MsgProcessor{

    Map<String,JieLongTGame> jieLongTGameMap = new HashMap<>();
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
            if(text.equals(" tex")){
                MessageTools.sendPicMsgByUserId(botMsg.getBaseMsg().getFromUserName(), "D:\\botSpace\\pipeline\\viliImg\\default\\02bde3a9641d4909839e27b3069e4988.jpg");
            }

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
                        String regex = CMDConst.PIC + "(.*)";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(text);
                        matcher.find();
                        String query = matcher.group(1);
                        //如果出现了. 则代表数量
                        Integer num = 1;
                        String queryText = query;
                        if(query.contains(".")){
                            num = Integer.parseInt(query.split("\\.")[1]);
                        }
                        queryText = query.split("\\.")[0].replaceAll(" ","");
//                        SougouImgProcessor processor = new SougouImgProcessor();
//                        processor.process(0, 50, query);
//                        processor.pipelineData();
                        ViliImgPipeline viliImgPipeline = new ViliImgPipeline();
                        viliImgPipeline.process(queryText);
                        viliImgPipeline.processSync();
                        if(StringUtils.isEmpty(queryText)){
                            queryText = "default";
                        }
                        String basePath = botConfig.getWorkspace() + "/pipeline/viliImg" + "/" + queryText;
                        File dic = new File(basePath);
                        File[] files = dic.listFiles();
                        for(int i = 0; i<num;i++){
                            //随机数
                            int random = (int) (Math.random() * files.length);
                            MessageTools.sendPicMsgByUserId(botMsg.getBaseMsg().getFromUserName(), files[random].getPath());
                            Thread.sleep(1000);
                        }
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
                    if(cmd.contains(CMDConst.TIANQI)){
                        //解析
                    }
                }
            }

            if(baseMsg.isGroupMsg()){
                String content = baseMsg.getContent();
                content = content.replaceAll(" ","").replaceAll(" ","");
                if("成语接龙".equals(content)){
                    JieLongTGame jieLongTGame = jieLongTGameMap.get(baseMsg.getGroupName());
                    if(jieLongTGame == null){
                        jieLongTGame = new JieLongTGame(baseMsg.getGroupName());
                        new Thread(jieLongTGame).start();
                    }
                    jieLongTGameMap.put(baseMsg.getGroupName(),jieLongTGame);
                    jieLongTGame.startGame(botMsg);
                } else if ("结束成语接龙".equals(content)){
                    JieLongTGame jieLongTGame = jieLongTGameMap.get(baseMsg.getGroupName());
                    if(jieLongTGame == null){
                        return;
                    }
                    jieLongTGame.endGame();
                } else {
                    JieLongTGame jieLongTGame = jieLongTGameMap.get(baseMsg.getGroupName());
                    if(jieLongTGame == null){
                        return;
                    }
                    jieLongTGame.setGameMagLinkList(botMsg);
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

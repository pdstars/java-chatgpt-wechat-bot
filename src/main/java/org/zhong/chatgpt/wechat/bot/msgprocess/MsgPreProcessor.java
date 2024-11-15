package org.zhong.chatgpt.wechat.bot.msgprocess;

import cn.hutool.extra.spring.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;
import org.zhong.chatgpt.wechat.bot.consts.BotConst;
import org.zhong.chatgpt.wechat.bot.game.TwoOnePointGame;
import org.zhong.chatgpt.wechat.bot.model.BotMsg;
import org.zhong.chatgpt.wechat.bot.model.WehchatMsgQueue;
import org.zhong.chatgpt.wechat.bot.sensitive.SensitiveWord;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;
import org.zhong.chatgpt.wechat.bot.util.NewsProcessor;
import org.zhong.chatgpt.wechat.bot.util.TianGProcessor;

public class MsgPreProcessor implements MsgProcessor{

	TimedCache<String, String> timedCache = CacheUtil.newTimedCache(20*60*1000);
	private static Logger LOG = LoggerFactory.getLogger(MsgPreProcessor.class);
	public void process(BotMsg botMsg) {
		BotConfig botConfig = SpringUtil.getBean(BotConfig.class);
		BaseMsg baseMsg = botMsg.getBaseMsg();
		String fromUserNickName = baseMsg.getFromUserNickName();
		if(StringUtils.isNotEmpty(fromUserNickName)
				&&(	fromUserNickName.contains("微信支付")
						|| fromUserNickName.contains("文件传输助手")
						|| fromUserNickName.contains("微信团队"))) {
			//忽略系统消息
			return;
		}
		
		if((baseMsg.isGroupMsg() && timedCache.get(baseMsg.getGroupUserName()) != null)
				|| (!baseMsg.isGroupMsg() && timedCache.get(baseMsg.getFromUserName()) != null)) {
			return;
		}

		if(baseMsg.isGroupMsg()) {//群聊
			//为了调试日志，在此记录
			LOG.info(String.format("收到群聊信息【%s】",botMsg.getBaseMsg().getText()));
			LOG.info(String.format("群聊名称是【%s】",botMsg.getBaseMsg().getGroupName()));
			if(!botConfig.getGroupWhiteList().contains(baseMsg.getGroupName())) {
				//如果群聊不在白名单
 				return;
			}
			
			if(!baseMsg.getContent().contains(botConfig.getAtBotName())) {
				//如果不是@我的消息
				return;
			}
//			TwoOnePointGame game = SpringUtil.getBean(TwoOnePointGame.class);
//			game.process(botMsg);
			long count = WehchatMsgQueue.countGroupUserPreMsg(baseMsg.getGroupUserName());
			if(count > 10) {
				timedCache.put(baseMsg.getGroupUserName(), baseMsg.getGroupUserName());
				botMsg.setReplyMsg(BotConst.AT + baseMsg.getGroupUserNickName() + " 你说话太快，接下来的10分钟我不会再处理你的新消息");
				WehchatMsgQueue.pushSendMsg(botMsg);
				return;
			}
			//实现我的回复逻辑
		//	CmdUtil.cmd(botMsg,"0");
		}else {//私聊
			
			if(!botConfig.getUserWhiteList().isEmpty()
					&& !botConfig.getUserWhiteList().contains(baseMsg.getFromUserNickName())) {

				return;
			}
			
//			long count = WehchatMsgQueue.countUserPreMsg(baseMsg.getFromUserName());
//			if(count > 10) {
//				timedCache.put(baseMsg.getFromUserName(), baseMsg.getFromUserName());
//				botMsg.setReplyMsg("你说话太快，接下来的10分钟我不会再处理你的新消息");
//				WehchatMsgQueue.pushSendMsg(botMsg);
//				return;
//			}
			//实现我的回复逻辑
//			CmdUtil.cmd(botMsg,"1");
		}
		
		
		if (baseMsg.getType().equals(MsgTypeEnum.TEXT.getType())) {
			
			baseMsg.setContent(baseMsg.getContent().replace(botConfig.getAtBotName(), ""));
			
			String content = baseMsg.getContent();
			if(StringUtils.isEmpty(content)) {
				//丢弃
				return;
			}
			
			boolean isSensitive = SensitiveWord.contains(content);
			if(isSensitive) {
				if(baseMsg.isGroupMsg()) {
					botMsg.setReplyMsg(BotConst.AT + baseMsg.getGroupUserNickName() + "你说的话太内涵，我无法回答。");
				}else {
					botMsg.setReplyMsg("你说的话太内涵，我无法回答。");
				}
				WehchatMsgQueue.pushSendMsg(botMsg);
				return;
			}


			WehchatMsgQueue.pushReplyMsg(botMsg);
		}else {
			botMsg.setReplyMsg("目前我只能针对文本消息进行回答");
			WehchatMsgQueue.pushSendMsg(botMsg);
		}
	}
}

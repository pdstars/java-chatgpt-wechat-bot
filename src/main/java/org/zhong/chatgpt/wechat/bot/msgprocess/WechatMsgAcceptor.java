package org.zhong.chatgpt.wechat.bot.msgprocess;

import cn.hutool.extra.spring.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;

import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;

public class WechatMsgAcceptor implements MsgAcceptor{
	
	private static Logger logger = LoggerFactory.getLogger(WechatMsgAcceptor.class);
	
	@Override
	public void start() {
		logger.info("消息接收线程启动");
		IMsgHandlerFace msgHandler = new WechatMsgHandler();
		BotConfig botConfig = SpringUtil.getBean(BotConfig.class);
		Wechat wechat = new Wechat(msgHandler, botConfig.getQrcodePath());
		wechat.start();
	}

}

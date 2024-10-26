package org.zhong.chatgpt.wechat.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@ComponentScan({"cn.zhouyafeng.itchat4j.controller","org.zhong.chatgpt.wechat.bot"})
public class ChatgptWechatBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatgptWechatBotApplication.class, args);
	}

}

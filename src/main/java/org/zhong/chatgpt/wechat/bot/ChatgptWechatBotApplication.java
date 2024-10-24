package org.zhong.chatgpt.wechat.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@ComponentScan(basePackages = "cn.zhouyafeng.itchat4j.controller")
public class ChatgptWechatBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatgptWechatBotApplication.class, args);
	}

}

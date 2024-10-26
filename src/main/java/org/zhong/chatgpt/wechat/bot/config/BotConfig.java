package org.zhong.chatgpt.wechat.bot.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.io.resource.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;
import org.zhong.chatgpt.wechat.bot.consts.BotConst;

import cn.hutool.core.io.file.FileReader;

@Configuration
public class BotConfig {
	@Value("${bot.botName}")
	private  String botName;

	private  String appKey = "";

	@Value("${bot.wechat.qrcode.path}")
	private  String qrcodePath;
	
	private  String proxyHost;
	
	private  int proxyPort = 0;
	
	private  Boolean proxyEnable = false;

	@Value("${bot.workspace}")
	private String   workspace;
	
	private  String dictPath = "classpath:dict.txt";
	
	private  List<String> groupWhiteList = new ArrayList<String>(); 
	
	private  List<String> userWhiteList = new ArrayList<String>(); 

	
	public  String getBotName() {
		return botName;
	}

	public  void setBotName(String botName) {
		this.botName = botName;
	}

	public  List<String> getGroupWhiteList() {
		if(groupWhiteList.size() == 0){
			String groupWhiteListUrl = ResourceUtil.getResource("groupWhiteList.txt").getPath();
			FileReader groupFileReader = new FileReader(groupWhiteListUrl);
			groupWhiteList = groupFileReader.readLines();
		}
		return groupWhiteList;
	}

	public  void setGroupWhiteList(List<String> groupWhiteList) {
		this.groupWhiteList = groupWhiteList;
	}

	public  String getAtBotName() {
		return BotConst.AT+botName;
	}


	public  String getAppKey() {
		return appKey;
	}

	public  void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public  List<String> getUserWhiteList() {
		return userWhiteList;
	}

	public  void setUserWhiteList(List<String> userWhiteList) {
		this.userWhiteList = userWhiteList;
	}

	public  String getQrcodePath() {
		return qrcodePath;
	}

	public void setQrcodePath(String qrcodePath) {
		this.qrcodePath = qrcodePath;
	}

	public  String getProxyHost() {
		return proxyHost;
	}

	public  void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public  int getProxyPort() {
		return proxyPort;
	}

	public  void setProxyPort(int proxyPost) {
		this.proxyPort = proxyPost;
	}

	public  Boolean getProxyEnable() {
		return proxyEnable;
	}

	public  void setProxyEnable(Boolean proxyEnable) {
		this.proxyEnable = proxyEnable;
	}

	public  String getDictPath() {
		return dictPath;
	}

	public  void setDictPath(String dictPath) {
		this.dictPath = dictPath;
	}

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}
}

package org.zhong.chatgpt.wechat.bot.config;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.io.resource.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
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
	private String workspace;
	
	private  String dictPath = "classpath:dict.txt";


	
	public  String getBotName() {
		return botName;
	}

	public  void setBotName(String botName) {
		this.botName = botName;
	}

	public  List<String> getGroupWhiteList() {
		InputStream in = null;
		List<String> groupWhiteList = new ArrayList<>();
		//获取配置文件路径
		try  {
			String configPath = workspace + "/config/groupWhiteList.txt";
			in = new FileInputStream(new File(configPath));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;

			while ((line = reader.readLine()) != null) {
				groupWhiteList.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try{
				in.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		return groupWhiteList;
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
		InputStream in = null;
		List<String> userWhiteList = new ArrayList<>();
		//获取配置文件路径
		try  {
			String configPath = workspace + "/config/userWhiteList.txt";
			in = new FileInputStream(new File(configPath));
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;

			while ((line = reader.readLine()) != null) {
				userWhiteList.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try{
				in.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		return userWhiteList;
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

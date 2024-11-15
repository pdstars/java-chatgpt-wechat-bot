package org.zhong.chatgpt.wechat.bot.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;
import org.zhong.chatgpt.wechat.bot.util.HttpClientUtils;
import org.zhong.chatgpt.wechat.bot.util.NewsProcessor;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MpWechatService {
    private String cookie;

    private String url = "https://mp.weixin.qq.com/cgi-bin/appmsgpublish?sub=list&search_field=null&begin=0&count=5&query=&fakeid=%s&type=101_1&free_publish_type=1&sub_action=list_ex&token=%s&lang=zh_CN&f=json&ajax=1";

    private BotConfig botConfig;
    private static Logger LOG = LoggerFactory.getLogger(MpWechatService.class);
    public MpWechatService(BotConfig botConfig){
        this.botConfig = botConfig;
    }


    public void saveMpText(String text) throws IOException {
        LocalDate today = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date = today.format(dateTimeFormatter);
        String filePath = botConfig.getWorkspace() + "/news/" + date + "/new.txt";
        File file = new File(filePath);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
            LOG.info("今日新闻保存成功");
        } else {
            LOG.info("今日新闻已保存");
            return;
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(text);
        writer.close();
    }

    /**
     * 解析公众号文章链接
     * @return
     */
    public String parseMpJson(JSONObject jsonObject){
        String link = jsonObject.getString("link");
        String htmlText = HttpClientUtils.get(link);
        Document doc = Jsoup.parse(htmlText);
        String text = doc.text();
        Pattern pattern = Pattern.compile("(?<=【每日资讯简报，一分钟知天下事】)([\\s\\S]*?)(?=【微语】)");
        Matcher matcher = pattern.matcher(text);
        String result = "";
        if(matcher.find()){
            result = matcher.group();
        }
        result = result.replaceAll("微信搜：每日资讯简报","");
        result = result.replaceAll(" 1、","\n1、");
        result = result.replaceAll("； ","； \n");
        return result;
    }
    /**
     * 获取最新公众号文章JSON
     * @return
     */
    public JSONObject getMpUrlJSON(){
        Properties properties = getUrlConfig();
        String url = String.format(this.url,properties.getProperty("wechat.mp.fakeid"),properties.getProperty("wechat.mp.token"));
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("cookie", properties.getProperty("wechat.mp.cookie"));
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject result = new JSONObject();
        try{
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode()==200){
                HttpEntity httpEntity = response.getEntity();
                String content = EntityUtils.toString(httpEntity);
                JSONObject contentJson = JSONObject.parseObject(content);
                JSONObject publish_page = JSONObject.parseObject(contentJson.getString("publish_page"));
                JSONArray publish_list = publish_page.getJSONArray("publish_list");
                result = JSONObject.parseObject(publish_list.getJSONObject(0).getString("publish_info"));
                result = result.getJSONArray("appmsgex").getJSONObject(0);
            }
            response.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    private Properties getUrlConfig(){
        String path = botConfig.getWorkspace() + "/config/config.properties";
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void main(String[] args) throws IOException {
        BotConfig botConfig1 = new BotConfig();
        botConfig1.setWorkspace("D:/botSpace");
        MpWechatService mpWechatService = new MpWechatService(botConfig1);
        JSONObject json =  mpWechatService.getMpUrlJSON();
        String text = mpWechatService.parseMpJson(json);
        mpWechatService.saveMpText(text);

    }

}

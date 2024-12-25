package org.zhong.chatgpt.wechat.bot.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TianQiProcess {
    private String url = "https://apis.tianapi.com/tianqi/index?key=%s&city=%s&type=1";
    @Value("${bot.apikey}")
    private String apikey;
    private static Logger LOG = LoggerFactory.getLogger(TianQiProcess.class);
    public String process(String city) {
        String res;
        res = HttpClientUtils.get(String.format(this.url,"0abfd5005885953a328d93d2355bbb77",city));
        JSONObject json = JSONObject.parseObject(res);
        StringBuilder sb = new StringBuilder();
        sb.append("%s %s %s %s \n");
        

        String result = "";
        if("200".equals(json.getString("code"))){
            JSONObject content = json.getJSONObject("result");
            result = content.getString("content");
        }else {
            LOG.error(String.format("天气获取失败【%s】",json.getString("msg")));
        }
        return result;
    }


}

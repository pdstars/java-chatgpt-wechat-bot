package org.zhong.chatgpt.wechat.bot.util;

import cn.hutool.log.Log;
import cn.zhouyafeng.itchat4j.core.MsgCenter;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TianGProcessor {
    private String url = "https://apis.tianapi.com/tiangou/index?key=%s";
    @Value("${bot.apikey}")
    private String apikey;

    private static Logger LOG = LoggerFactory.getLogger(TianGProcessor.class);
    public String process() {
        String res = HttpClientUtils.get(String.format(this.url,"0abfd5005885953a328d93d2355bbb77"));
        JSONObject json = JSONObject.parseObject(res);
        String result = "";
        if("200".equals(json.getString("code"))){
            JSONObject content = json.getJSONObject("result");
            result = content.getString("content");
        }else {
            LOG.error(String.format("舔狗日记获取失败【%s】",json.getString("msg")));
        }
        return result;
    }

    public static void main(String[] args) {
        TianGProcessor t = new TianGProcessor();
        t .process();
    }
}

package org.zhong.chatgpt.wechat.bot.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple PageProcessor.
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@Service
public class SougouImgProcessor {

    private String url = "https://pic.sogou.com/pics?mode=1&start=%s&xml_len=%s&query=%s";
    private SougouImgPipeline pipeline = new SougouImgPipeline();;
    private List<JSONObject> dataList = new ArrayList<>();;
    private List<Map<String,String>> urlList = new ArrayList<>();;
    private String word;


    public void process(int idx, int size,String word) {
        this.word = word;
        String res = HttpClientUtils.get(String.format(this.url, idx, size, this.word));
        JSONArray jsonArray =  getJsonByHTML(res);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            Map map = new HashMap();
            map.put("author_thumbUrl",item.getString("author_thumbUrl"));
            map.put("picUrl",item.getString("picUrl"));
            urlList.add(map);
        }
        System.out.println(urlList.size());
    }

    //解析html里的json
    public JSONArray getJsonByHTML(String html){
        String str = html.split("\"searchList\":")[2].split(",\"colorList\"")[0];
        JSONArray result = JSONArray.parseArray(str);
        return result;
    }

    // 下载
    public void pipelineData(){
        // 多线程
        pipeline.processSync(this.urlList, this.word);
    }


    public static void main(String[] args) {
        SougouImgProcessor processor = new SougouImgProcessor();
        int start = 0, size = 50, limit = 50; // 定义爬取开始索引、每次爬取数量、总共爬取数量
        for(int i=start;i<start+limit;i+=size)
            processor.process(i, size,"宠物");

        processor.pipelineData();

    }

}
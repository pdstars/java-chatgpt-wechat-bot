package org.zhong.chatgpt.wechat.bot.util;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViliImgPipeline {

    private String url = "https://www.vilipix.com/tags/%s/illusts";
   // private SougouImgPipeline pipeline = new SougouImgPipeline();;
    private List<JSONObject> dataList = new ArrayList<>();;
    public List<Map<String,String>> urlList = new ArrayList<>();;
    private String word;

    private String path = "D:/TEST";

    private volatile AtomicInteger suc;
    private volatile AtomicInteger fails;

    public ViliImgPipeline(){
        BotConfig botConfig = SpringUtil.getBean(BotConfig.class);
        this.path = botConfig.getWorkspace() + "/pipeline/viliImg";
        suc = new AtomicInteger();
        fails = new AtomicInteger();
    }
    public void process(String word) {
        this.word = word;
        if(StringUtils.isEmpty(word)){
            this.url = "https://www.vilipix.com/";
            this.word = "default";
        }

        String res = HttpClientUtils.get(String.format(this.url,this.word));
        try{
            getJsonByHTML(res);
        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println(urlList.size());
    }

    //解析html里的json
    public void getJsonByHTML(String html) throws UnsupportedEncodingException {
        String str = html.split("illusts:")[1].split("]")[0] + "]";
        String regex = "(?<=regular_url:\").*?(?=\",)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            Map map = new HashMap();
            String url = matcher.group();
            map.put("regular_url", UnicodeUtil.toString(url));
            urlList.add(map);
        }
    }

    // 下载
    /**
     * 多线程处理
     *
     */
    public void processSync() {
        long start = System.currentTimeMillis();
        int count = 0;
        ExecutorService executorService = Executors.newCachedThreadPool(); // 创建缓存线程池
        for (int i=0;i<this.urlList.size();i++) {
            String picUrl = this.urlList.get(i).get("regular_url");
            if (picUrl == null)
                continue;
            String name = "";
            if(i<10){
                name="000"+i;
            }else if(i<100){
                name="00"+i;
            }else if(i<1000){
                name="0"+i;
            }
            if(this.word.equals("default")){
                UUID uuid = UUID.randomUUID();
                name = uuid.toString();
            }

            String finalName = name;

            executorService.execute(() -> {
                try {
                    downloadImg(picUrl, word, finalName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            count++;
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                // 超时的时候向线程池中所有的线程发出中断(interrupted)。
                // executorService.shutdownNow();
            }
            System.out.println("AwaitTermination Finished");
            System.out.println("共有URL: "+this.urlList.size());
            System.out.println("下载成功: " + suc);
            System.out.println("下载失败: " + fails);

            File dir = new File(this.path + "/" + word + "/");
            int len = Objects.requireNonNull(dir.list()).length;
            System.out.println("当前共有文件： "+len);

            long end = System.currentTimeMillis();
            System.out.println("耗时：" + (end - start) / 1000.0 + "秒");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 下载
     * @param url
     * @param cate
     * @throws Exception
     */
    private void downloadImg(String url, String cate, String name) throws Exception {
        String path = this.path + "/" + cate + "/";
        File dir = new File(path);
        if (!dir.exists()) {    // 目录不存在则创建目录
            dir.mkdirs();
        }
        String realExt = url.substring(url.lastIndexOf("."));   // 获取扩展名
        if (!(realExt.equals(".jpg")  || realExt.equals(".png") || realExt.equals(".jpeg"))){
            realExt = ".jpg";
        }
        String fileName = name + realExt;
        fileName = fileName.replace("-", "");
        String filePath = path + fileName;
        File img = new File(filePath);
        if(img.exists()){   // 若文件之前已经下载过，则跳过
            System.out.println(String.format("文件%s已存在本地目录",fileName));
            return;
        }

        URLConnection con = new URL(url).openConnection();
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        InputStream inputStream = con.getInputStream();
        byte[] bs = new byte[1024];

        File file = new File(filePath);
        FileOutputStream os = new FileOutputStream(file, true);
        // 开始读取 写入
        int len;
        while ((len = inputStream.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        os.close();
        System.out.println("picUrl: " + url);
        System.out.println(String.format("正在下载第%s张图片", suc.getAndIncrement()));
    }
    public static void main(String[] args) {
        ViliImgPipeline processor = new ViliImgPipeline();
        processor.process("少女");

    }
}

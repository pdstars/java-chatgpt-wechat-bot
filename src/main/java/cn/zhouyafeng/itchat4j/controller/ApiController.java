package cn.zhouyafeng.itchat4j.controller;

import cn.zhouyafeng.itchat4j.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zhong.chatgpt.wechat.bot.model.Bot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api")
public class ApiController {
    @Value("${bot.wechat.qrcode.path}")
    private String qrCode;
    private static Core core = Core.getInstance();
    private static Logger LOG = LoggerFactory.getLogger(ApiController.class);
    @GetMapping("/getLoginQrCode")
    public ResponseEntity<byte[]> getQrCode() throws InterruptedException, IOException {
        if (core.isAlive()) { // 已登陆
            LOG.info("itchat4j已登陆");
            return null;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bot.buildChatGPTWechatBot().start();
            }
        }).start();

        File file = new File(qrCode + "/QR.jpg");
        // 检测登录qrcode是否生成，只检测10次
        for (int i = 0; i < 10; i++) {
            if(file.exists()){
                break;
            }
            Thread.sleep(1000);
            if(i == 9){
                return null;
            }
        }

        byte[] imageByte = Files.readAllBytes(file.toPath());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.IMAGE_JPEG);
        httpHeaders.setContentLength(imageByte.length);
        file.delete();
        return new ResponseEntity<>(imageByte,httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/getAlive")
    public boolean getAlive(){
        Core core = Core.getInstance();
        return core.isAlive();
    }
}

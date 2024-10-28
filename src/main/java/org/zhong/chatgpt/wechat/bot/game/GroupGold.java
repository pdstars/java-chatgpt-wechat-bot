package org.zhong.chatgpt.wechat.bot.game;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zhong.chatgpt.wechat.bot.config.BotConfig;

import java.io.File;
import java.io.IOException;

/**
 * 群组金币类
 */

@Service
public class GroupGold {

    @Autowired
    private BotConfig botConfig;

    /**
     * 获取当前群组的JSON
     * @param groupName
     * @return
     * @throws IOException
     */
    public JSONObject getGroupGold(String groupName) throws IOException {
        String path = botConfig.getWorkspace() + "/gold/" + groupName;
        File file = new File(path);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        String content = FileUtil.readString(file,"utf-8");
        return new JSONObject(content);
    }

    public void setGroupGold(String groupName,String content) throws IOException{
        String path = botConfig.getWorkspace() + "/gold/" + groupName;
        File file = new File(path);
        FileUtil.writeString(content,file,"utf-8");
    }

}

package org.zhong.chatgpt.wechat.bot.game;

import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import org.zhong.chatgpt.wechat.bot.model.BotMsg;

/**
 * 游戏接口
 */
public interface BaseGame {
    void startGame(BotMsg botMsg);

    void endGame(BaseMsg baseMsg);

    void process(BotMsg botMsg);
}

package org.zhong.chatgpt.wechat.bot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zhong.chatgpt.wechat.bot.entity.Idiom;

@Repository
public interface IdiomRepository extends JpaRepository<Idiom,Long> {
}

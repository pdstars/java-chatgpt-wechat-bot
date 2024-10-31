package org.zhong.chatgpt.wechat.bot.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "idiom")
public class Idiom {
    @Id
    private Long id;

    @Column
    private String derivation;

    @Column
    private String example;


    @Column
    private String explanation;

    @Column
    private String pinyin;

    @Column
    private String word;

    @Column
    private String abbreviation;

    @Column
    private String pinyin_r;
    @Column
    private String first;
    @Column
    private String last;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


}

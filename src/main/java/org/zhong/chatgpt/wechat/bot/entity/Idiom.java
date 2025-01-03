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

    @Column(name = "derivation")
    private String derivation;

    @Column(name = "example")
    private String example;


    @Column(name = "explanation")
    private String explanation;

    @Column(name = "pinyin")
    private String pinyin;

    @Column(name = "word")
    private String word;

    @Column(name = "abbreviation")
    private String abbreviation;

    @Column(name = "pinyin_r")
    private String pinyin_r;
    @Column(name = "first")
    private String first;
    @Column(name = "last")
    private String last;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getDerivation() {
        return derivation;
    }

    public void setDerivation(String derivation) {
        this.derivation = derivation;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getPinyin_r() {
        return pinyin_r;
    }

    public void setPinyin_r(String pinyin_r) {
        this.pinyin_r = pinyin_r;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}

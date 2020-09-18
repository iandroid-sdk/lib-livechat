package com.iandroid.allclass.lib_livechat.bean;

/**
 * created by wangkm
 * on 2020/9/18.
 */
public class ChatItem<T> {
    private String content;
    private long ts;
    private String from;
    private String pfid;
    private String subject;
    private int status;
    private String index;
    private int ugid;
    private int uglv;
    private T user_info = null;//用户详情信息，比如昵称、头像等

    public T getUser_info() {
        return user_info;
    }

    public void setUser_info(T user_info) {
        this.user_info = user_info;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getPfid() {
        return pfid;
    }

    public void setPfid(String pfid) {
        this.pfid = pfid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getUgid() {
        return ugid;
    }

    public void setUgid(int ugid) {
        this.ugid = ugid;
    }

    public int getUglv() {
        return uglv;
    }

    public void setUglv(int uglv) {
        this.uglv = uglv;
    }
}

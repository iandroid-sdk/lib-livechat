package com.iandroid.allclass.lib_livechat.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.iandroid.allclass.lib_livechat.utils.SocketUtils;

import androidx.annotation.Nullable;

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
    private int msgType = ChatMsgType.TEXT;
    //本地处理
    private int client_state;//发送状态
    private String sid;//发送消息的key

    protected ConversationContent real_content;

    public void parse() {
        try {
            if (SocketUtils.isJson(getContent())) {
                real_content = JSON.parseObject(getContent(), ConversationContent.class);
            }
        } catch (Exception e) {
            real_content = null;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj != null && obj instanceof ChatItem){
            ChatItem chatItem = (ChatItem)obj;
            if(!TextUtils.isEmpty(chatItem.getIndex())
                && !TextUtils.isEmpty(getIndex())){
                return getIndex().equals(chatItem.getIndex());
            }else if(!TextUtils.isEmpty(chatItem.getSid())
                    && !TextUtils.isEmpty(getSid())){
                return getSid().equals(chatItem.getSid());
            }else return false;
        }
        return super.equals(obj);
    }

    public ConversationContent getReal_content() {
        return real_content;
    }

    public void setReal_content(ConversationContent real_content) {
        this.real_content = real_content;
    }

    public int getClient_state() {
        return client_state;
    }

    public void setClient_state(int client_state) {
        this.client_state = client_state;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getMsgType() {
        return real_content == null ? msgType : real_content.getMsg_type();
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

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

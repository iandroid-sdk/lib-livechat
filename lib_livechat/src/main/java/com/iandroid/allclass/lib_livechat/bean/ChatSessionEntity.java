package com.iandroid.allclass.lib_livechat.bean;

import java.util.List;

/**
 * created by wangkm
 * on 2020/9/18.
 */
public class ChatSessionEntity {
    private String sid;
    private List<ChatItem> result;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public List<ChatItem> getResult() {
        return result;
    }

    public void setResult(List<ChatItem> result) {
        this.result = result;
    }
}

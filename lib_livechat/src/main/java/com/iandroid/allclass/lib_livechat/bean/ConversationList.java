package com.iandroid.allclass.lib_livechat.bean;

import java.util.List;

/**
 * created by wangkm
 * on 2020/8/18.
 */
public class ConversationList {
    private String sid;
    private List<ConversationItem> result;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public List<ConversationItem> getResult() {
        return result;
    }

    public void setResult(List<ConversationItem> result) {
        this.result = result;
    }
}

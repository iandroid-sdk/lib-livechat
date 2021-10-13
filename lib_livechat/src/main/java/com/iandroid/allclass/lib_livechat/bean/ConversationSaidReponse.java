package com.iandroid.allclass.lib_livechat.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * created by wangkm
 * on 2020/9/15.
 */
public class ConversationSaidReponse extends BaseResponse {
    private String index;
    private String content;
    private int unread;

    @JSONField(name = "createAt")
    private long ts;
    @JSONField(name = "from")
    private String pfid;

    @JSONField(name = "to")
    private String to_pfid;

    public String getTo_pfid() {
        return to_pfid;
    }

    public void setTo_pfid(String to_pfid) {
        this.to_pfid = to_pfid;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    //from 特殊情况，如果from是自己的话，需要使用to_pfid字段
    public String getPfid() {
        return pfid;
    }

    public void setPfid(String pfid) {
        this.pfid = pfid;
    }
}

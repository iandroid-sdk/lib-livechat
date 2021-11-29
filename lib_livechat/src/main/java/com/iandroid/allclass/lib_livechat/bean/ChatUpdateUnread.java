package com.iandroid.allclass.lib_livechat.bean;

/**
 * created by wangkm
 * on 2021/11/26.
 */
public class ChatUpdateUnread {
    public String pfid;
    public String index;
    public long ts;

    public String getPfid() {
        return pfid;
    }

    public void setPfid(String pfid) {
        this.pfid = pfid;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}

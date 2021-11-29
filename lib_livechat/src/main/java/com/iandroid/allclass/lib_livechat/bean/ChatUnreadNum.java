package com.iandroid.allclass.lib_livechat.bean;

/**
 * created by wangkm
 * on 2021/11/26.
 */
public class ChatUnreadNum {
    public String pfid;
    public String index;
    public int unread_num;

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

    public int getUnread_num() {
        return unread_num;
    }

    public void setUnread_num(int unread_num) {
        this.unread_num = unread_num;
    }
}

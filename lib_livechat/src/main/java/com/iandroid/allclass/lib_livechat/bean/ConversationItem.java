package com.iandroid.allclass.lib_livechat.bean;

import androidx.annotation.Nullable;

/**
 * created by wangkm
 * on 2020/8/18.
 */
public class ConversationItem {
    private String index;
    private String pfid;
    private long ts;
    private String content;
    private int unread;
    private int user_flag = 0;
    private Object user_info = null;//用户详情信息，比如昵称、头像等

    public Object getUser_info() {
        return user_info;
    }

    public void setUser_info(Object user_info) {
        this.user_info = user_info;
    }

    public int getUser_flag() {
        return user_flag;
    }

    public void setUser_flag(int user_flag) {
        this.user_flag = user_flag;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getPfid() {
        return pfid;
    }

    public void setPfid(String pfid) {
        this.pfid = pfid;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj != null && obj instanceof ConversationItem){
            ConversationItem conversationItem = (ConversationItem)obj;
            if(conversationItem.getPfid() != null && getPfid() != null){
                return getPfid().equals(conversationItem.getPfid());
            }
        }
        return super.equals(obj);
    }
}

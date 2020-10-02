package com.iandroid.allclass.lib_livechat.bean;

/**
 * created by wangkm
 * on 2020/9/24.
 */
public class ChatSayResponse extends BaseResponse{
    private long createAt;
    private String index;
    private String to;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}

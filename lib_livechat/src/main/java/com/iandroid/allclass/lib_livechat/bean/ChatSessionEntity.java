package com.iandroid.allclass.lib_livechat.bean;

import java.util.List;

/**
 * created by wangkm
 * on 2020/9/18.
 */
public class ChatSessionEntity extends BaseResponse {
    private List<ChatItem> result;

    public List<ChatItem> getResult() {
        return result;
    }

    public void setResult(List<ChatItem> result) {
        this.result = result;
    }
}

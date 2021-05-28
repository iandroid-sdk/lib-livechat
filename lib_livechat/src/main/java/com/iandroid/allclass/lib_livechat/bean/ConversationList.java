package com.iandroid.allclass.lib_livechat.bean;

import java.util.List;

/**
 * created by wangkm
 * on 2020/8/18.
 */
public class ConversationList extends BaseResponse {
    private List<ConversationItem> result;

    public List<ConversationItem> getResult() {
        return result;
    }

    public void setResult(List<ConversationItem> result) {
        this.result = result;
    }
}

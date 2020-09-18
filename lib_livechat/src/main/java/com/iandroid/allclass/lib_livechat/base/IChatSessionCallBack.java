package com.iandroid.allclass.lib_livechat.base;

import com.iandroid.allclass.lib_livechat.bean.ChatSessionEntity;

/**
 * created by wangkm
 * on 2020/9/14.
 * 状态机关键回调
 */
public interface IChatSessionCallBack {
    /**
     * 登出
     */
    public void chatListResponse(ChatSessionEntity chatSessionEntity);
}

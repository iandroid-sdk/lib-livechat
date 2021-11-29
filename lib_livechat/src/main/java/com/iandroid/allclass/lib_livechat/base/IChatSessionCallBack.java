package com.iandroid.allclass.lib_livechat.base;

import com.iandroid.allclass.lib_livechat.bean.ChatItem;
import com.iandroid.allclass.lib_livechat.bean.ChatSayResponse;
import com.iandroid.allclass.lib_livechat.bean.ChatSessionEntity;
import com.iandroid.allclass.lib_livechat.bean.ChatUpdateUnread;

/**
 * created by wangkm
 * on 2020/9/14.
 * 状态机关键回调
 */
public interface IChatSessionCallBack {
    /**
     * 聊天列表返回
     */
    public void onChatListResponse(ChatSessionEntity chatSessionEntity);


    /**
     * 发送消息返回
     */
    public void onChatSayResponse(ChatSayResponse chatSayResponse, ChatItem chatItem);

    /**
     * 收到聊天消息
     * @param chatItem
     */
    public void onSaid(ChatItem chatItem);

    /**
     * 更新已读信息
     */
    public void readUpdate(ChatUpdateUnread chatUpdateUnread);

}

package com.iandroid.allclass.lib_livechat.base;

import com.iandroid.allclass.lib_livechat.bean.ConversationItem;

/**
 * created by wangkm
 * on 2020/9/14.
 * 状态机关键回调
 */
public interface IStateKeyCallBack {
    /**
     * 私信回话加载完成
     */
    public void conversationLoadSuccess();

    /**
     * 收到聊天消息
     */
    public void onReceiveChat(ConversationItem conversationItem);


    /**
     * 未读消息更新
     * @param pfid,如果pfid为空，表示所有未读消息数，否则表示指定用户的未读消息数
     * @param num，未读消息数
     */
    public void updateUnreadMsgNum(String pfid, int num);

    /**
     * 删除会话
     * @param conversationItem
     */
    public void delConversation(ConversationItem conversationItem);
}

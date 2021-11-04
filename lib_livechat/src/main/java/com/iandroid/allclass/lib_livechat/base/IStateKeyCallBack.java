package com.iandroid.allclass.lib_livechat.base;

import com.iandroid.allclass.lib_livechat.bean.ConversationItem;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;

/**
 * created by wangkm
 * on 2020/9/14.
 * 状态机关键回调
 */
public interface IStateKeyCallBack {
    /**
     * 登出
     */
    public void logout();
    /**
     * 私信回话加载完成
     */
    public void conversationLoadSuccess();

    /**
     * 收到聊天消息
     */
    public void onReceiveChat(ConversationItem conversationItem, boolean isFromOtherSaid);


    /**
     * 未读消息更新
     * @param conversationItem,如果为空，表示所有未读消息数，否则表示指定用户的未读消息数
     * @param num，未读消息数
     */
    public void updateUnreadMsgNum(ConversationItem conversationItem, int num);

    /**
     * 删除会话
     * @param conversationItem
     */
    public void delConversation(ConversationItem conversationItem);

    /**
     * 查询用户信息
     * @param pifd
     */
    public void queryUserInfo(String pifd);

    /**
     * 状态机状态回调
     * @param status
     */
    public void statusCallback(SocketEvent.enmSocketStatus status);

    /**
     * 提出登录
     */
    public void tickOut();

    /**
     * 自定义协议
     */
    public void onReceiveMsg(String event, Object[] originalData, Object eventData);
}

package com.iandroid.allclass.lib_livechat.conversation;

import android.text.TextUtils;

import com.iandroid.allclass.lib_livechat.api.StateChat;
import com.iandroid.allclass.lib_livechat.base.IChatSessionCallBack;
import com.iandroid.allclass.lib_livechat.bean.ChatItem;
import com.iandroid.allclass.lib_livechat.bean.ChatSayResponse;
import com.iandroid.allclass.lib_livechat.bean.ChatSessionEntity;
import com.iandroid.allclass.lib_livechat.bean.ConversationSaidReponse;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;
import com.iandroid.allclass.lib_livechat.utils.SocketUtils;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * created by wangkm
 * on 2020/9/18.
 */
public class ChatSession {
    private String pfid;
    private IChatSessionCallBack iChatSessionCallBack;
    private Map<String, ChatItem> sendingMsg;

    public ChatSession(String pfid, IChatSessionCallBack iChatSessionCallBack) {
        this.pfid = pfid;
        this.iChatSessionCallBack = iChatSessionCallBack;
        this.sendingMsg = new LinkedHashMap<>();
        StateChat.getInstance().addChatSession(pfid, this);
    }

    public void clear() {
        StateChat.getInstance().removeChatSession(pfid);
        pfid = null;
        iChatSessionCallBack = null;
        this.sendingMsg.clear();
    }

    public boolean fetchChatList(String index) {
        String transactionId = SocketUtils.transactionId(SocketEvent.IM_HEAD_SINGLE_LIST);
        return StateChat.getInstance().send(SocketEvent.EVENT_C2S_SINGLELIST,
                genChatMsgList(pfid, transactionId, index, -1, SocketEvent.PAGE_SIZE));
    }

    //发送消息给别人
    public boolean sayTo(ChatItem chatItem) {
        chatItem.setSid(SocketUtils.transactionId(chatItem.getSubject()));
        sendingMsg.put(chatItem.getSid(), chatItem);
        return StateChat.getInstance().send(SocketEvent.EVENT_C2S_SAY,
                genImSay(chatItem.getSubject(), chatItem.getContent(), chatItem.getSid()));
    }

    //收到消息
    public void onSaid(ConversationSaidReponse conversationSaidReponse) {
        conversationSaidReponse.setUnread(0);
        if (iChatSessionCallBack != null) {
            ChatItem chatItem = new ChatItem();
            chatItem.setIndex(conversationSaidReponse.getIndex());
            chatItem.setSubject(conversationSaidReponse.getPfid());
            chatItem.setUser_info(ConversationManager.getInstance().getUserInfo(conversationSaidReponse.getPfid()));
            chatItem.setContent(conversationSaidReponse.getContent());
            chatItem.setTs(conversationSaidReponse.getTs());
            chatItem.parse();
            iChatSessionCallBack.onSaid(chatItem);
        }
    }

    /**
     * 删除某条消息
     * @param chatItem
     * @return
     */
    public boolean delChat(ChatItem chatItem) {
        String transcationId = SocketUtils.transactionId(SocketEvent.IM_HEAD_DEL);
        return StateChat.getInstance().send(SocketEvent.EVENT_C2S_DELONE,
                genDelSingle(chatItem.getIndex(), transcationId));
    }

    public ChatItem chatSayResponse(ChatSayResponse chatSayResponse) {
        if (sendingMsg == null || chatSayResponse == null || !sendingMsg.containsKey(chatSayResponse.getSid())) {
            return null;
        }

        ChatItem chatItem = sendingMsg.remove(chatSayResponse.getSid());
        if (iChatSessionCallBack != null)
            iChatSessionCallBack.chatSayResponse(chatSayResponse, chatItem);

        return chatItem;
    }

    public void chatListResponse(ChatSessionEntity chatSessionEntity) {
        if (iChatSessionCallBack != null) iChatSessionCallBack.chatListResponse(chatSessionEntity);
    }

    private JSONObject genChatMsgList(String contact_pfid,
                                      String sid,
                                      String last_index,
                                      int direction,
                                      int count) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("contact_pfid", contact_pfid);
            obj.put("sid", sid);
            if (!TextUtils.isEmpty(last_index))
                obj.put("last_index", last_index);
            obj.put("direction", direction);
            obj.put("count", count);
        } catch (Exception e) {
            e.printStackTrace();
            obj = null;
        }
        return obj;
    }

    public JSONObject genImSay(String to, String c, String sid) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("to", to);
            obj.put("c", c);
            obj.put("sid", sid);
        } catch (Exception e) {
            e.printStackTrace();
            obj = null;
        }
        return obj;
    }

    public static JSONObject genDelSingle(String index, String sid) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("index", index);
            obj.put("sid", sid);
        } catch (Exception e) {
            e.printStackTrace();
            obj = null;
        }
        return obj;
    }
}

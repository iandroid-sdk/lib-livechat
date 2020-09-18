package com.iandroid.allclass.lib_livechat.conversation;

import android.text.TextUtils;

import com.iandroid.allclass.lib_livechat.api.StateChat;
import com.iandroid.allclass.lib_livechat.base.IChatSessionCallBack;
import com.iandroid.allclass.lib_livechat.bean.ChatSessionEntity;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;
import com.iandroid.allclass.lib_livechat.utils.SocketUtils;

import org.json.JSONObject;

/**
 * created by wangkm
 * on 2020/9/18.
 */
public class ChatSession {
    private String pfid;
    private IChatSessionCallBack iChatSessionCallBack;

    public ChatSession(String pfid, IChatSessionCallBack iChatSessionCallBack) {
        this.pfid = pfid;
        this.iChatSessionCallBack = iChatSessionCallBack;
        StateChat.getInstance().addChatSession(pfid, this);
    }

    public void clear() {
        StateChat.getInstance().removeChatSession(pfid);
        pfid = null;
        iChatSessionCallBack = null;
    }

    public boolean fetchChatList(String index) {
        String transactionId = SocketUtils.transactionId(SocketEvent.IM_HEAD_SINGLE_LIST);
        return StateChat.getInstance().send(SocketEvent.EVENT_C2S_SINGLELIST,
                genChatMsgList(pfid, transactionId, index, -1, SocketEvent.PAGE_SIZE));
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

}

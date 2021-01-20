package com.iandroid.allclass.lib_livechat.api;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.iandroid.allclass.lib_livechat.base.ChatManager;
import com.iandroid.allclass.lib_livechat.base.ISocketEventHandler;
import com.iandroid.allclass.lib_livechat.base.IStateKeyCallBack;
import com.iandroid.allclass.lib_livechat.base.StateChatPresenter;
import com.iandroid.allclass.lib_livechat.bean.ChatItem;
import com.iandroid.allclass.lib_livechat.bean.ChatSayResponse;
import com.iandroid.allclass.lib_livechat.bean.ChatSessionEntity;
import com.iandroid.allclass.lib_livechat.bean.ConversationSaidReponse;
import com.iandroid.allclass.lib_livechat.conversation.ChatSession;
import com.iandroid.allclass.lib_livechat.conversation.ConversationManager;
import com.iandroid.allclass.lib_livechat.exception.LoginException;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.Socket;

/**
 * created by wangkm
 * on 2020/8/18.
 * 状态机---私信，粉丝团，全局状态控制等
 */
public class StateChat implements ISocketEventHandler {
    private StateChatPresenter stateChatPresenter;
    private Config roomConfig;//当前所在直播间的配置信息
    private IStateKeyCallBack iStateKeyCallBack;
    private Map<String, WeakReference<ChatSession>> chatSessionMap;
    /**
     * 登录
     *
     * @param config
     * @throws LoginException
     */
    public static void login(Config config) throws LoginException {
        getInstance().iStateKeyCallBack = config.stateKeyCallBack();

        getStateChatPresenter().login(config);
    }

    /**
     * 登出
     */
    public static void logout() {
        ConversationManager.getInstance().clearData();
        stateChange(SocketEvent.enmUserState.enmExit, SocketEvent.enmStateAction.enmActionNull, null);
        getStateChatPresenter().logout();
        if (getInstance().iStateKeyCallBack != null)
            getInstance().iStateKeyCallBack.logout();
    }

    @Override
    public Object onMsgParse(String event, Object[] original) {
        Object eventData = null;
        switch (event) {
            case SocketEvent.EVENT_PRIVATECHAT_SAID:
                eventData = JSON.parseObject(original[0].toString(), ConversationSaidReponse.class);
                break;
            case SocketEvent.EVENT_C2S_SINGLELIST:
                eventData = JSON.parseObject(original[0].toString(), ChatSessionEntity.class);
                break;
            case SocketEvent.EVENT_C2S_SAY:
                eventData = JSON.parseObject(original[0].toString(), ChatSayResponse.class);
                break;
        }
        return eventData;
    }

    @Override
    public void onReceiveMsg(String event, Object[] originalData, Object eventData) {
        switch (event) {
            case SocketEvent.EVENT_PRIVATECHAT_SAID:
                if (eventData != null && eventData instanceof ConversationSaidReponse) {
                    ConversationSaidReponse conversationSaidReponse = (ConversationSaidReponse) eventData;
                    onSaid(conversationSaidReponse);
                    ConversationManager.getInstance().updateConversationOnSaid(conversationSaidReponse, iStateKeyCallBack);
                }
                break;
            case SocketEvent.EVENT_C2S_SINGLELIST:
                if (eventData != null && eventData instanceof ChatSessionEntity) {
                    chatListResponse((ChatSessionEntity) eventData);
                }
                break;
            case SocketEvent.EVENT_C2S_SAY:
                if (eventData != null && eventData instanceof ChatSayResponse) {
                    chatSayResponse((ChatSayResponse) eventData);
                }
                break;
            case Socket.EVENT_CONNECT:
                if (iStateKeyCallBack != null)
                    iStateKeyCallBack.statusCallback(SocketEvent.enmSocketStatus.enmConnected);
                break;
            case Socket.EVENT_RECONNECT_ERROR:
                if (iStateKeyCallBack != null)
                    iStateKeyCallBack.statusCallback(SocketEvent.enmSocketStatus.enmConnectError);
                break;
            case SocketEvent.EVENT_AUTHENTICATED:
                if (iStateKeyCallBack != null)
                    iStateKeyCallBack.statusCallback(SocketEvent.enmSocketStatus.enmAuthSuccess);
                break;
            case SocketEvent.EVENT_UNAUTHENTICATED:
                if (iStateKeyCallBack != null)
                    iStateKeyCallBack.statusCallback(SocketEvent.enmSocketStatus.enmAuthFailed);
                break;
        }
    }

    public void conversationLoadSuccess() {
        if (iStateKeyCallBack != null) {
            iStateKeyCallBack.conversationLoadSuccess();
            iStateKeyCallBack.updateUnreadMsgNum(null, ConversationManager.getInstance().getTotalUnreadMsgNum());
        }
    }
    /**
     * 缓存当前进入的直播间信息
     *
     * @param roomConfig
     */
    public static void setRoomConfig(Config roomConfig) {
        getInstance().roomConfig = roomConfig;
    }

    public void fetchAllConversation() {
        if (stateChatPresenter != null)
            stateChatPresenter.fetchAllConversation();
    }

    /**
     * 全局状态改变：登录app，进入直播间，退出直播间等
     */
    public static void stateChange(SocketEvent.enmUserState state,
                                   SocketEvent.enmStateAction action,
                                   Config config) {
        Config curRoomConfig = (config == null ? getInstance().roomConfig : config);

        switch (state) {
            case enmInApp://状态机登录或者退出直播间
                if (action == SocketEvent.enmStateAction.enmActionNull) {
                    //先更新自己平台状态
                    getStateChatPresenter().stateChange(state, action, null, null);
                    if (curRoomConfig != null) {
                        //如果当前有直播间信息，更新自己在直播间的状态
                        getStateChatPresenter().stateChange(Config.isMy(curRoomConfig) ? SocketEvent.enmUserState.enmAnchor : SocketEvent.enmUserState.enmAudience,
                                Config.isMy(curRoomConfig) ? SocketEvent.enmStateAction.enmActionStreaming : SocketEvent.enmStateAction.enmActionNull,
                                curRoomConfig.pfid(),
                                curRoomConfig);
                    }
                } else if (action == SocketEvent.enmStateAction.enmActionLeaveRoom
                        && curRoomConfig != null) {
                    getStateChatPresenter().stateChange(state, action, curRoomConfig.pfid(), curRoomConfig);
                }
                break;
            case enmExit:
                getStateChatPresenter().stateChange(state, action, null, null);
                break;
            case enmAnchor:
            case enmAudience:
                if (curRoomConfig != null)
                    getStateChatPresenter().stateChange(state, action, curRoomConfig.pfid(), curRoomConfig);
                break;
            case enmReloginRoom:
                getStateChatPresenter().stateReloginRoom();
                break;
        }
    }

    public boolean send(String event, final Object... args) {
        return getStateChatPresenter().send(event, args);
    }

    private static class LazyHolder {
        private static final StateChat sInstance = new StateChat();
    }

    public static StateChat getInstance() {
        return LazyHolder.sInstance;
    }

    public StateChat() {
        stateChatPresenter = ChatManager.getStateChat(this);
    }

    public static IStateKeyCallBack getiStateKeyCallBack() {
        return getInstance().iStateKeyCallBack;
    }

    private static StateChatPresenter getStateChatPresenter() {
        return getInstance().stateChatPresenter;
    }

    public void addChatSession(String pfid, ChatSession chatSession) {
        if (chatSessionMap == null) chatSessionMap = new HashMap<>();
        chatSessionMap.put(pfid, new WeakReference<>(chatSession));
    }

    public void removeChatSession(String pfid) {
        if (chatSessionMap != null
                && chatSessionMap.containsKey(pfid)
                && !TextUtils.isEmpty(pfid))
            chatSessionMap.remove(pfid);
    }

    //获取聊天消息列表响应
    public void chatListResponse(ChatSessionEntity chatSessionEntity) {
        if (chatSessionMap == null) return;

        String pfid = chatSessionEntity.getSubject();
        if (!TextUtils.isEmpty(pfid) && chatSessionMap.containsKey(pfid)) {
            WeakReference<ChatSession> chatSessionWeakReference = chatSessionMap.get(pfid);
            if (chatSessionWeakReference == null || chatSessionWeakReference.get() == null) return;
            chatSessionWeakReference.get().onChatListResponse(chatSessionEntity);
        }
    }

    //收到消息
    public void onSaid(ConversationSaidReponse conversationSaidReponse) {
        if (chatSessionMap == null) return;

        String pfid = conversationSaidReponse.getPfid();
        if (!TextUtils.isEmpty(pfid) && chatSessionMap.containsKey(pfid)) {
            WeakReference<ChatSession> chatSessionWeakReference = chatSessionMap.get(pfid);
            if (chatSessionWeakReference == null || chatSessionWeakReference.get() == null) return;
            chatSessionWeakReference.get().onSaid(conversationSaidReponse);
        }
    }

    //发送消息返回响应
    public void chatSayResponse(ChatSayResponse chatSayResponse) {
        if (chatSessionMap == null) return;

        ChatItem chatItem = null;
        String toPfid = chatSayResponse.getTo();
        if (!TextUtils.isEmpty(toPfid) && chatSessionMap.containsKey(toPfid)) {
            WeakReference<ChatSession> chatSessionWeakReference = chatSessionMap.get(toPfid);
            if (chatSessionWeakReference == null || chatSessionWeakReference.get() == null) return;
            chatItem = chatSessionWeakReference.get().onChatSayResponse(chatSayResponse);
        }

        //更新会话
        if (!TextUtils.isEmpty(toPfid)
                && chatSayResponse.getRet_code() == SocketEvent.CODE_OK
                && chatItem != null) {
            if (chatSayResponse.getRet_code() == SocketEvent.CODE_OK) {
                ConversationSaidReponse conversationSaidReponse = new ConversationSaidReponse();
                conversationSaidReponse.setIndex(chatSayResponse.getIndex());
                conversationSaidReponse.setPfid(toPfid);
                conversationSaidReponse.setTs(chatSayResponse.getCreateAt());
                conversationSaidReponse.setContent(chatItem.getContent());
                ConversationManager.getInstance().updateConversationOnSaid(conversationSaidReponse, iStateKeyCallBack);
            }
        }
    }
}

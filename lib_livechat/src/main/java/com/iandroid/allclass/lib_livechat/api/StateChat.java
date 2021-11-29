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
import com.iandroid.allclass.lib_livechat.bean.ChatUnreadNum;
import com.iandroid.allclass.lib_livechat.bean.ChatUpdateUnread;
import com.iandroid.allclass.lib_livechat.bean.ConversationSaidReponse;
import com.iandroid.allclass.lib_livechat.conversation.ChatSession;
import com.iandroid.allclass.lib_livechat.conversation.ConversationManager;
import com.iandroid.allclass.lib_livechat.exception.LoginException;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

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

    public void updateUserToken(String token) {
        UserInfo.userToken = token;
    }
    private Config curStatueConfig;

    /**
     * 登录
     *
     * @param config
     * @throws LoginException
     */
    public static void login(Config config) throws LoginException {
        getInstance().iStateKeyCallBack = config.stateKeyCallBack();
        getInstance().curStatueConfig = config;

        getStateChatPresenter().login(config);
    }

    public static Boolean isMy(String userId) {
        if (getInstance().curStatueConfig == null
                || getInstance().curStatueConfig.selfPfid() == null
                || TextUtils.isEmpty(userId)) return false;

        return TextUtils.equals(getInstance().curStatueConfig.selfPfid(), userId);
    }

    /**
     * 登出
     */
    public static void logout() {
        UserInfo.userToken = null;
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
            case SocketEvent.EVENT_UPDATE_UNREAD:
                eventData = JSON.parseObject(original[0].toString(), ChatUpdateUnread.class);
                break;
            case SocketEvent.EVENT_READ_UPDATE:
                eventData = JSON.parseObject(original[0].toString(), ChatUnreadNum.class);
                break;
            case SocketEvent.EVENT_CMD: {
                try {
                    JSONObject json = new JSONObject(original[0].toString());
                    eventData = json.optString("cmd");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
                    ConversationManager.getInstance().updateConversationOnSaid(conversationSaidReponse,
                            iStateKeyCallBack,
                            StateChat.isMy(conversationSaidReponse.getTo_pfid()));
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
            case SocketEvent.EVENT_CMD:
                if (eventData != null
                        && eventData instanceof String
                        && eventData.toString().equalsIgnoreCase(SocketEvent.CMD_REVOKE)
                        && iStateKeyCallBack != null)
                    iStateKeyCallBack.tickOut();
                break;
            case SocketEvent.EVENT_UPDATE_UNREAD:
                if (eventData != null && eventData instanceof ChatUpdateUnread) {
                    ChatUpdateUnread chatUpdateUnread = (ChatUpdateUnread)eventData;
                    if (!TextUtils.isEmpty(chatUpdateUnread.pfid)
                            && chatSessionMap.containsKey(chatUpdateUnread.pfid)) {
                        WeakReference<ChatSession> chatSessionWeakReference = chatSessionMap.get(chatUpdateUnread.pfid);
                        if (chatSessionWeakReference == null || chatSessionWeakReference.get() == null) return;
                        chatSessionWeakReference.get().readUpdate(chatUpdateUnread);
                    }
                }
                break;
            case SocketEvent.EVENT_READ_UPDATE:
                if (eventData != null && eventData instanceof ChatUnreadNum) {
                    updateUnreadNum((ChatUnreadNum)eventData);
                }
                break;
            default:
                if (iStateKeyCallBack != null)
                    iStateKeyCallBack.onReceiveMsg(event, originalData, eventData);
                break;
        }
    }

    @Override
    public void statusCallback(SocketEvent.enmSocketStatus status) {
        if (iStateKeyCallBack != null)
            iStateKeyCallBack.statusCallback(status);
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

    public static void appBringToFront() {
        if (getInstance().stateChatPresenter != null
                && getInstance().stateChatPresenter.isConnected())
            stateChange(SocketEvent.enmUserState.enmAppFront, null, null);
    }

    public static void appBringToBack() {
        if (getInstance().stateChatPresenter != null
                && getInstance().stateChatPresenter.isConnected())
            stateChange(SocketEvent.enmUserState.enmAppBackground, null, null);
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
                if (curRoomConfig != null) {
                    StateChatPresenter.lastStateRoomChangeMsg =
                            getStateChatPresenter().stateChange(state, action, curRoomConfig.pfid(), curRoomConfig);
                }
                break;
            case enmReloginRoom:
                getStateChatPresenter().stateReloginRoom();
                break;
            case enmFromFloatView:
                getStateChatPresenter().stateChangeFromFloatView(Config.isMy(curRoomConfig) ? SocketEvent.enmUserState.enmAnchor : SocketEvent.enmUserState.enmAudience,
                        Config.isMy(curRoomConfig) ? SocketEvent.enmStateAction.enmActionStreaming : SocketEvent.enmStateAction.enmActionNull, curRoomConfig);
                break;
            case enmAppBackground:
                getStateChatPresenter().stateChange(curRoomConfig == null ? SocketEvent.enmUserState.enmInApp : (Config.isMy(curRoomConfig) ? SocketEvent.enmUserState.enmAnchor : SocketEvent.enmUserState.enmAudience),
                        SocketEvent.enmStateAction.enmActionAppBackground);
                break;
            case enmAppFront:
                getStateChatPresenter().stateChange(curRoomConfig == null ? SocketEvent.enmUserState.enmInApp : (Config.isMy(curRoomConfig) ? SocketEvent.enmUserState.enmAnchor : SocketEvent.enmUserState.enmAudience),
                        SocketEvent.enmStateAction.enmActionAppFront);
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

        String sessionUserId = getSessionPfidFromSaidMsg(conversationSaidReponse);
        if (!TextUtils.isEmpty(sessionUserId)
                && (chatSessionMap.containsKey(sessionUserId))) {
            WeakReference<ChatSession> chatSessionWeakReference = chatSessionMap.get(sessionUserId);
            if (chatSessionWeakReference == null || chatSessionWeakReference.get() == null) return;
            chatSessionWeakReference.get().onSaid(conversationSaidReponse);
        }
    }

    private String getSessionPfidFromSaidMsg(ConversationSaidReponse conversationSaidReponse) {
        String sessionUserId = conversationSaidReponse.getPfid();
        if (StateChat.isMy(sessionUserId)
                && !TextUtils.isEmpty(conversationSaidReponse.getTo_pfid())
                && chatSessionMap.containsKey(conversationSaidReponse.getTo_pfid())){
            sessionUserId = conversationSaidReponse.getTo_pfid();
        }
        return sessionUserId;
    }

    public void updateUnreadNum(ChatUnreadNum chatUnreadNum){
        ConversationManager.getInstance().updateUnreadNum(chatUnreadNum, iStateKeyCallBack);
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
                ConversationManager.getInstance().updateConversationOnSaid(conversationSaidReponse,
                        iStateKeyCallBack,
                        false);
            }
        }
    }
}

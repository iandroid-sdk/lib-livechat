package com.iandroid.allclass.lib_livechat.api;

import com.alibaba.fastjson.JSON;
import com.iandroid.allclass.lib_livechat.base.ChatManager;
import com.iandroid.allclass.lib_livechat.base.ISocketEventHandler;
import com.iandroid.allclass.lib_livechat.base.IStateKeyCallBack;
import com.iandroid.allclass.lib_livechat.base.StateChatPresenter;
import com.iandroid.allclass.lib_livechat.bean.ConversationSaidReponse;
import com.iandroid.allclass.lib_livechat.conversation.ConversationManager;
import com.iandroid.allclass.lib_livechat.exception.LoginException;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;

/**
 * created by wangkm
 * on 2020/8/18.
 * 状态机---私信，粉丝团，全局状态控制等
 */
public class StateChat implements ISocketEventHandler {
    private StateChatPresenter stateChatPresenter;
    private Config roomConfig;//当前所在直播间的配置信息
    private ISocketEventHandler iSocketEventHandler;
    private IStateKeyCallBack iStateKeyCallBack;
    /**
     * 登录
     *
     * @param config
     * @throws LoginException
     */
    public static void login(Config config) throws LoginException {
        getInstance().iSocketEventHandler = config.socketEventHandler();
        getInstance().iStateKeyCallBack = config.stateKeyCallBack();

        getStateChatPresenter().login(config);
    }

    /**
     * 登出
     */
    public static void logout() {
        stateChange(SocketEvent.enmUserState.enmExit, SocketEvent.enmStateAction.enmActionNull, null);
        getStateChatPresenter().logout();
    }

    @Override
    public Object onMsgParse(String event, Object[] original) {
        Object eventData = null;
        switch (event) {
            case SocketEvent.EVENT_PRIVATECHAT_SAID:
                eventData = JSON.parseObject(original[0].toString(), ConversationSaidReponse.class);
                break;
        }
        return eventData;
    }

    @Override
    public void onReceiveMsg(String event, Object[] originalData, Object eventData) {
        switch (event) {
            case SocketEvent.EVENT_PRIVATECHAT_SAID:
                if (eventData != null && eventData instanceof ConversationSaidReponse)
                    ConversationManager.getInstance().updateConversationOnSaid((ConversationSaidReponse) eventData, iStateKeyCallBack);
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
}

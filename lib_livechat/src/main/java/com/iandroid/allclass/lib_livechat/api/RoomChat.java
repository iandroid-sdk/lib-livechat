package com.iandroid.allclass.lib_livechat.api;

import android.util.Log;

import com.iandroid.allclass.lib_livechat.base.ChatManager;
import com.iandroid.allclass.lib_livechat.base.RoomChatPresenter;
import com.iandroid.allclass.lib_livechat.base.StateChatPresenter;
import com.iandroid.allclass.lib_livechat.exception.LoginException;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;

import org.json.JSONObject;

import java.util.List;


/**
 * created by wangkm
 * on 2020/8/19.
 * 封装房间聊天室逻辑
 */
public class RoomChat {
    private RoomChatPresenter chatRoom;
    private RoomChatPresenter adminRoom;
    private Config roomConfig;

    /**
     * 登录直播间
     *
     * @param config
     */
    public void loginRoom(Config config) {
        this.roomConfig = config;
        if (chatRoom == null) chatRoom = ChatManager.getRoomChat(config.socketEventHandler());
        if (adminRoom == null) adminRoom = ChatManager.getRoomChat(config.socketEventHandler());
        try {
            chatRoom.login(getLoginConfig(config, config.cht_server_list(), true));
            adminRoom.login(getLoginConfig(config, config.ctl_server_list(), false));
        } catch (LoginException e) {
            e.printStackTrace();
            Log.e(roomConfig.tag(), "loginRoom:" + e.getMessage());
        }
    }

    /**
     * 登出直播间
     */
    public void logoutRoom() {
        StateChatPresenter.lastStateRoomChangeMsg = null;
        StateChat.stateChange(SocketEvent.enmUserState.enmInApp, SocketEvent.enmStateAction.enmActionLeaveRoom, roomConfig);
        this.roomConfig = null;
        if (chatRoom != null) chatRoom.logout();
        if (adminRoom != null) adminRoom.logout();
    }

    /**
     * 主播离开
     */
    public void anchorLeave() {
        StateChat.stateChange(SocketEvent.enmUserState.enmAnchor, SocketEvent.enmStateAction.enmActionAnchorPause, roomConfig);
    }

    /***
     * 主播回来
     */
    public void anchorBack() {
        StateChat.stateChange(SocketEvent.enmUserState.enmAnchor, SocketEvent.enmStateAction.enmActionAnchorBack, roomConfig);
    }

    /**
     * 发送消息
     *
     * @param data
     * @return
     */
    public boolean sendToRoomSay(JSONObject data) {
        if (chatRoom == null || !chatRoom.isConnected()) {
            loginRoomRetry();
            return false;
        } else return chatRoom.send(SocketEvent.EVENT_C2S_CHAT_SAY, data);
    }

    /**
     * 发送消息
     *
     * @param event
     * @param args
     * @return
     */
    public boolean sendToAdminChat(final String event, final Object... args) {
        if (adminRoom == null || !adminRoom.isConnected()) {
            loginRoomRetry();
            return false;
        } else return adminRoom.send(event, args);
    }

    /**
     * 登录重试
     */
    private void loginRoomRetry() {
        if (roomConfig != null) {
            loginRoom(roomConfig);
        }
    }

    /**
     * 将直播间聊天室的url统一转化成带host的config，其他信息保持不变
     *
     * @param config
     * @param hostlist
     * @return
     */
    private Config getLoginConfig(Config config, List<String> hostlist, boolean isRoomChatConnection) {
        return Config.builder()
                .hosts(hostlist)
                .selfPfid(config.selfPfid())
                .userType(config.userType())
                .platform(config.platform())
                .event_list(config.event_list())
                .from(config.from())
                .socketEventHandler(config.socketEventHandler())
                .isRoomChatConnection(isRoomChatConnection)
                .reconnectionDelay(config.reconnectionDelay())
                .reconnectionDelayMax(config.reconnectionDelayMax())
                .reconnectionAttempts(config.reconnectionAttempts())
                .liveId(config.liveId())
                .liveKey(config.liveKey())
                .synToState(config.synToState())
                .name(config.name())
                .fromSeq(config.fromSeq())
                .channelId(config.channelId())
                .area(config.area())
                .pfid(config.pfid())
                .token(config.token())
                .tag(config.tag())
                .appVer(config.appVer())
                .build();
    }
}

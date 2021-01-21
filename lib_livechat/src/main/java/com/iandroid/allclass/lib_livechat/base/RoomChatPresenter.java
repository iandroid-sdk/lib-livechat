package com.iandroid.allclass.lib_livechat.base;

import android.text.TextUtils;
import android.util.Log;

import com.iandroid.allclass.lib_livechat.api.Config;
import com.iandroid.allclass.lib_livechat.api.StateChat;
import com.iandroid.allclass.lib_livechat.api.UserInfo;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * created by wangkm
 * on 2020/8/6.
 * <p>
 * 聊天室
 */
public class RoomChatPresenter extends ChatManager {

    public RoomChatPresenter(ISocketEventHandler iSocketEventHandler) {
        super(iSocketEventHandler);
        socket_event_list.add(SocketEvent.EVENT_JOIN);
    }

    @Override
    public void auth() {
        send(SocketEvent.EVENT_C2S_AUTHENTICATION, getAuthCode());
    }

    @Override
    public Object getAuthCode() {
        JSONObject obj = new JSONObject();
        try {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("pfid", getConfig().selfPfid());
            paramMap.put("live_id", getConfig().liveId());
            paramMap.put("name", getConfig().name());
            paramMap.put("LOCALE", Locale.getDefault().getCountry());
            paramMap.put("access_token", UserInfo.getUserToken(getConfig()));
            if (!TextUtils.isEmpty(getConfig().from())) {
                paramMap.put("from", getConfig().from());
                Log.d(TAG, "[prepare]from：" + getConfig().from());
            }
            String jwts = Jwts.builder().setClaims(paramMap).
                    signWith(SignatureAlgorithm.HS256, getConfig().liveKey().getBytes()).compact();

            obj.put("live_id", getConfig().liveId());
            obj.put("token", jwts);
            obj.put("platform", getConfig().platform());
            obj.put("version", getConfig().appVer());
            if (baseSocket != null && baseSocket.isRelogin()) {
                obj.put("r", 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            obj = null;
        }
        return obj;
    }

    @Override
    public void authBack(boolean success) {
        Config config = getConfig();
        if (success && config != null && config.isRoomChatConnection()) {
            StateChat.setRoomConfig(config);
            if (config.synToState() && (baseSocket == null || !baseSocket.isRelogin())) {
                StateChat.stateChange(Config.isMy(config) ? SocketEvent.enmUserState.enmAnchor : SocketEvent.enmUserState.enmAudience,
                        Config.isMy(config) ? SocketEvent.enmStateAction.enmActionStreaming : SocketEvent.enmStateAction.enmActionNull,
                        config);
            }
        }
    }
}

package com.iandroid.allclass.lib_livechat.base;

import android.text.TextUtils;
import android.util.Log;

import com.iandroid.allclass.lib_livechat.api.Config;
import com.iandroid.allclass.lib_livechat.api.StateChat;
import com.iandroid.allclass.lib_livechat.api.UserInfo;
import com.iandroid.allclass.lib_livechat.conversation.ConversationGetter;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * created by wangkm
 * on 2020/8/6.
 * <p>
 * 状态机
 */
public class StateChatPresenter extends ChatManager {
    public static Object lastStateRoomChangeMsg = null;

    public StateChatPresenter(ISocketEventHandler iSocketEventHandler) {
        super(iSocketEventHandler);
        socket_event_list.add(SocketEvent.EVENT_PRIVATECHAT_OFFICIAL_ULIST);
        socket_event_list.add(SocketEvent.EVENT_PRIVATECHAT_UNOFFICIAL_ULIST);
        socket_event_list.add(SocketEvent.EVENT_PRIVATECHAT_SAID);
        socket_event_list.add(SocketEvent.EVENT_PRIVATECAHT_MLIST);
        socket_event_list.add(SocketEvent.EVENT_PRIVATECHAT_SAY);
        socket_event_list.add(SocketEvent.EVENT_UPDATE_UNREAD_NUM);
        socket_event_list.add(SocketEvent.EVENT_READ_UPDATE);

        socket_event_list.add(SocketEvent.EVENT_CMD);
    }

    @Override
    public void auth() {
        Log.d(TAG, "发起状态机认证");
        send(SocketEvent.EVENT_C2S_AUTHENTICATION, getAuthCode());
    }

    @Override
    public void logout() {
        super.logout();
        lastStateRoomChangeMsg = null;
    }

    @Override
    public Object getAuthCode() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("access_token", UserInfo.getUserToken(getConfig()));
            obj.put("pfid", getConfig().pfid());
            obj.put("platform", getConfig().platform());
            obj.put("version", getConfig().appVer());
            obj.put("user_type", getConfig().userType());
            obj.put("LOCALE", Locale.getDefault().getCountry());
        } catch (Exception e) {
            obj = null;
        }
        return obj;
    }

    @Override
    public void authBack(boolean success) {
        super.authBack(success);
        if (success) {
            //用户上线
            if (lastStateRoomChangeMsg != null) {
                StateChat.stateChange(SocketEvent.enmUserState.enmReloginRoom, SocketEvent.enmStateAction.enmActionNull, null);
            } else {
                StateChat.stateChange(SocketEvent.enmUserState.enmInApp,
                        SocketEvent.enmStateAction.enmActionNull,
                        null);
            }
            fetchAllConversation();
        }
    }

    public void fetchAllConversation() {
        //获取私信回话列表（官方&非官方）
        if (mDisposable != null)
            mDisposable.add(ConversationGetter.getInstance().conversationRequest());
    }

    /**
     * 用户状态更新
     *
     * @param state
     * @param action
     * @param pfid
     * @param config
     */
    public Object stateChange(SocketEvent.enmUserState state,
                              SocketEvent.enmStateAction action,
                              String pfid,
                              Config config) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("s", state.getValue());
            if (!TextUtils.isEmpty(action.getAcion()))
                obj.put("a", action.getAcion());

            if (config != null && !TextUtils.isEmpty(config.liveId())) {
                obj.put("live_id", config.liveId());
            }

            if(config != null && !TextUtils.isEmpty(config.from())){
                obj.put("from", config.from());
            }

            if (!TextUtils.isEmpty(pfid)) {
                obj.put("o_pfid", pfid);
            }
            lastStateRoomChangeMsg = obj;
            send(SocketEvent.EVENT_C2S_STATUS, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public Object stateChangeFromFloatView(SocketEvent.enmUserState state,
                              SocketEvent.enmStateAction action,
                                           Config config) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("s", state.getValue());
            if (!TextUtils.isEmpty(action.getAcion()))
                obj.put("a", action.getAcion());

            obj.put("r", 1);

            if (config != null && !TextUtils.isEmpty(config.liveId())) {
                obj.put("live_id", config.liveId());
            }

            lastStateRoomChangeMsg = obj;
            send(SocketEvent.EVENT_C2S_STATUS, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public Object stateChange(SocketEvent.enmUserState state,
            SocketEvent.enmStateAction action) {
        JSONObject obj = new JSONObject();
        try {
            if (!TextUtils.isEmpty(action.getAcion())) {
                obj.put("s", state.getValue());
                obj.put("a", action.getAcion());

                send(SocketEvent.EVENT_C2S_STATUS, obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void stateReloginRoom() {
        JSONObject obj = new JSONObject();
        if (lastStateRoomChangeMsg != null) {
            try {
                obj = new JSONObject(lastStateRoomChangeMsg.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                obj = new JSONObject();
            }
        }

        try {
            obj.put("r", 1);
            lastStateRoomChangeMsg = obj;
            send(SocketEvent.EVENT_C2S_STATUS, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object handleEventOnIOThread(String event, Object[] original) {
        switch (event) {
            case SocketEvent.EVENT_PRIVATECHAT_UNOFFICIAL_ULIST:
            case SocketEvent.EVENT_PRIVATECHAT_OFFICIAL_ULIST:
                return ConversationGetter.getInstance().conversationResponse(event, original);
        }
        return super.handleEventOnIOThread(event, original);
    }
}

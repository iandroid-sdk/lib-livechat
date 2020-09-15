package com.iandroid.allclass.lib_livechat.base;

import android.text.TextUtils;
import android.util.Log;

import com.iandroid.allclass.lib_livechat.api.Config;
import com.iandroid.allclass.lib_livechat.api.StateChat;
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

    public StateChatPresenter(ISocketEventHandler iSocketEventHandler) {
        super(iSocketEventHandler);
        socket_event_list.add(SocketEvent.EVENT_PRIVATECHAT_OFFICIAL_ULIST);
        socket_event_list.add(SocketEvent.EVENT_PRIVATECHAT_UNOFFICIAL_ULIST);
        socket_event_list.add(SocketEvent.EVENT_PRIVATECHAT_SAID);

    }

    @Override
    public void auth() {
        Log.d(TAG, "发起状态机认证");
        send(SocketEvent.EVENT_C2S_AUTHENTICATION, getAuthCode());
    }

    @Override
    public Object getAuthCode() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("access_token", getConfig().token());
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
            StateChat.stateChange(SocketEvent.enmUserState.enmInApp,
                    SocketEvent.enmStateAction.enmActionNull,
                    null);
            //获取私信回话列表（官方&非官方）
            mDisposable.add(ConversationGetter.getInstance().conversationRequest());
        }
    }

    /**
     * 用户状态更新
     *
     * @param state
     * @param action
     * @param pfid
     * @param config
     */
    public void stateChange(SocketEvent.enmUserState state,
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
            if (!TextUtils.isEmpty(pfid)) {
                obj.put("o_pfid", pfid);
            }
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

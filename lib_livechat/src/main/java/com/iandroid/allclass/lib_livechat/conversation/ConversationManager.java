package com.iandroid.allclass.lib_livechat.conversation;

import android.text.TextUtils;

import com.iandroid.allclass.lib_livechat.api.StateChat;
import com.iandroid.allclass.lib_livechat.base.IStateKeyCallBack;
import com.iandroid.allclass.lib_livechat.bean.ConversationItem;
import com.iandroid.allclass.lib_livechat.bean.ConversationSaidReponse;
import com.iandroid.allclass.lib_livechat.socket.BaseSocket;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * created by wangkm
 * on 2020/8/18.
 * 私信会话数据管理
 */
public class ConversationManager {
    private List<ConversationItem> conversationItemList;

    private static class LazyHolder {
        private static final ConversationManager sInstance = new ConversationManager();
    }

    public ConversationManager() {
        conversationItemList = new ArrayList<>();
    }

    public static ConversationManager getInstance() {
        return ConversationManager.LazyHolder.sInstance;
    }

    public void clearData(){
        if(conversationItemList != null)conversationItemList.clear();
    }
    /**
     * 获取私信会话列表
     */
    public void fetchAllConversation() {
        clearData();
        StateChat.getInstance().fetchAllConversation();
    }

    public List<ConversationItem> getConversationItemList() {
        return conversationItemList;
    }

    /**
     * 添加会话列表
     *
     * @param datalist
     */
    public void addConversations(List<ConversationItem> datalist) {
        for (ConversationItem item : datalist) {
            int index = conversationItemList.indexOf(item);
            item.parse();
            if (index >= 0) {
                item.setUser_info(conversationItemList.get(index).getUser_info());
                conversationItemList.set(index, item);
            } else {
                conversationItemList.add(item);
            }
        }
        Collections.sort(conversationItemList, comparator);
    }

    public void delConversation(ConversationItem item) {
        if (item == null) return;
        //清除未读状态
        clearUnreadMsg(item);
        //服务器删除
        String transcationId = BaseSocket.genChatTransactionId(SocketEvent.IM_HEAD_DEL);
        if (StateChat.getInstance().send(SocketEvent.EVENT_C2S_DELALL,
                genDelAll(item.getPfid(), transcationId))) {
            conversationItemList.remove(item);
        }
        //通知UI删除
        if (StateChat.getiStateKeyCallBack() != null) {
            StateChat.getiStateKeyCallBack().delConversation(item);
        }
    }

    public void updateConversationOnSaid(ConversationSaidReponse conversationSaidReponse,
                                         IStateKeyCallBack iStateKeyCallBack) {
        if (conversationSaidReponse == null || TextUtils.isEmpty(conversationSaidReponse.getPfid()))
            return;

        ConversationItem conversationItem = new ConversationItem();
        conversationItem.setPfid(conversationSaidReponse.getPfid());

        int index = conversationItemList.indexOf(conversationItem);
        boolean isNeedAdd = true;

        if (index >= 0) {
            conversationItem = conversationItemList.get(index);
            isNeedAdd = false;
        }
        conversationItem.setContent(conversationSaidReponse.getContent());
        conversationItem.setIndex(conversationSaidReponse.getIndex());
        conversationItem.setUnread(conversationSaidReponse.getUnread());
        conversationItem.setTs(conversationSaidReponse.getTs());
        conversationItem.parse();

        if (isNeedAdd) {
            conversationItemList.add(conversationItem);
        }
        Collections.sort(conversationItemList, comparator);

        if (iStateKeyCallBack != null) {
            iStateKeyCallBack.onReceiveChat(conversationItem);
            iStateKeyCallBack.updateUnreadMsgNum(null, getTotalUnreadMsgNum());
        }
    }

    /**
     * 更新会话用户信息
     *
     * @param pfid
     * @param userInfo
     */
    public <T> void updateUserInfo(String pfid, T userInfo) {
        if (TextUtils.isEmpty(pfid) || userInfo == null) return;

        for (ConversationItem item : conversationItemList) {
            if (item != null
                    && !TextUtils.isEmpty(item.getPfid())
                    && item.getPfid().equals(pfid)) {
                item.setUser_info(userInfo);
            }
        }
    }

    /**
     * 获取用户信息
     *
     * @param pfid
     * @param <T>
     * @return
     */
    public <T> T getUserInfo(String pfid) {
        if (TextUtils.isEmpty(pfid)) return null;
        T userInfo = null;
        for (ConversationItem item : conversationItemList) {
            if (item != null
                    && !TextUtils.isEmpty(item.getPfid())
                    && item.getPfid().equals(pfid)
                    && item.getUser_info() != null) {
                userInfo = (T) item.getUser_info();
                break;
            }
        }
        if (userInfo == null && StateChat.getiStateKeyCallBack() != null) {
            StateChat.getiStateKeyCallBack().queryUserInfo(pfid);
        }

        return userInfo;
    }

    /**
     * 清除指定用户的未读消息
     */
    public void clearUnreadMsg(ConversationItem conversationItem) {
        if (conversationItem == null) return;
        boolean code = StateChat.getInstance().send(SocketEvent.EVENT_C2S_READ,
                genImRead(conversationItem.getPfid(), BaseSocket.genChatTransactionId(SocketEvent.IM_HEAD_READ), null));
        if (code) {
            resetConversationUnreadInfoByItem(conversationItem);
        }
    }

    private void resetConversationUnreadInfoByItem(ConversationItem conversationItem) {
        if (conversationItem == null) return;
        boolean isNeedUpdate = false;
        for (ConversationItem item : conversationItemList) {
            if (item != null
                    && item.equals(conversationItem)
                    && item.getUnread() > 0) {
                item.setUnread(0);
                isNeedUpdate = true;
            }
        }

        if (isNeedUpdate && StateChat.getiStateKeyCallBack() != null) {
            StateChat.getiStateKeyCallBack().updateUnreadMsgNum(conversationItem, conversationItem.getUnread());
            StateChat.getiStateKeyCallBack().updateUnreadMsgNum(null, getTotalUnreadMsgNum());
        }
    }

    public int getTotalUnreadMsgNum() {
        int total = 0;
        for (ConversationItem item : conversationItemList) {
            if (item != null) {
                total += item.getUnread();
            }
        }
        return total;
    }

    private Comparator comparator = (obj1, obj2) -> {
        ConversationItem s1 = (ConversationItem) obj1;
        ConversationItem s2 = (ConversationItem) obj2;

        if (s1.getTs() > s2.getTs()) {
            return -1;
        } else if (s1.getTs() < s2.getTs()) {
            return 1;
        } else {
            return 0;
        }
    };

    public static JSONObject genImRead(String contact_pfid,
                                       String sid,
                                       String index) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("contact_pfid", contact_pfid);
            obj.put("sid", sid);
            if (!TextUtils.isEmpty(index))
                obj.put("index", index);
        } catch (Exception e) {
            e.printStackTrace();
            obj = null;
        }
        return obj;
    }

    public static JSONObject genDelAll(String pfid, String sid) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("subject", pfid);
            obj.put("sid", sid);
        } catch (Exception e) {
            e.printStackTrace();
            obj = null;
        }
        return obj;
    }
}

package com.iandroid.allclass.lib_livechat.conversation;

import android.text.TextUtils;

import com.iandroid.allclass.lib_livechat.api.StateChat;
import com.iandroid.allclass.lib_livechat.base.IStateKeyCallBack;
import com.iandroid.allclass.lib_livechat.bean.ConversationItem;
import com.iandroid.allclass.lib_livechat.bean.ConversationSaidReponse;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;
import com.iandroid.allclass.lib_livechat.utils.SocketUtils;

import org.json.JSONObject;

import java.util.ArrayList;
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
    public synchronized void addConversations(List<ConversationItem> datalist) {
        for (ConversationItem item : datalist) {
            int index = conversationItemList.indexOf(item);
            item.parse();
            if (item.getPfid() == null || item.getPfid().isEmpty()) continue;
            if (index >= 0) {
                item.setUser_info(conversationItemList.get(index).getUser_info());
                conversationItemList.set(index, item);
            } else {
                conversationItemList.add(item);
            }
        }
    }

    public void delConversation(ConversationItem item) {
        if (item == null) return;
        //清除未读状态
        clearUnreadMsg(item);
        //服务器删除
        String transcationId = SocketUtils.transactionId(SocketEvent.IM_HEAD_DEL);
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
                                         IStateKeyCallBack iStateKeyCallBack,
                                         Boolean isFromOtherSaid) {
        if (conversationSaidReponse == null
                || conversationSaidReponse.getPfid() == null
                || TextUtils.isEmpty(conversationSaidReponse.getPfid()))
            return;

        ConversationItem conversationItem = new ConversationItem();
        conversationItem.setPfid(conversationSaidReponse.getPfid());
        //特殊处理收到自己的消息
        if (StateChat.isMy(conversationSaidReponse.getPfid())
                && !TextUtils.isEmpty(conversationSaidReponse.getTo_pfid())) {
            conversationItem.setPfid(conversationSaidReponse.getTo_pfid());
        }

        int index = conversationItemList.indexOf(conversationItem);
        boolean isNeedAdd = true;

        if (index >= 0) {
            conversationItem = conversationItemList.get(index);
            isNeedAdd = false;
        }
        conversationItem.setContent(conversationSaidReponse.getContent());
        conversationItem.setIndex(conversationSaidReponse.getIndex());
        conversationItem.setUnread(conversationSaidReponse.getUnread());
        conversationItem.setTs(Math.max(conversationSaidReponse.getTs(), conversationItem.getTs()));
        conversationItem.parse();

        if (isNeedAdd) {
            conversationItemList.add(conversationItem);
        }

        if (iStateKeyCallBack != null) {
            iStateKeyCallBack.onReceiveChat(conversationItem, isFromOtherSaid);
            iStateKeyCallBack.updateUnreadMsgNum(null, getTotalUnreadMsgNum());
        }
    }

    /**
     * 更新会话用户信息
     *
     * @param pfid
     * @param userInfo
     */
    public <T> void updateUserInfo(String pfid, T userInfo, boolean isContainer) {
        if (TextUtils.isEmpty(pfid) || userInfo == null) return;

        for (ConversationItem item : conversationItemList) {
            if (item != null
                    && !TextUtils.isEmpty(item.getPfid())
                    && item.getPfid().equals(pfid)) {
                item.setContainer(isContainer);
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
                genImRead(conversationItem.getPfid(), SocketUtils.transactionId(SocketEvent.IM_HEAD_READ), null));
        if (code) {
            resetConversationUnreadInfoByItem(conversationItem);
        }
    }

    public void clearUnreadMsg(String pfid) {
        if (pfid == null) return;
        boolean code = StateChat.getInstance().send(SocketEvent.EVENT_C2S_READ,
                genImRead(pfid, SocketUtils.transactionId(SocketEvent.IM_HEAD_READ), null));
        if (code) {
            resetConversationUnreadInfoByPfid(pfid);
        }
    }

    private void resetConversationUnreadInfoByPfid(String pfid) {
        if (pfid == null) return;
        boolean isNeedUpdate = false;
        ConversationItem conversationItem = null;
        for (ConversationItem item : conversationItemList) {
            if (item != null
                    && pfid.equals(item.getPfid())
                    && item.getUnread() > 0) {
                conversationItem = item;
                item.setUnread(0);
                conversationItem.setUnread(0);
                isNeedUpdate = true;
            }
        }

        if (isNeedUpdate && StateChat.getiStateKeyCallBack() != null) {
            if (conversationItem != null)
                StateChat.getiStateKeyCallBack().updateUnreadMsgNum(conversationItem, conversationItem.getUnread());
            StateChat.getiStateKeyCallBack().updateUnreadMsgNum(null, getTotalUnreadMsgNum());
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
                conversationItem.setUnread(0);
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
            if (item != null && !item.isContainer()) {
                total += item.getUnread();
            }
        }
        return total;
    }

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

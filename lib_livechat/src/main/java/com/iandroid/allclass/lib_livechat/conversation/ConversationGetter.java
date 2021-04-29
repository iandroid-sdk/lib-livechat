package com.iandroid.allclass.lib_livechat.conversation;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.iandroid.allclass.lib_livechat.api.StateChat;
import com.iandroid.allclass.lib_livechat.bean.ConversationList;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;
import com.iandroid.allclass.lib_livechat.utils.SocketUtils;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * created by wangkm
 * on 2020/8/18.
 * 私信会话数据请求
 */
public class ConversationGetter {
    private static final int pagesize = 40;
    private int curOfficailConversationIndex = 0;
    private int curUserConversationIndex = 0;

    private int nextOfficailConversationIndex = 0;
    private int nextUserConversationIndex = 0;
    private final int maxRetryTime = 10;

    //获取会话列表
    public Disposable conversationRequest() {
        curOfficailConversationIndex = 0;
        curUserConversationIndex = 0;
        nextOfficailConversationIndex = 0;
        nextUserConversationIndex = 0;
        return Observable.just(1)
                .subscribe(integer -> {
                    pullUList(true);
                    pullUList(false);
                });
    }

    private void pullUList(boolean isOfficial) {
        if (isOfficial) {
            if (curOfficailConversationIndex <= nextOfficailConversationIndex)
                StateChat.getInstance().send(SocketEvent.EVENT_C2S_OFFICIAL_ULIST,
                        getConversationRequestParam(nextOfficailConversationIndex,
                                pagesize,
                                SocketUtils.transactionId(String.valueOf(nextOfficailConversationIndex))));
        } else if (curUserConversationIndex <= nextUserConversationIndex) {
            StateChat.getInstance().send(SocketEvent.EVENT_C2S_UNOFFICIAL_ULIST,
                    getConversationRequestParam(nextUserConversationIndex,
                            pagesize,
                            SocketUtils.transactionId(String.valueOf(nextUserConversationIndex))));

        }
    }

    private static JSONObject getConversationRequestParam(int start, int count, String sid) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("start", start);
            obj.put("count", count);
            obj.put("sid", sid);
        } catch (Exception e) {
            e.printStackTrace();
            obj = null;
        }
        return obj;
    }

    private boolean isEndOfConversation() {
        return curOfficailConversationIndex > nextOfficailConversationIndex
                && curUserConversationIndex > nextUserConversationIndex;
    }

    public Object conversationResponse(String event, Object[] original) {
        if (original == null || original.length <= 0) return null;

        ConversationList conversationList = null;
        try {
            conversationList = JSON.parseObject(original[0].toString(), ConversationList.class);
            if (conversationList != null && conversationList.getResult() != null)
                ConversationManager.getInstance().addConversations(conversationList.getResult());

            int responsePagesize = conversationList.getResult() != null ? conversationList.getResult().size() : 0;
            int responsePageIndex = SocketUtils.toInt(conversationList.getSid().substring(0, conversationList.getSid().indexOf("_")), 0);
            //hasMore 为true 可能还有更多数据，需要继续请求下一页
            boolean hasMore = responsePagesize >= pagesize;
            boolean isFromOfficalConversation = TextUtils.equals(event, SocketEvent.EVENT_PRIVATECHAT_OFFICIAL_ULIST);
            if (isFromOfficalConversation) {
                if (hasMore) {
                    nextOfficailConversationIndex = ++responsePageIndex;
                    curOfficailConversationIndex = nextOfficailConversationIndex;
                    pullUList(true);
                    //Log.d("lang_socket", "officialConversation hasmore, nextpage:" + (nextOfficalConversationIndex));
                } else {
                    Log.d("lang_socket", "[Conversation]official isEnd, totalPage:" + (responsePageIndex + 1));
                    curOfficailConversationIndex++;
                }
            } else {
                if (hasMore) {
                    nextUserConversationIndex = ++responsePageIndex;
                    curUserConversationIndex = nextUserConversationIndex;
                    pullUList(false);
                    //Log.d("lang_socket", "userConversation hasmore, nextpage:" + (nextUserConversationIndex));
                } else {
                    Log.d("lang_socket", "[Conversation]user isEnd, totalPage:" + (responsePageIndex + 1));
                    curUserConversationIndex++;
                }
            }

            if (isEndOfConversation()){
                Log.d("lang_socket", "[conversation]Request finally");
                StateChat.getInstance().conversationLoadSuccess();
            }

        } catch (Exception e) {
            Log.d("lang_socket", "[Conversation]Exception:" + e);
        }
        return conversationList;
    }

    private static class LazyHolder {
        private static final ConversationGetter sInstance = new ConversationGetter();
    }

    public static ConversationGetter getInstance() {
        return ConversationGetter.LazyHolder.sInstance;
    }
}

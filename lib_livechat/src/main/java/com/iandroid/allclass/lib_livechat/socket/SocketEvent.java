package com.iandroid.allclass.lib_livechat.socket;

/**
 * created by wangkm
 * on 2020/8/6.
 */
public class SocketEvent {
    //socketEvent
    public static final String EVENT_AUTHENTICATED = "authenticated";
    public static final String EVENT_UNAUTHENTICATED = "unauthorized";
    public static final String EVENT_CLOSE = "close";

    //room
    public static final String EVENT_JOIN = "join";

    // C2S event
    public static final String EVENT_C2S_AUTHENTICATION = "authentication";
    public static final String EVENT_C2S_STATUS = "status";

    //私信 C2S
    public static final String EVENT_PRIVATECHAT_SAY = "pmsg/v1/say";
    public static final String EVENT_PRIVATECHAT_SAID = "pmsg/v1/said";
    public static final String EVENT_C2S_OFFICIAL_ULIST = "pmsg/v1/ulist_official";
    public static final String EVENT_C2S_UNOFFICIAL_ULIST = "pmsg/v1/ulist_unofficial";
    public static final String EVENT_C2S_READ = "pmsg/v1/read";
    public static final String EVENT_C2S_DELALL = "pmsg/v1/delall";
    public static final String EVENT_C2S_SINGLELIST = "pmsg/v1/msglist";

    //room C2S
    public static final String EVENT_C2S_CHAT_MSG = "msg";

    //S2C
    public static final String EVENT_PRIVATECHAT_OFFICIAL_ULIST = "pmsg/v1/ulist_official";
    public static final String EVENT_PRIVATECHAT_UNOFFICIAL_ULIST = "pmsg/v1/ulist_unofficial";
    public static final String EVENT_PRIVATECAHT_MLIST = "pmsg/v1/msglist";

    //msg tag
    public static final String IM_HEAD_READ = "read";
    public static final String IM_HEAD_DEL = "del";
    public static final String IM_HEAD_SINGLE_LIST = "single";

    public static final int PAGE_SIZE = 40;

    // state
    public enum enmUserState {
        enmInApp(1),
        enmAudience(2),
        enmAnchor(3),
        enmExit(-1);

        private int value;

        public int getValue() {
            return value;
        }

        enmUserState(int value) {
            this.value = value;
        }
    }

    //action
    public enum enmStateAction {
        enmActionNull(""),
        enmActionStreaming("A0"),
        enmActionLeaveRoom("A1"),
        enmActionAnchorPause("A6"),
        enmActionAnchorBack("A7"),
        enmActionFollow("A8"),
        enmActionCancelFollow("A9"),
        enmActionFirstSendGiftEnterRoom("A10");
        private String acion;

        enmStateAction(String acion) {
            this.acion = acion;
        }

        public String getAcion() {
            return acion;
        }
    }
}

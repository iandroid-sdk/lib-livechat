package com.iandroid.allclass.liveChat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.iandroid.allclass.bean.RoomEvent;
import com.iandroid.allclass.bean.RoomInfo;
import com.iandroid.allclass.bean.UserInfo;
import com.iandroid.allclass.lib_livechat.api.Config;
import com.iandroid.allclass.lib_livechat.api.RoomChat;
import com.iandroid.allclass.lib_livechat.api.StateChat;
import com.iandroid.allclass.lib_livechat.base.ISocketEventHandler;
import com.iandroid.allclass.lib_livechat.base.IStateKeyCallBack;
import com.iandroid.allclass.lib_livechat.bean.ConversationItem;
import com.iandroid.allclass.lib_livechat.exception.LoginException;
import com.iandroid.allclass.lib_livechat.socket.SocketEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements ISocketEventHandler , IStateKeyCallBack {
    private UserInfo loginInfo;
    private String tag = "lang_socket";
    private List<RoomInfo> roomInfoList;
    private String userinfo = "{\n" +
            "\t\"pfid\": 1018462,\n" +
            "\t\"pretty_id\": \"1018462\",\n" +
            "\t\"access_token\": \"d23eb0f4ee40a7feaa26dd7315ec0ea8\",\n" +
            "\t\"headimg\": \"http:\\/\\/blob.ufile.ucloud.com.cn\\/7b8ac86b2fa9a436841137768f2234c3e80ff5b8f675c6aa7d2f6eca7661dc22200410152004.jpg\",\n" +
            "\t\"nickname\": \"蚕.丞相\",\n" +
            "\t\"sex\": 1,\n" +
            "\t\"noble_exp\": 123544,\n" +
            "\t\"before_noble_exp\": 49507,\n" +
            "\t\"after_noble_exp\": 124807,\n" +
            "\t\"lvl\": 41,\n" +
            "\t\"sign\": \"明朝即长路，惜取此时心\",\n" +
            "\t\"send\": 114351,\n" +
            "\t\"income\": 60732,\n" +
            "\t\"balance\": 1,\n" +
            "\t\"sun\": 4128,\n" +
            "\t\"data_complete\": 1,\n" +
            "\t\"last_login_time\": 1597735272000,\n" +
            "\t\"sex_modified\": 1,\n" +
            "\t\"constellation\": \"摩羯座\",\n" +
            "\t\"award_name\": \"\",\n" +
            "\t\"award_small_icon\": \"\",\n" +
            "\t\"invite_id\": 0,\n" +
            "\t\"can_bind_invite\": 0,\n" +
            "\t\"mobile_phone\": \"8613671651167\",\n" +
            "\t\"location\": \"南投縣\",\n" +
            "\t\"birthday\": \"\",\n" +
            "\t\"birthday_modified\": 0,\n" +
            "\t\"year_protected\": 0,\n" +
            "\t\"anchor_lvl\": 23,\n" +
            "\t\"anchor_gid\": 1,\n" +
            "\t\"anchor_glvl\": 7,\n" +
            "\t\"grade_id\": 2,\n" +
            "\t\"grade_lvl\": 22,\n" +
            "\t\"ugid\": 2,\n" +
            "\t\"uglv\": 55,\n" +
            "\t\"tourist\": 0,\n" +
            "\t\"reg_date\": 1478094074000,\n" +
            "\t\"headimg_web\": \"https:\\/\\/static.kingkong.com.tw\\/public\\/user\\/1018462\\/1018462\",\n" +
            "\t\"balance_noble\": 0,\n" +
            "\t\"nlv\": 0,\n" +
            "\t\"noble_identity_hide\": 0,\n" +
            "\t\"nst\": 0,\n" +
            "\t\"isVip\": 0,\n" +
            "\t\"uta\": 0,\n" +
            "\t\"lang_fans\": 0,\n" +
            "\t\"medal\": \"\",\n" +
            "\t\"medal_act\": \"\",\n" +
            "\t\"a_ic\": \"\",\n" +
            "\t\"enter_room_notice\": 0,\n" +
            "\t\"show_invite\": false,\n" +
            "\t\"langq_code\": \"\"\n" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginInfo = new Gson().fromJson(userinfo, UserInfo.class);
        findViewById(R.id.id_btn_login_statemac).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginStateMac();
            }
        });

        findViewById(R.id.id_btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        findViewById(R.id.id_btn_switchroom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchRoom();
            }
        });

        findViewById(R.id.id_btn_logout_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutRoom();
            }
        });
        roomInfoList = Arrays.asList(new RoomInfo("4507613", "4507613Y196719Tkf", "7BnKIQ"),
                new RoomInfo("1797163", "1797163Y03848IEWQ", "x8SaZm"));
    }
//2020-08-19 17:19:42.049 27583-28224/com.lang.lang D/RoomSocket: roomcache:RoomCacheData{roomTrace=null, pfid='4507613', live_key='7BnKIQ', live_id='4507613Y196719Tkf', prs_id=''}

    private void loginStateMac() {
        Log.d(tag, "登录状态机");

        try {
            StateChat.login(Config.builder()
                    .appVer("5.0.8")
                    .selfPfid(loginInfo.pfid)
                    .pfid(loginInfo.pfid)
                    .platform("android")
                    .userType("user")
                    .stateKeyCallBack(this)
                    .token(loginInfo.access_token)
                    .tag(tag)
                    .hosts(Arrays.asList("https://state-1.lv-show.com/state",
                            "https://state-2.lv-show.com/state",
                            "https://state-3.lv-show.com/state"))
                    .build());
        } catch (LoginException e) {
            e.printStackTrace();
            Log.e(tag, "LoginException:" + e.getMessage());
        }
    }

    @Override
    public void logout() {
        Log.d(tag, "登出状态机");
        StateChat.logout();
        if (roomChat != null) roomChat.logoutRoom();
    }

    @Override
    public void conversationLoadSuccess() {

    }

    @Override
    public void onReceiveChat(ConversationItem conversationItem) {

    }

    @Override
    public void updateUnreadMsgNum(ConversationItem conversationItem, int num) {

    }

    @Override
    public void delConversation(ConversationItem conversationItem) {

    }

    @Override
    public void queryUserInfo(String pifd) {

    }

    @Override
    public void statusCallback(SocketEvent.enmSocketStatus status) {

    }

    @Override
    public void tickOut() {

    }

    private RoomChat roomChat;

    private void logoutRoom() {
        if (roomChat != null) roomChat.logoutRoom();
    }

    private void switchRoom() {
        if (roomChat == null) roomChat = new RoomChat();
        roomChat.loginRoom(Config.builder()
                .appVer("5.0.8")
                .platform("android")
                .tag(tag)
                .selfPfid(loginInfo.pfid)
                .name(loginInfo.nickname)
                .token(loginInfo.access_token)
                .pfid(roomInfoList.get(0).pifd)
                .event_list(new ArrayList<>(Arrays.asList(RoomEvent.EVENT_PERSON_CUSTOMIZE,
                        RoomEvent.EVENT_ROOM_CUSTOMIZE)))
                .socketEventHandler(this)
                .liveId(roomInfoList.get(0).live_id)
                .liveKey(roomInfoList.get(0).live_key)
                .ctl_server_list(Arrays.asList("https://ctl-2.lv-show.com/control_nsp",
                        "https://ctl.lv-show.com/control_nsp",
                        "https://ctl-q1.lv-show.com/control_nsp",
                        "https://ctl-q2.lv-show.com/control_nsp",
                        "https://ctl-q3.lv-show.com/control_nsp",
                        "https://ctl-q4.lv-show.com/control_nsp",
                        "https://ctl-q5.lv-show.com/control_nsp"))
                .cht_server_list(Arrays.asList("https://cht.lv-show.com/chat_nsp",
                        "https://cht-2.lv-show.com/chat_nsp",
                        "https://cht-q1.lv-show.com/chat_nsp",
                        "https://cht-q2.lv-show.com/chat_nsp",
                        "https://cht-q3.lv-show.com/chat_nsp"))
                .build());
    }

    @Override
    public Object onMsgParse(String event, Object[] original) {
        Log.d(tag, "[Room-UI], onMsgParse event:" + event);
        return null;
    }

    @Override
    public void onReceiveMsg(String event, Object[] originalData, Object eventData) {
        Log.d(tag, "[Room-UI], onReceiveMsg event:" + event);

    }

//2020-08-19 13:49:39.552 13917-14627/com.lang.lang D/RoomSocket: roomcache:RoomCacheData{roomTrace=RoomTrace{from='', from_seq='', channel_id='', area_id='null'}, pfid='3687493', live_key='Fm3H1i', live_id='3687493Y09885Iv4L', prs_id=''}
//2020-08-19 13:48:10.725 12231-13488/com.lang.lang D/RoomSocket: roomcache:RoomCacheData{roomTrace=null, pfid='1797163', live_key='x8SaZm', live_id='1797163Y03848IEWQ', prs_id=''}
    //  2020-08-19 13:48:42.786 12231-13733/com.lang.lang D/RoomSocket: roomcache:RoomCacheData{roomTrace=null, pfid='4098922', live_key='38RIQL', live_id='4098922Y12920acIr', prs_id=''}
//2020-08-19 13:49:04.999 12231-13888/com.lang.lang D/RoomSocket: roomcache:RoomCacheData{roomTrace=null, pfid='1497127', live_key='c1J49K', live_id='1497127Aa10268eUM', prs_id=''}
    //2020-08-19 13:49:22.791 13917-14458/com.lang.lang D/RoomSocket: roomcache:RoomCacheData{roomTrace=null, pfid='4722853', live_key='9hwsik', live_id='4722853Y08264o1sw', prs_id=''}

}

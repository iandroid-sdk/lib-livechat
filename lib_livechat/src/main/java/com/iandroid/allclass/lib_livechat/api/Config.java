package com.iandroid.allclass.lib_livechat.api;

import android.text.TextUtils;

import com.google.auto.value.AutoValue;
import com.iandroid.allclass.lib_livechat.base.ISocketEventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * created by wangkm
 * on 2020/8/6.
 */
@AutoValue
public abstract class Config {
    @Nullable
    public abstract String name();

    @Nullable
    public abstract String liveKey();

    @Nullable
    public abstract String liveId();

    public abstract String userType();

    @NonNull
    public abstract String appVer();

    @NonNull
    public abstract String platform();

    @NonNull
    public abstract String token();

    @NonNull
    public abstract String selfPfid();

    @NonNull
    public abstract String pfid();

    @Nullable
    public abstract String host();

    public abstract List<String> hosts();

    public abstract int reconnectionAttempts();

    public abstract boolean isRoomChatConnection();

    public abstract int reconnectionDelay();

    public abstract int reconnectionDelayMax();

    @Nullable
    public abstract String from();

    @Nullable
    public abstract String fromSeq();

    @Nullable
    public abstract String channelId();

    @Nullable
    public abstract String area();

    @Nullable
    public abstract String tag();

    @Nullable
    public abstract ISocketEventHandler socketEventHandler();

    public abstract List<String> ctl_server_list();

    public abstract List<String> cht_server_list();

    public abstract ArrayList<String> event_list();

    public static boolean enableLog() {
        return true;
    }

    public static boolean isMy(Config config) {
        return config != null
                && !TextUtils.isEmpty(config.pfid())
                && !TextUtils.isEmpty(config.selfPfid())
                && TextUtils.equals(config.pfid(), config.selfPfid());
    }

    public static Builder builder() {
        return new AutoValue_Config.Builder()
                .appVer("1.0.0")
                .tag("LangSocket")
                .hosts(Arrays.asList(""))
                .cht_server_list(Arrays.asList(""))
                .ctl_server_list(Arrays.asList(""))
                .event_list(new ArrayList<String>())
                .host("")
                .token("")
                .pfid("")
                .selfPfid("")
                .area("")
                .channelId("")
                .fromSeq("")
                .name("")
                .isRoomChatConnection(false)
                .socketEventHandler(null)
                .liveId("")
                .liveKey("")
                .platform("Android")
                .userType("user")
                .reconnectionAttempts(10)
                .reconnectionDelayMax(60)
                .reconnectionDelay(10);
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder name(String name);

        public abstract Builder liveId(String liveId);

        public abstract Builder liveKey(String liveKey);

        public abstract Builder userType(String userType);

        public abstract Builder appVer(String appVer);

        public abstract Builder platform(String platform);

        public abstract Builder token(String token);

        public abstract Builder selfPfid(String selfPfid);

        public abstract Builder pfid(String pfid);

        public abstract Builder hosts(List<String> hosts);

        public abstract Builder host(String host);

        public abstract Builder reconnectionAttempts(int reconnectionAttempts);

        public abstract Builder reconnectionDelay(int reconnectionDelay);

        public abstract Builder reconnectionDelayMax(int reconnectionDelayMax);

        public abstract Builder from(String from);

        public abstract Builder fromSeq(String fromSeq);

        public abstract Builder channelId(String channelId);

        public abstract Builder area(String area);

        public abstract Builder socketEventHandler(ISocketEventHandler socketEventHandler);

        public abstract Builder tag(String tag);

        public abstract Builder ctl_server_list(List<String> ctl_server_list);

        public abstract Builder cht_server_list(List<String> cht_server_list);

        public abstract Builder isRoomChatConnection(boolean isRoomChatConnection);

        public abstract Builder event_list(ArrayList<String> event_list);

        public abstract Config build();
    }
}

package com.iandroid.allclass.lib_livechat.api;

import android.text.TextUtils;

/**
 * created by wangkm
 * on 2021/1/21.
 */
public class UserInfo {
    public static String userToken = null;

    public static String getUserToken(Config config) {
        if (config == null) return TextUtils.isEmpty(userToken) ? "" : userToken;

        if (TextUtils.isEmpty(userToken)) return config.token();

        return userToken;
    }
}

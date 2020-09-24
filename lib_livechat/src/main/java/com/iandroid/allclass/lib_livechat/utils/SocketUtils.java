package com.iandroid.allclass.lib_livechat.utils;

import android.text.TextUtils;

import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * created by wangkm
 * on 2020/8/6.
 */
public class SocketUtils {
    private static long sSid;
    public static SSLContext getSSLContext() {
        try {
            String sslProtocol = "TLSv1.2";
            SSLContext sslContext;
            sslContext = SSLContext.getInstance(sslProtocol);
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }

                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) {
                }
            }};
            sslContext.init(null, trustAllCerts, null);
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String transactionId(String head) {
        if (sSid == 0) {
            Date now = new Date();
            sSid = now.getTime();
        }
        sSid++;
        if (head == null)
            head = "";
        return new StringBuffer(head).append("_").append(sSid).toString();
    }

    public static int toInt(String str, int defValue) {
        if (TextUtils.isEmpty(str))
            return defValue;
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    public static boolean isJson(String param) {
        boolean b = isBlank(param);
        if (!b) {
            return param.startsWith("{") || param.startsWith("[");
        }

        return false;
    }

    public static boolean isBlank(String param) {
        return param == null || param.trim().equals("") || param.trim().equals("null");
    }
}

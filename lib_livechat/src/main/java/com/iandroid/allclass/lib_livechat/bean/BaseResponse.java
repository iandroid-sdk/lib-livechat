package com.iandroid.allclass.lib_livechat.bean;

/**
 * created by wangkm
 * on 2020/9/15.
 */
public class BaseResponse {
    private String sid;
    private int ret_code;
    private String ret_msg;
    private long at;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public int getRet_code() {
        return ret_code;
    }

    public void setRet_code(int ret_code) {
        this.ret_code = ret_code;
    }

    public String getRet_msg() {
        return ret_msg;
    }

    public void setRet_msg(String ret_msg) {
        this.ret_msg = ret_msg;
    }

    public long getAt() {
        return at;
    }

    public void setAt(long at) {
        this.at = at;
    }
}
